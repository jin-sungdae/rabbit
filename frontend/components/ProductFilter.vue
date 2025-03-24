<template>
  <div class="product-filter">
    <div class="filter-section">
      <h3>카테고리</h3>
      <ul>
        <li
          v-for="category in categories"
          :key="category.id"
          :class="{ selected: selectedCategory === category.id }"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
        </li>
      </ul>
    </div>

    <div class="filter-section">
      <h3>브랜드</h3>
      <ul>
        <li
          v-for="brand in brands"
          :key="brand.id"
          :class="{ selected: selectedBrand === brand.id }"
          @click="selectBrand(brand.id)"
        >
          {{ brand.name }}
        </li>
      </ul>
    </div>

    <div class="filter-section">
      <h3>태그</h3>
      <ul>
        <li
          v-for="tag in tags"
          :key="tag"
          :class="{ selected: selectedTag === tag }"
          @click="selectTag(tag)"
        >
          #{{ tag }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  categories: Array,
  brands: Array,
  tags: Array
});

const emit = defineEmits(['filter-change']);

const selectedCategory = ref(null);
const selectedBrand = ref(null);
const selectedTag = ref(null);

function selectCategory(categoryId) {
  selectedCategory.value = categoryId;
  emitFilterChange();
}

function selectBrand(brandId) {
  selectedBrand.value = brandId;
  emitFilterChange();
}

function selectTag(tag) {
  selectedTag.value = tag;
  emitFilterChange();
}

function emitFilterChange() {
  emit('filter-change', {
    categoryId: selectedCategory.value,
    brandId: selectedBrand.value,
    tag: selectedTag.value
  });
}
</script>

<style scoped>
.product-filter {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-section h3 {
  font-size: 1.1rem;
  margin-bottom: 8px;
}

.filter-section ul {
  list-style: none;
  padding: 0;
}

.filter-section li {
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.filter-section li:hover {
  background: #f0f0f0;
}

.filter-section li.selected {
  background: #007bff;
  color: white;
}
</style>