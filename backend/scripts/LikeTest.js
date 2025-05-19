import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 50, // 가상 사용자 수
    duration: '30s', // 테스트 지속 시간
};

export default function () {
    const baseUrl = 'http://localhost:8080';

    // 유저 ID 계산
    const userId = __VU;

    // initUser1은 SSE 채널 열기 (오직 VU 1만)
    if (userId === 1) {
        // 로그인
        let loginPayload = JSON.stringify({
            email: `initUser${userId}@example.com`,
            password: 'testPassword123!',
        });
        const loginRes = http.post(`${baseUrl}/api/auth/login`, loginPayload, {
            headers: { 'Content-Type': 'application/json' },
        });
        check(loginRes, { 'Login is 200': (r) => r.status === 200 });
        const accessToken = loginRes.json('data.accessToken');

        // SSE 채널 열기 (계속 연결 유지)
        let sseRes = http.get(`${baseUrl}/api/sse`, {
            headers: { 'Authorization': `Bearer ${accessToken}` },
        });
        check(sseRes, { 'SSE channel open success': (r) => r.status === 200 });
        // 여기서 그냥 무한 루프에 넣거나 연결 유지하는 방식 필요 (k6에서는 제한적)
        // 또는 일정 시간 동안 요청을 반복하는 것으로 대체
        for (let i = 0; i < 30; i++) {
            sleep(1);
        }
        return;
    }

    // 나머지 VU들은 좋아요 요청만
    const evenUserId = userId % 2 === 0 ? userId : userId + 1; // 짝수 계정 ID
    let loginRes = http.post(`${baseUrl}/api/auth/login`, JSON.stringify({
        email: `initUser${evenUserId}@example.com`,
        password: 'testPassword123!',
    }), { headers: { 'Content-Type': 'application/json' } });
    check(loginRes, { 'Login is 200': (r) => r.status === 200 });
    let evenAccessToken = loginRes.json('data.accessToken');

    // 좋아요 보내기
    let likeRes = http.post(`${baseUrl}/api/likes`, {
        targetUserId: __VU === 1 ? 1 : 1, // 모두 initUser1에게 좋아요 (혹은 다른 대상)
    }, {
        headers: {
            'Authorization': `Bearer ${evenAccessToken}`,
            'Content-Type': 'application/json'
        }
    });
    check(likeRes, { 'Like sent': (r) => r.status === 200 });

    sleep(1);
}