#!/bin/bash

# 가상 메모리 4GB 설정
sudo dd if=/dev/zero of=/swapfile bs=128M count=32
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo sh -c 'echo "/swapfile swap swap defaults 0 0" >> /etc/fstab'

# 도커 설치 및 실행/활성화
yum install docker -y
systemctl enable docker
systemctl start docker

# 도커 네트워크 생성
docker network create common

# Nginx Proxy Manager 설치
docker run -d \
  --name npm_1 \
  --restart unless-stopped \
  --network common \
  -p 80:80 \
  -p 443:443 \
  -p 81:81 \
  -e TZ=Asia/Seoul \
  -v /dockerProjects/npm_1/volumes/data:/data \
  -v /dockerProjects/npm_1/volumes/etc/letsencrypt:/etc/letsencrypt \
  jc21/nginx-proxy-manager:latest

# HAProxy 설정 디렉토리 생성
mkdir -p /dockerProjects/ha_proxy_1/volumes/usr/local/etc/haproxy/lua

# Lua 스크립트 생성
cat << 'EOF' > /dockerProjects/ha_proxy_1/volumes/usr/local/etc/haproxy/lua/retry_on_502_504.lua
core.register_action("retry_on_502_504", { "http-res" }, function(txn)
  local status = txn.sf:status()
  if status == 502 or status == 504 then
    txn:Done()
  end
end)
EOF

# HAProxy 설정 파일 생성
cat << 'EOF' > /dockerProjects/ha_proxy_1/volumes/usr/local/etc/haproxy/haproxy.cfg
global
    lua-load /usr/local/etc/haproxy/lua/retry_on_502_504.lua

resolvers docker
    nameserver dns1 127.0.0.11:53
    resolve_retries       3
    timeout retry         1s
    hold valid            10s

defaults
    mode http
    timeout connect 5s
    timeout client 60s
    timeout server 60s

frontend http_front
    bind *:80
    acl host_app1 hdr_beg(host) -i api.glog.oa.gg
    use_backend http_back_1 if host_app1

backend http_back_1
    balance roundrobin
    option httpchk GET /actuator/health
    default-server inter 2s rise 1 fall 1 init-addr last,libc,none resolvers docker
    option redispatch
    http-response lua.retry_on_502_504
    server app_server_1_1 app1_1:8080 check
    server app_server_1_2 app1_2:8080 check
EOF

# HAProxy 실행
docker run \
  -d \
  --network common \
  -p 8090:80 \
  -v /dockerProjects/ha_proxy_1/volumes/usr/local/etc/haproxy:/usr/local/etc/haproxy \
  -e TZ=Asia/Seoul \
  --name ha_proxy_1 \
  haproxy

# Redis 실행
docker run -d \
  --name=redis_1 \
  --restart unless-stopped \
  --network common \
  -p 6379:6379 \
  -e TZ=Asia/Seoul \
  redis

# MySQL 실행
docker run -d \
  --name mysql_1 \
  --restart unless-stopped \
  -v /dockerProjects/mysql_1/volumes/var/lib/mysql:/var/lib/mysql \
  -v /dockerProjects/mysql_1/volumes/etc/mysql/conf.d:/etc/mysql/conf.d \
  --network common \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=0913 \
  -e TZ=Asia/Seoul \
  mysql:latest

# MySQL 초기화 대기 및 스크립트 실행
echo "MySQL이 기동될 때까지 대기 중..."
until docker exec mysql_1 mysql -uroot -p0913 -e "SELECT 1" &> /dev/null; do
  echo "MySQL이 아직 준비되지 않음. 5초 후 재시도..."
  sleep 5
done
echo "MySQL이 준비됨. 초기화 스크립트 실행 중..."

docker exec mysql_1 mysql -uroot -p0913 -e "
CREATE USER 'lldjlocal'@'127.0.0.1' IDENTIFIED WITH caching_sha2_password BY '1234';
CREATE USER 'lldjlocal'@'172.18.%.%' IDENTIFIED WITH caching_sha2_password BY '1234';
CREATE USER 'lldj'@'%' IDENTIFIED WITH caching_sha2_password BY 'lldj123414';

GRANT ALL PRIVILEGES ON *.* TO 'lldjlocal'@'127.0.0.1';
GRANT ALL PRIVILEGES ON *.* TO 'lldjlocal'@'172.18.%.%';
GRANT ALL PRIVILEGES ON *.* TO 'lldj'@'%';

CREATE DATABASE moviematch;
FLUSH PRIVILEGES;
"
# Grafana 데이터 저장 디렉토리 생성
mkdir -p /dockerProjects/grafana/volumes/var/lib/grafana
chown -R 472:472 /dockerProjects/grafana/volumes/var/lib/grafana

# Grafana 실행
docker run -d \
  --name grafana \
  --restart unless-stopped \
  --network common \
  -p 3000:3000 \
  -v /dockerProjects/grafana/volumes/var/lib/grafana:/var/lib/grafana \
  grafana/grafana

# Prometheus 설정 디렉토리 및 설정 파일 생성
mkdir -p /dockerProjects/prometheus/volumes/etc/prometheus

cat << 'EOF' > /dockerProjects/prometheus/volumes/etc/prometheus/prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']
EOF

# Prometheus 실행
docker run -d \
  --name prometheus \
  --restart unless-stopped \
  --network common \
  -p 9090:9090 \
  -v /dockerProjects/prometheus/volumes/etc/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus

# Redis Exporter 실행
docker run -d \
  --name redis-exporter \
  --restart unless-stopped \
  --network common \
  -p 9121:9121 \
  -e REDIS_ADDR=redis://redis_1:6379 \
  oliver006/redis_exporter

# GitHub 패키지 레지스트리 로그인
echo "${var.github_access_token_1}" | docker login ghcr.io -u ${var.github_access_token_1_owner} --password-stdin
