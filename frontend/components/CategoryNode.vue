<template>
  <li>
    <div
      class="category-name"
      :class="{ active: isActive }"
      @click="toggle"
    >
      {{ category.name }}
    </div>
    <ul v-if="isExpanded && category.children?.length">
      <CategoryNode
        v-for="child in category.children"
        :key="child.id"
        :category="child"
      />
    </ul>
  </li>
</template>

<script setup>
import { ref } from 'vue'

// props
defineProps({
  category: {
    type: Object,
    required: true
  }
})

const isExpanded = ref(false)
const isActive = ref(false)

function toggle() {
  isExpanded.value = !isExpanded.value
  isActive.value = !isActive.value
}
</script>

<style scoped>
.category-name {
  cursor: pointer;
  padding: 4px 0;
  transition: all 0.2s;
}

.category-name.active {
  font-weight: bold;
  color: #2c3e50;
}

ul {
  margin-left: 16px;
  padding-left: 0;
  list-style-type: none;
}
</style>