<template>
  <header class="header">
    <div class="header-inner">
      <!-- 로고 -->
      <div class="logo">
        <NuxtLink to="/">MyCommerce</NuxtLink>
      </div>

      <!-- 내비게이션 -->
      <nav class="nav">
        <NuxtLink to="/products">전체 상품</NuxtLink>
        <NuxtLink to="/brands">브랜드</NuxtLink>
        <NuxtLink to="/series">시리즈</NuxtLink>
        <NuxtLink to="/tags">태그</NuxtLink>
      </nav>

      <!-- 사용자 버튼 -->
      <div class="user">
        <NuxtLink to="/cart" class="btn small">🛒</NuxtLink>

        <template v-if="!auth.isInitialized.value">
          <span class="text">로딩 중...</span>
        </template>

        <template v-else-if="auth.isLoggedIn.value">
          <NuxtLink v-if="isSeller" to="/seller/dashboard" class="btn small seller-btn">판매자센터</NuxtLink>
          <NuxtLink :to="mypageUrl" class="btn small">마이페이지</NuxtLink>
          <button class="btn small" @click="handleLogout">로그아웃</button>
        </template>

        <template v-else>
          <NuxtLink to="/login" class="btn small">로그인</NuxtLink>
          <NuxtLink to="/register" class="btn small">회원가입</NuxtLink>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useAuth } from '@/composables/useAuth'
import { useLogout } from '@/composables/useLogout'
import { computed } from 'vue'

const auth = useAuth()
const { logout } = useLogout()

const handleLogout = async () => {
  await logout()
  await navigateTo('/')
}

const user = computed(() => auth.isLoggedIn?.value)
const isSeller = computed(() => auth.userType?.value === 'SELLER')
const mypageUrl = computed(() => user.value ? (isSeller.value ? '/mypage' : '/mypage') : '/login')

</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #fff;
  border-bottom: 1px solid #eee;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-family: 'Pretendard', sans-serif;
}

.logo a {
  font-size: 20px;
  font-weight: 600;
  color: #222;
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 24px;
}

.nav a {
  font-size: 15px;
  color: #444;
  text-decoration: none;
}

.nav a.router-link-active {
  font-weight: bold;
  color: #000;
}

.user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn.small {
  padding: 6px 10px;
  background-color: #2d6cdf;
  color: white;
  font-size: 14px;
  border-radius: 6px;
  text-decoration: none;
  border: none;
  cursor: pointer;
}

.btn.small:hover {
  background-color: #1f4ebb;
}

.text {
  font-size: 14px;
  color: #888;
}

.seller-btn {
  background-color: #0c7b5e;
}

.seller-btn:hover {
  background-color: #055f46;
}

</style>