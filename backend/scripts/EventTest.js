import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 50, // 가상 사용자 수
    duration: '30s', // 테스트 지속 시간
};

export default function () {
    const baseUrl = 'http://localhost:8080';

    // 홀수 계정으로 로그인 (InitUser1, InitUser3, ..., InitUser29)
    let oddUserId = __VU * 2 - 1; // 홀수 계정 ID 계산 (1, 3, 5, ..., 29)
    let oddLoginPayload = JSON.stringify({
        email: `initUser${oddUserId}@example.com`, // 홀수 계정 이메일
        password: 'testPassword123!', // 홀수 계정 비밀번호
    });
    let oddLoginResponse = http.post(`${baseUrl}/api/auth/login`, oddLoginPayload, {
        headers: { 'Content-Type': 'application/json' },
    });
    check(oddLoginResponse, {
        'Odd Account Login Status is 200': (r) => r.status === 200,
    });

    // 홀수 계정의 accessToken 추출
    let oddAccessToken = oddLoginResponse.json('data.accessToken');

    // SSE 채널 열기
    let sseResponse = http.get(`${baseUrl}/api/sse`, {
        headers: {
            'Authorization': `Bearer ${oddAccessToken}`,
        },
    });
    check(sseResponse, {
        'SSE Channel Open Status is 200': (r) => r.status === 200,
    });

    // 짝수 계정으로 로그인 (InitUser2, InitUser4, ..., InitUser30)
    let evenUserId = __VU * 2; // 짝수 계정 ID 계산 (2, 4, 6, ..., 30)
    let evenLoginPayload = JSON.stringify({
        email: `initUser${evenUserId}@example.com`, // 짝수 계정 이메일
        password: 'testPassword123!', // 짝수 계정 비밀번호
    });
    let evenLoginResponse = http.post(`${baseUrl}/api/auth/login`, evenLoginPayload, {
        headers: { 'Content-Type': 'application/json' },
    });
    check(evenLoginResponse, {
        'Even Account Login Status is 200': (r) => r.status === 200,
    });

    // 짝수 계정의 accessToken 추출
    let evenAccessToken = evenLoginResponse.json('data.accessToken');

    // 짝수 계정이 like 전송
    let likeResponse = http.post(`${baseUrl}/api/likes`, {
        targetUserId: oddUserId, // 홀수 계정에게 like 전송
    }, {
        headers: {
            'Authorization': `Bearer ${evenAccessToken}`,
            'Content-Type': 'application/json',
        },
    });
    check(likeResponse, {
        'Like Send Status is 200': (r) => r.status === 200,
    });

    sleep(1); // 대기
}