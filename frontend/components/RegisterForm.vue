<template>
  <div class="register-container">
    <h2>회원가입</h2>
    <form @submit.prevent="register">
      <input type="text" v-model="user.userId" placeholder="아이디" required />
      <input type="password" v-model="user.password" placeholder="비밀번호" required />
      <input type="text" v-model="user.userName" placeholder="이름" required />
      <input type="email" v-model="user.email" placeholder="이메일" required />
      <input type="date" v-model="user.birthday" placeholder="생년월일" required />
      <input type="text" v-model="user.phoneNumber" placeholder="전화번호" required />
      <input type="text" v-model="user.zipcode" placeholder="우편번호" />
      <input type="text" v-model="user.address1" placeholder="주소 1" />
      <input type="text" v-model="user.address2" placeholder="주소 2" />

      <!-- 사용자 타입 선택 -->
      <select v-model="user.userType">
        <option value="student">학생</option>
        <option value="teacher">교사</option>
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

const user = ref({
  userId: '',
  password: '',
  userName: '',
  email: '',
  birthday: '',
  phoneNumber: '',
  zipcode: '',
  address1: '',
  address2: '',
  address: '', // 백엔드에 보낼 주소 필드
  userType: 'student',
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
    const response = await fetch(`${config.public.apiBase}/api/v1/user/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(user.value),
    });


    const data = await response.json();
    message.value = data.message || '회원가입 성공!';
  } catch (error) {
    message.value = '회원가입 실패!';
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
