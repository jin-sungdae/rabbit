<template>
  <div class="description-tab">
    <h3>상품 설명</h3>
    <p>{{ description }}</p>
  </div>
</template>

<script setup>
defineProps({
  description: {
    type: String,
    required: true
  }
});
</script>