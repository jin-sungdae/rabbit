<template>
  <div class="register-container">
    <h2>회원가입</h2>
    <form @submit.prevent="register">
      <input type="text" v-model="user.userId" placeholder="아이디" required />
      <input type="password" v-model="user.userPassword" placeholder="비밀번호" required />
      <input type="text" v-model="user.userName" placeholder="이름" required />
      <input type="email" v-model="user.email" placeholder="이메일" required />
      <input type="date" v-model="user.birthday" placeholder="생년월일" required />
      <input type="text" v-model="user.phoneNumber" placeholder="전화번호" required />
      <input type="text" v-model="user.zipcode" placeholder="우편번호" />
      <input type="text" v-model="user.address1" placeholder="주소 1" />
      <input type="text" v-model="user.address2" placeholder="주소 2" />

      <!-- 사용자 역할 선택 -->
      <select v-model="user.role">
        <option value="USER">구매자</option>
        <option value="SELLER">판매자</option>
      </select>

      <!-- 가입 경로 선택 -->
      <select v-model="user.joinRoot">
        <option value="google">구글</option>
        <option value="facebook">페이스북</option>
        <option value="kakao">카카오</option>
        <option value="email">이메일</option>
      </select>

      <!-- 알림 설정 -->
      <label>
        <input type="checkbox" v-model="user.isMailing" /> 이메일 알림 수신
      </label>
      <label>
        <input type="checkbox" v-model="user.isSms" /> SMS 알림 수신
      </label>

      <button type="submit">회원가입</button>
    </form>
    <p v-if="message">{{ message }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';  // 페이지 이동을 위해 useRouter 사용

const router = useRouter();

const user = ref({
  userId: '',
  userPassword: '',
  userName: '',
  email: '',
  birthday: '',
  phoneNumber: '',
  zipcode: '',
  address1: '',
  address2: '',
  address: '', // 백엔드에 보낼 주소 필드
  role : 'USER',
  joinRoot: 'email',
  isMailing: true,
  isSms: true
});

const message = ref('');

const register = async () => {
  try {
    // 주소 조합
    user.value.address = `${user.value.address1} ${user.value.address2}`.trim();

    const config = useRuntimeConfig();
    // 1) 회원가입 요청
    const response = await fetch(`${config.public.apiBase}/api/v1/user/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(user.value),
    });

    const data = await response.json();

    if (!response.ok) {
      // 회원가입 실패 시
      message.value = data.message || '회원가입 실패!';
      return;
    }
    // 회원가입 성공 메시지
    message.value = '회원가입 성공! 잠시 후 자동 로그인합니다...';

    // 2) 회원가입에 성공하면 → 자동 로그인 로직
    //    (userId, password를 사용해 로그인 API 호출)
    const loginResponse = await fetch(`${config.public.apiBase}/api/v1/user/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: user.value.userId,
        password: user.value.password
      })
    });

    const loginData = await loginResponse.json();
    if (!loginResponse.ok) {
      // 로그인 실패
      message.value = loginData.message || '회원가입은 되었으나, 자동 로그인 실패!';
      return;
    }

    // 로그인 성공 시 토큰 등을 로컬스토리지에 저장 (백엔드와 프로토콜에 따라 다름)
    // 예: localStorage에 accessToken 저장
    if (loginData.accessToken) {
      localStorage.setItem('accessToken', loginData.accessToken);
    }

    message.value = '자동 로그인 완료! 메인 페이지로 이동합니다.';
    // 3) 메인 페이지("/")로 이동
    setTimeout(() => {
      router.push('/Home');
    }, 1000); // 1초 후 이동
  } catch (error) {
    console.error(error);
    message.value = '회원가입/로그인 중 오류가 발생했습니다.';
  }
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  text-align: center;
}

input, select {
  display: block;
  width: 100%;
  margin-bottom: 10px;
  padding: 8px;
}

label {
  display: block;
  text-align: left;
  margin: 5px 0;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}
</style>
