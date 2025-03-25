<!-- pages/mypage/index.vue -->
<template>
    <div>
      <h1 class="title">마이페이지 홈</h1>
      <p>안녕하세요 {{ auth.userName.value }}님, 아래에서 주문 및 계정 정보를 확인하실 수 있습니다.</p>
    </div>
  </template>
  
  <script setup>
  import { useAuth } from '@/composables/useAuth'
  const auth = useAuth()
  </script>
  
  <style scoped>
  .title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 12px;
  }
  </style>