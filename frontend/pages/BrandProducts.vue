<template>
  <div class="brand-products">
    <h2>{{ brandName }} 브랜드 상품</h2>
    <ProductGallery :products="products" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ProductGallery from '@/components/ProductGallery.vue'
import { fetchProductsByBrand } from '@/api/productApi'

const route = useRoute()
const brandId = route.params.brandId
const brandName = ref('')
const products = ref([])

onMounted(async () => {
  const { name, items } = await fetchProductsByBrand(brandId)
  brandName.value = name
  products.value = items
})
</script>