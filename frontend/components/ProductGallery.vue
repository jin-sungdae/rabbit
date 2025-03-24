<template>
  <div class="product-gallery">
    <div class="main-image">
      <img :src="selectedImage" :alt="selectedAlt" />
    </div>

    <div class="thumbnail-list">
      <img
        v-for="(image, index) in images"
        :key="index"
        :src="image.imageUrl"
        :alt="image.altText || 'Product Image'"
        :class="{ selected: selectedImage === image.imageUrl }"
        @click="selectImage(image)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';

const props = defineProps({
  images: {
    type: Array,
    required: true
  }
});

const selectedImage = ref('');
const selectedAlt = ref('');

watch(
  () => props.images,
  (newImages) => {
    if (newImages?.length > 0) {
      const main = newImages.find((img) => img.isMain) || newImages[0];
      selectedImage.value = main.imageUrl;
      selectedAlt.value = main.altText || 'Product Image';
    }
  },
  { immediate: true }
);

const selectImage = (image) => {
  selectedImage.value = image.imageUrl;
  selectedAlt.value = image.altText || 'Product Image';
};
</script>

<style scoped>
.product-gallery {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.main-image img {
  width: 100%;
  max-width: 500px;
  object-fit: contain;
  border: 1px solid #ccc;
  padding: 8px;
}

.thumbnail-list {
  display: flex;
  gap: 8px;
}

.thumbnail-list img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border: 2px solid transparent;
  cursor: pointer;
}

.thumbnail-list img.selected {
  border-color: #333;
}
</style>