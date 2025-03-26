<template>
    <div class="dashboard">
      <h2 class="title">판매자 대시보드</h2>
  
      <div class="cards">
        <!-- 주문 관리 -->
        <div class="card">
          <h3>신규 주문</h3>
          <p class="count">{{ dashboard.orders }}</p>
          <NuxtLink to="/seller/orders" class="btn">주문 관리</NuxtLink>
        </div>
  
        <!-- 상품 관리 -->
        <div class="card">
          <h3>등록 상품</h3>
          <p class="count">{{ dashboard.products }}</p>
          <NuxtLink to="/seller/products" class="btn">상품 관리</NuxtLink>
        </div>
  
        <!-- 리뷰 관리 -->
        <div class="card">
          <h3>미확인 리뷰</h3>
          <p class="count">{{ dashboard.reviews }}</p>
          <NuxtLink to="/seller/reviews" class="btn">리뷰 관리</NuxtLink>
        </div>
  
        <!-- 판매 지표 -->
        <div class="card analytics">
          <h3>이번 달 매출</h3>
          <p class="sales">{{ dashboard.sales | numberFormat }}원</p>
          <NuxtLink to="/seller/analytics" class="btn">상세 지표 보기</NuxtLink>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  definePageMeta({ layout: 'seller' })
  
  import { ref, onMounted } from 'vue'
  import { useRuntimeConfig } from '#app'
  
  const dashboard = ref({
    orders: 0,
    products: 0,
    reviews: 0,
    sales: 0
  })
  
  const config = useRuntimeConfig()
  
  onMounted(async () => {
    const response = await fetch(`${config.public.apiBase}/api/v1/seller/dashboard`, {
      method: 'GET',
      credentials: 'include'
    })
  
    const data = await response.json()
    
    if (response.ok && data.success) {
      dashboard.value = data.data
    } else {
      console.error('Dashboard 정보 로딩 실패', data.message)
    }
  })
  </script>
  
  <style scoped>
  .dashboard {
    padding: 30px;
    max-width: 1000px;
    margin: 0 auto;
  }
  
  .title {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 30px;
  }
  
  .cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
  }
  
  .card {
    padding: 24px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
    text-align: center;
  }
  
  .card.analytics {
    grid-column: span 2;
    background-color: #f0f9f6;
  }
  
  .card h3 {
    margin-bottom: 15px;
    font-size: 16px;
    color: #666;
  }
  
  .count, .sales {
    font-size: 28px;
    font-weight: 600;
    margin-bottom: 20px;
    color: #222;
  }
  
  .btn {
    display: inline-block;
    padding: 8px 14px;
    font-size: 14px;
    background-color: #0c7b5e;
    color: white;
    text-decoration: none;
    border-radius: 6px;
  }
  
  .btn:hover {
    background-color: #055f46;
  }
  </style>