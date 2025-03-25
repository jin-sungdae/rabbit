<template>
    <section>
      <h2 class="section-title">찜한 상품</h2>
  
      <div v-if="wishlist.length === 0" class="empty">
        찜한 상품이 없습니다.
      </div>
  
      <ul v-else class="wishlist">
        <li v-for="item in wishlist" :key="item.id" class="wishlist-item">
          <img :src="item.image" alt="상품 이미지" class="product-image" />
          <div class="info">
            <div class="name">{{ item.name }}</div>
            <div class="price">{{ item.price.toLocaleString() }}원</div>
          </div>
        </li>
      </ul>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  
  const wishlist = ref([])
  
  onMounted(() => {
    // TODO: 실제 API 연동 필요
    wishlist.value = [
      {
        id: 1,
        name: 'AI 공부 노트',
        price: 12900,
        image: 'https://via.placeholder.com/80x80'
      },
      {
        id: 2,
        name: '미래형 개발자 키트',
        price: 49000,
        image: 'https://via.placeholder.com/80x80'
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
  
  .empty {
    color: #888;
  }
  
  .wishlist {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  
  .wishlist-item {
    display: flex;
    align-items: center;
    padding: 12px;
    border-radius: 8px;
    background-color: #f8f8f8;
    border: 1px solid #eee;
  }
  
  .product-image {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 6px;
    margin-right: 16px;
  }
  
  .info {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
  
  .name {
    font-size: 16px;
    font-weight: 500;
  }
  
  .price {
    font-size: 14px;
    color: #444;
  }
  </style>