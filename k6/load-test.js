import http from 'k6/http'
import { check, sleep } from 'k6'

export const options = {
  vus: 50, // 동시 사용자 수
  duration: '10s', // 부하 지속 시간
}

export default function () {
  const res = http.get('http://localhost:80/api/v1/user/test') // 테스트 대상 URI

  check(res, {
    'status is 200': (r) => r.status === 200,
    'status is 429 (rate limited)': (r) => r.status === 429,
  })

  sleep(0.1)
}
