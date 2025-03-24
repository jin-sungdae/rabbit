<template>
  <div class="reviews-tab">
    <h3>사용자 리뷰</h3>
    <ul>
      <li v-for="(review, index) in reviews" :key="index">
        <strong>{{ review.author }}</strong>: {{ review.content }}
      </li>
    </ul>
  </div>
</template>

<script setup>
defineProps({
  reviews: {
    type: Array,
    required: true,
    default: () => []
  }
});
</script>