import http from 'k6/http';
import { sleep, check } from 'k6';
import { Trend } from 'k6/metrics';

// 평균 응답 시간을 추적하기 위한 Trend 객체 생성
const responseTime = new Trend('response_time');

export const options = {
    vus: 100,
    duration: "10s",
};

export default function () {
    const res = http.get('https://api.app.mm.ts0608.life/api/monitoring/health');
    responseTime.add(res.timings.duration);
    sleep(1);
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    };
}