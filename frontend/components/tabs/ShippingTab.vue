<template>
  <div class="shipping-tab">
    <h3>배송 정보</h3>
    <p>{{ shippingInfo }}</p>
  </div>
</template>

<script setup>
defineProps({
  shippingInfo: {
    type: String,
    required: true
  }
});
</script>