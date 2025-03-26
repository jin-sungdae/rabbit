<template>
    <div class="products-page">
      <h2 class="title">상품 관리</h2>
      <p class="description">상품 목록을 관리하거나 새로운 상품을 등록할 수 있습니다.</p>
  
      <div class="actions">
        <NuxtLink to="/seller/products/new" class="btn">+ 새 상품 등록</NuxtLink>
      </div>
  
      <div v-if="products.length > 0" class="product-list">
        <div v-for="product in products" :key="product.id" class="product-item">
          <h3>{{ product.name }}</h3>
          <p>{{ product.description }}</p>
          <div class="meta">
            <span>₩{{ product.price.toLocaleString() }}</span>
            <button class="edit-btn">수정</button>
          </div>
        </div>
      </div>
  
      <div v-else class="no-data">
        등록된 상품이 없습니다.
      </div>
    </div>
  </template>
  
  <script setup>
    definePageMeta({ layout: 'seller' })
  import { ref, onMounted } from 'vue'
  import { useAuth } from '@/composables/useAuth'
  
  const products = ref([])
  const auth = useAuth()
  
  onMounted(async () => {
    try {
      const res = await fetch('/api/v1/seller/products', {
        credentials: 'include'
      })
      const data = await res.json()
      if (res.ok) {
        products.value = data.data
      }
    } catch (err) {
      console.error('상품 목록 불러오기 실패', err)
    }
  })
  
  definePageMeta({ layout: 'seller' })
  </script>
  
  <style scoped>
  .products-page {
    max-width: 960px;
    margin: 0 auto;
    padding: 24px;
  }
  .title {
    font-size: 22px;
    font-weight: bold;
    margin-bottom: 16px;
  }
  .description {
    color: #666;
    margin-bottom: 24px;
  }
  .actions {
    margin-bottom: 20px;
  }
  .btn {
    background: #1976d2;
    color: #fff;
    padding: 10px 16px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    text-decoration: none;
  }
  .product-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .product-item {
    padding: 16px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background: #f9f9f9;
  }
  .meta {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
  }
  .edit-btn {
    background: #ffc107;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
    cursor: pointer;
  }
  .no-data {
    text-align: center;
    color: #888;
    padding: 40px;
    font-size: 16px;
  }
  </style>