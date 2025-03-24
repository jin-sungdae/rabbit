import axios from 'axios'

// 기본 Axios 인스턴스 생성
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:1113/api/v1', // 환경변수 또는 기본값
  timeout: 10000, // 10초 타임아웃
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// 요청 인터셉터 (필요시 인증 토큰 등 추가)
api.interceptors.request.use(
  (config) => {
    // 예: localStorage에 저장된 토큰 자동 부착
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 응답 인터셉터 (필요 시 전역 오류 처리 가능)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // 예: 401 오류 발생 시 로그인 페이지로 이동
    if (error.response && error.response.status === 401) {
      console.warn('인증 오류 발생')
      // window.location.href = '/login' // 자동 리디렉션 예시
    }
    return Promise.reject(error)
  }
)

export default api