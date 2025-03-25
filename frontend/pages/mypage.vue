
<template>
    <div class="mypage">
      <Header />
  
      <div class="mypage-container">
        <aside class="sidebar">
          <MypageSidebar />
        </aside>
  
        <main class="content">
          <h1 class="title">마이페이지</h1>
          <NuxtPage />
        </main>
      </div>
  
      <Footer />
    </div>
  </template>
  
  <script setup>
  import Header from '@/components/Header.vue'
  import Footer from '@/components/Footer.vue'
  import MypageSidebar from '@/components/mypage/MypageSidebar.vue'
  </script>
  
  <style scoped>
  .mypage {
    font-family: 'Pretendard', sans-serif;
    background: #f9f9f9;
    min-height: 100vh;
  }
  
  .mypage-container {
    display: flex;
    max-width: 1280px;
    margin: 0 auto;
    padding: 32px 16px;
    gap: 32px;
  }
  
  .sidebar {
    width: 240px;
    background-color: #fff;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.04);
  }
  
  .content {
    flex: 1;
    background-color: #fff;
    padding: 24px;
    border-radius: 12px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.04);
    min-height: 500px;
  }
  
  .title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 24px;
  }
  </style>