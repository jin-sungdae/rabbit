import http from 'k6/http'
import { check } from 'k6'

export const options = {
  vus: 10,
  duration: '10s',
}

export default function () {
  const res = http.post('http://localhost/api/v1/user/login', JSON.stringify({
    userId: 'testuser',
    userPassword: 'password'
  }), {
    headers: { 'Content-Type': 'application/json' }
  })

  check(res, {
    'is status 200 or 429': (r) => r.status === 200 || r.status === 429,
  })
}