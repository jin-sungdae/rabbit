// load-test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 50, // 동시 사용자 수
  duration: '10s', // 테스트 시간
};

export default function () {
  const payload = JSON.stringify({
    userId: 'testuser',
    userPassword: 'invalid'
  });

  const headers = {
    'Content-Type': 'application/json'
  };

  const res = http.post('http://localhost/api/v1/user/login', payload, { headers });

  check(res, {
    'status is 200 or 429': (r) => r.status === 200 || r.status === 429,
  });

  sleep(1);
}