<template>
  <div class="series-products">
    <h2>{{ seriesName }} 시리즈 상품</h2>
    <ProductGallery :products="products" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ProductGallery from '@/components/ProductGallery.vue'
import { fetchProductsBySeries } from '@/api/productApi'

const route = useRoute()
const seriesId = route.params.seriesId
const seriesName = ref('')
const products = ref([])

onMounted(async () => {
  const { name, items } = await fetchProductsBySeries(seriesId)
  seriesName.value = name
  products.value = items
})
</script>