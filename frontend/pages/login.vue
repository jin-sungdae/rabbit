<template>
  <div class="home-page">
    <h1>로그인 페이지</h1>
    <p>이곳은 로그인 페이지입니다. 아래에 로그인 폼이 있고, 회원가입 버튼을 통해 /register 페이지로 이동 가능합니다.</p>

    <!-- 로그인 폼 -->
    <form @submit.prevent="login" class="login-form">
      <input
          type="text"
          v-model="loginInfo.userId"
          placeholder="아이디"
          required
      />
      <input
          type="password"
          v-model="loginInfo.userPassword"
          placeholder="비밀번호"
          required
      />


      <button type="submit">로그인</button>
    </form>

    <hr />
    <div class="social-login-section">
      <h2>소셜 로그인</h2>

      <!-- 카카오 로그인 버튼 -->
      <button @click="loginWith('kakao')" class="social-btn kakao">
        카카오로 로그인
      </button>

      <!-- 구글 로그인 버튼 -->
      <button @click="loginWith('google')" class="social-btn google">
        구글로 로그인
      </button>

      <!-- 네이버 로그인 버튼 -->
      <button @click="loginWith('naver')" class="social-btn naver">
        네이버로 로그인
      </button>
    </div>

    <!-- 로그인 결과 메시지 -->
    <p v-if="message">{{ message }}</p>

    <hr />

    <!-- 회원가입 페이지로 이동하는 버튼 -->
    <button @click="goRegisterPage">회원가입 하러 가기</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuth } from '@/composables/useAuth'

definePageMeta({ layout: 'default' })

const router = useRouter()
const auth = useAuth()

const loginInfo = ref({
  userId: '',
  userPassword: ''
})

const message = ref('')

const login = async () => {
  try {
    const config = useRuntimeConfig()

    const response = await fetch(`${config.public.apiBase}/api/v1/user/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(loginInfo.value)
    })

    const data = await response.json()

    if (response.ok) {
      // 서버가 반환한 사용자 정보 기반으로 상태 설정
      console.log(data.data)
      auth.isLoggedIn.value = true
      auth.userType.value = data.data.role || null // "SELLER" or "BUYER"
      auth.userName.value = data.data.userName

      message.value = '로그인 성공!'

      // 역할 기반 페이지 이동
      if (auth.userType.value === 'SELLER') {
        router.push('/seller/dashboard')
      } else {
        router.push('/')
      }
    } else {
      message.value = data.message || '로그인 실패'
    }

  } catch (error) {
    console.error('로그인 중 오류:', error)
    message.value = '로그인 중 오류 발생'
  }
}

const loginWith = (provider) => {
  // Spring 서버 주소 (API 주소랑 다를 수 있음)
  const springBaseUrl = 'http://localhost:1113'

  const redirectUrl = `${springBaseUrl}/oauth2/authorization/${provider}`
  window.location.href = redirectUrl
}

const goRegisterPage = () => {
  router.push('/register')
}

// JWT 디코딩 함수
function parseJwt(token) {
  const base64Payload = token.split('.')[1]
  const payload = atob(base64Payload)
  return JSON.parse(payload)
}
</script>

<style scoped>
.home-page {
  text-align: center;
  margin-top: 50px;
}

.login-form {
  margin: 20px auto;
  max-width: 300px;
  text-align: left;
}

.login-form input {
  display: block;
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
}

.login-form button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: #fff;
  border: none;
  cursor: pointer;
}

.social-login-section {
  margin-top: 20px;
  text-align: center;
}

.social-btn {
  width: 250px;
  max-width: 90%;
  padding: 12px;
  margin: 10px auto;
  font-weight: bold;
  font-size: 16px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  display: block;
}

.kakao {
  background-color: #FEE500;
  color: #3C1E1E;
}

.google {
  background-color: #DB4437;
  color: #fff;
}

.naver {
  background-color: #03C75A;
  color: #fff;
}
</style>
