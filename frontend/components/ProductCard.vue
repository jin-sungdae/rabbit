<template>
  <div class="product-card">
    <router-link :to="`/products/${product.id}`">
      <img :src="product.imageUrl" :alt="product.name" class="product-image" />
      <div class="product-info">
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-price">
          {{ formattedPrice }}
        </p>
        <p class="product-tag" v-if="product.tags && product.tags.length">
          <span v-for="tag in product.tags" :key="tag" class="tag">#{{ tag }}</span>
        </p>
      </div>
    </router-link>
  </div>
</template>

<script setup>
defineProps({
  product: {
    type: Object,
    required: true
  }
});

const formattedPrice = computed(() =>
  product.salePrice ? `${product.salePrice.toLocaleString()}원` : `${product.basePrice.toLocaleString()}원`
);
</script>

<style scoped>
.product-card {
  border: 1px solid #eaeaea;
  border-radius: 6px;
  overflow: hidden;
  transition: box-shadow 0.2s ease;
  background-color: #fff;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.product-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.product-price {
  font-size: 14px;
  color: #555;
}

.product-tag .tag {
  display: inline-block;
  margin-right: 6px;
  font-size: 12px;
  color: #888;
}
</style>