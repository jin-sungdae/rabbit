<template>
  <div class="home">
    <div class="top-bar">
      <Header />
  

      <div class="auth-buttons">
        <template v-if="!auth.isInitialized.value">
          <span>로딩 중...</span>
        </template>
        <template v-else-if="auth.isLoggedIn.value">
          <span>환영합니다 {{ auth.userName }}님</span>
          <button @click="logout">로그아웃</button>
        </template>
        <template v-else>
          <NuxtLink to="/login" class="btn">로그인</NuxtLink>
          <NuxtLink to="/register" class="btn">회원가입</NuxtLink>
        </template>
      </div>
    </div>
  

    
      <!-- 카테고리 사이드바 + 본문 그리드 -->
      <div class="container">
        <div class="sidebar">
          <CategorySidebar />
        </div>
  
        <div class="content">
  
          <!-- 추천 탭 섹션 -->
          <section>
            <ProductTab
              :featured-products="featuredProducts"
              :latest-products="latestProducts"
              :discounted-products="discountedProducts"
            />
          </section>
  
          <!-- 상품 갤러리 -->
          <!-- <section>
            <h2>추천 상품</h2>
            <ProductGallery :products="featuredProducts" />
          </section>
   -->
          <!-- 태그 기반 인기 상품 -->
          <!-- <section v-if="popularTags.length">
            <div v-for="tag in popularTags" :key="tag.id" class="tag-section">
              <h3>#{{ tag.name }}</h3>
              <ProductGallery :products="tagProductMap[tag.name]" />
            </div>
          </section> -->
  
          <!-- 인기 브랜드 -->
          <!-- <section v-if="popularBrands.length">
            <h2>브랜드 인기 상품</h2>
            <div v-for="brand in popularBrands" :key="brand.id" class="brand-section">
              <h3>{{ brand.name }}</h3>
              <ProductGallery :products="brandProductMap[brand.name]" />
            </div>
          </section> -->
  
          <!-- 시리즈 상품 -->
          <!-- <section v-if="seriesList.length">
            <h2>시리즈 상품</h2>
            <div v-for="series in seriesList" :key="series.id" class="series-section">
              <h3>{{ series.name }}</h3>
              <ProductGallery :products="seriesProductMap[series.name]" />
            </div>
          </section> -->
  
        </div>
      </div>
  
      <Footer />
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  
  import Header from '@/components/Header.vue'
  import Footer from '@/components/Footer.vue'
  import CategorySidebar from '@/components/CategorySidebar.vue'
  import ProductTab from '@/components/ProductTab.vue'
  import ProductGallery from '@/components/ProductGallery.vue'
  import { useAuth } from '@/composables/useAuth'
  import { useLogout } from '@/composables/useLogout'

  import {
    fetchFeaturedProducts,
    fetchLatestProducts,
    fetchDiscountedProducts,
    fetchPopularTags,
    fetchProductsByTag,
    fetchPopularBrands,
    fetchProductsByBrand,
    fetchSeriesList,
    fetchProductsBySeries
  } from '@/api/productApi'
  
  // 상품 탭 데이터
  const featuredProducts = ref([])
  const latestProducts = ref([])
  const discountedProducts = ref([])
  
  // 태그 기반 데이터
  const popularTags = ref([])
  const tagProductMap = ref({})
  
  // 브랜드 기반 데이터
  const popularBrands = ref([])
  const brandProductMap = ref({})
  
  // 시리즈 기반 데이터
  const seriesList = ref([])
  const seriesProductMap = ref({})


  onMounted(async () => {
    // 상품 탭
    featuredProducts.value = await fetchFeaturedProducts()
    latestProducts.value = await fetchLatestProducts()
    discountedProducts.value = await fetchDiscountedProducts()
  
    // 태그 + 태그별 상품
    popularTags.value = await fetchPopularTags()
    for (const tag of popularTags.value) {
      const products = await fetchProductsByTag(tag.id)
      tagProductMap.value[tag.name] = products
    }
  
    // 브랜드 + 브랜드별 상품
    popularBrands.value = await fetchPopularBrands()
    for (const brand of popularBrands.value) {
      const products = await fetchProductsByBrand(brand.id)
      brandProductMap.value[brand.name] = products
    }
  
    // 시리즈 + 시리즈별 상품
    seriesList.value = await fetchSeriesList()
    for (const series of seriesList.value) {
      const products = await fetchProductsBySeries(series.id)
      seriesProductMap.value[series.name] = products
    }
  })

  const auth = useAuth()



  const { logout } = useLogout()
  </script>
  
  <style scoped>
  .home {
    font-family: 'Pretendard', sans-serif;
  }
  .container {
    display: flex;
  }
  .sidebar {
    width: 250px;
    padding: 16px;
  }
  .content {
    flex: 1;
    padding: 16px;
  }
  section {
    margin-bottom: 32px;
  }

  .top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  }

  .auth-buttons {
    display: flex;
    gap: 10px;
  }

  .auth-buttons .btn {
    padding: 8px 14px;
    background-color: #007bff;
    color: white;
    border-radius: 4px;
    text-decoration: none;
    font-size: 14px;
  }

  .auth-buttons .btn:hover {
    background-color: #0056b3;
  }
  
  </style>