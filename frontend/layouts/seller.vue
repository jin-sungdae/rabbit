<template>
    <div>
      <Header />
      <div class="seller-container">
        <nav class="sidebar">
          <ul>
            <li><NuxtLink to="/seller/dashboard">대시보드</NuxtLink></li>
            <li><NuxtLink to="/seller/profile">상점 프로필 관리</NuxtLink></li>
            <li><NuxtLink to="/seller/products">상품 관리</NuxtLink></li>
            <li><NuxtLink to="/seller/orders">주문 관리</NuxtLink></li>
            <li><NuxtLink to="/seller/reviews">리뷰 관리</NuxtLink></li>
            <li><NuxtLink to="/seller/analytics">판매 데이터 분석</NuxtLink></li>
          </ul>
        </nav>
        <main class="seller-content">
          <slot />
        </main>
      </div>
      <Footer />
    </div>
  </template>
  
  <script setup>
  import Header from '@/components/Header.vue'
  import Footer from '@/components/Footer.vue'
  </script>
  
  <style scoped>
  .seller-container {
    display: flex;
    min-height: 80vh;
  }
  
  .sidebar {
    width: 240px;
    background-color: #f8fafc;
    padding: 20px;
    border-right: 1px solid #eaeaea;
  }
  
  .sidebar ul {
    list-style: none;
    padding: 0;
  }
  
  .sidebar ul li {
    margin-bottom: 16px;
  }
  
  .sidebar ul li a {
    text-decoration: none;
    color: #333;
  }
  
  .seller-content {
    flex-grow: 1;
    padding: 32px;
  }
  </style>