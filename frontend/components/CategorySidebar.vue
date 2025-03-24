<template>
  <aside class="category-sidebar">
    <h2>카테고리</h2>
    <ul class="category-tree">
      <CategoryNode
        v-for="category in categories"
        :key="category.id"
        :category="category"
        @category-selected="handleCategorySelect"
      />
    </ul>
  </aside>
</template>

<script setup>
import CategoryNode from './CategoryNode.vue';

const props = defineProps({
  categories: Array, // 계층 구조 포함된 카테고리 트리
});

const emit = defineEmits(['category-selected']);

function handleCategorySelect(category) {
  emit('category-selected', category);
}
</script>

<style scoped>
.category-sidebar {
  padding: 1rem;
  border-right: 1px solid #ddd;
  background-color: #f9f9f9;
}

.category-sidebar h2 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.category-tree {
  list-style: none;
  padding-left: 0;
}
</style>