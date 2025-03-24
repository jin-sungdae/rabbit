<template>
  <div class="product-list">
    <CategorySidebar @category-selected="onCategorySelected" />
    <ProductFilter @filter="onFilter" />
    <ProductGallery :products="products" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import CategorySidebar from '@/components/CategorySidebar.vue'
import ProductFilter from '@/components/ProductFilter.vue'
import ProductGallery from '@/components/ProductGallery.vue'
// import { fetchProducts } from '@/api/productApi'

const products = ref([])
const filters = ref({})

// const loadProducts = async () => {
//   products.value = await fetchProducts(filters.value)
// }

// const onCategorySelected = (categoryId) => {
//   filters.value.categoryId = categoryId
//   loadProducts()
// }

// const onFilter = (newFilter) => {
//   filters.value = { ...filters.value, ...newFilter }
//   loadProducts()
// }

// watch(filters, loadProducts, { immediate: true })
</script>