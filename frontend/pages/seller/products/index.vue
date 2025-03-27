<template>
    <div class="products-page">
      <h2 class="title">상품 관리</h2>
      <p class="description">상품 목록을 관리하거나 새로운 상품을 등록할 수 있습니다.</p>
  
      <div class="actions">
        <NuxtLink to="/seller/products/new" class="btn">+ 새 상품 등록</NuxtLink>
      </div>
  
      <div v-if="products.length > 0" class="product-grid">
        <div v-for="product in products" :key="product.id" class="product-item">
          <h3>{{ product.defaultName }}</h3>
          <p>{{ product.productCode }}</p>
          <div class="meta">
            <!-- <img 
                v-if="product.mainImage && product.mainImage.fileUrl" 
                :src="product.mainImage.fileUrl" 
                alt="메인 이미지" 
                class="thumbnail"
                /> -->
            <button class="edit-btn">수정</button>
          </div>
        </div>
      </div>
  
      <div v-else class="no-data">등록된 상품이 없습니다.</div>
  
      <!-- 페이지네이션 -->
      <nav class="pagination">
        <button
          v-for="p in totalPages"
          :key="p"
          :class="{ active: page === p }"
          @click="goToPage(p)">
          {{ p }}
        </button>
      </nav>
    </div>
  </template>
  
  <script setup lang="ts">

    definePageMeta({ layout: 'seller' })
  import { ref, onMounted } from 'vue'
  import { useAuth } from '@/composables/useAuth'
  import { fetchWithAuth } from '@/utils/fetchWithAuth'

  const config = useRuntimeConfig()

  const products = ref([])
const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = 20

const fetchProducts = async (page = 1) => {
  try {
    const data = await fetchWithAuth<{
      success: boolean,
      data: {
        content: Product[]
        totalPages: number
      }
    }>(
      `${config.public.apiBase}/api/v1/products/all?page=${page - 1}&size=${pageSize}`,
      {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
      }
    );

    if (data.success) {
      products.value = data.data.content;
      totalPages.value = data.data.totalPages;
      currentPage.value = page;
    } else {
      console.error('상품 목록 응답 에러', data);
    }
  } catch (err) {
    console.error('상품 목록 불러오기 실패', err);
  }
};

const goToPage = (page: number) => {
  if (page !== currentPage.value) fetchProducts(page)
}

onMounted(() => {
  fetchProducts(1)
})

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

  .product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 5열 */
  gap: 20px;
  margin-top: 2rem;
}

.product-item {
  border: 1px solid #ddd;
  padding: 1rem;
  border-radius: 8px;
}

.pagination {
  margin-top: 2rem;
  display: flex;
  gap: 10px;
  justify-content: center;
}

.pagination button {
  padding: 6px 12px;
  border: none;
  background: #eee;
  cursor: pointer;
  border-radius: 4px;
}

.pagination button.active {
  background: #333;
  color: white;
  font-weight: bold;
}
  </style>