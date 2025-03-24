<template>
  <div class="product-detail">
    <ProductCard :product="product" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ProductCard from '@/components/ProductCard.vue'
import { fetchProductDetail } from '@/api/productApi'

const route = useRoute()
const product = ref(null)

onMounted(async () => {
  const id = route.params.id
  product.value = await fetchProductDetail(id)
})
</script>