<template>
    <section>
      <h2 class="section-title">주문 내역</h2>
  
      <div v-if="orders.length === 0" class="empty">
        주문 내역이 없습니다.
      </div>
  
      <ul v-else class="order-list">
        <li v-for="order in orders" :key="order.id" class="order-item">
          <div>
            <strong>주문번호:</strong> {{ order.orderNumber }}
          </div>
          <div>
            <strong>주문일자:</strong> {{ order.date }}
          </div>
          <div>
            <strong>상품명:</strong> {{ order.productName }}
          </div>
          <div>
            <strong>총 금액:</strong> {{ order.totalPrice.toLocaleString() }}원
          </div>
        </li>
      </ul>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  
  // 예시 더미 데이터
  const orders = ref([])
  
  onMounted(() => {
    // TODO: 실제 API 연동 시 여기를 교체
    orders.value = [
      {
        id: 1,
        orderNumber: 'ORD-001',
        date: '2025-03-25',
        productName: 'AI 마스터 교재',
        totalPrice: 39000
      },
      {
        id: 2,
        orderNumber: 'ORD-002',
        date: '2025-03-22',
        productName: '코딩 집중 캠프 패키지',
        totalPrice: 128000
      }
    ]
  })
  </script>
  
  <style scoped>
  .section-title {
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 16px;
  }
  
  .order-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  
  .order-item {
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;
    border: 1px solid #eee;
  }
  .empty {
    color: #888;
  }
  </style>