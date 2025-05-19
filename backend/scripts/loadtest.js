import http from 'k6/http';
import { check,sleep } from 'k6';

export let options = {
    vus: 200,            // 💥 최대 동시 접속자 수 (동시에 200명 부하)
    duration: '30s',      // ⏱️ 1분간 지속
};

export default function () {
    const res = http.get('http://localhost:8080/api/monitoring/health'); // 인증 없는 API 경로

    check(res, {
        'Health check is successful': (r) => r.status === 200,
        'Response message is correct': (r) => r.body === 'Server is running!',
    });

    sleep(1);
}