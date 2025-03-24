<template>
  <div class="tag-products">
    <h2>#{{ tagName }}</h2>
    <ProductGallery :products="products" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ProductGallery from '@/components/ProductGallery.vue'
import { fetchProductsByTag } from '@/api/productApi'

const route = useRoute()
const tagName = route.params.tagName
const products = ref([])

onMounted(async () => {
  products.value = await fetchProductsByTag(tagName)
})
</script>