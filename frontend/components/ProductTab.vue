<template>
  <div class="product-tabs">
    <ul class="tab-menu">
      <li
        v-for="tab in tabs"
        :key="tab.name"
        :class="{ active: currentTab === tab.name }"
        @click="currentTab = tab.name"
      >
        {{ tab.label }}
      </li>
    </ul>

    <div class="tab-content">
      <component :is="getComponent(currentTab)" :product="product" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 탭 정의
const props = defineProps({
  product: {
    type: Object,
    required: true
  }
});

const currentTab = ref('description');

const tabs = [
  { name: 'description', label: '상품 설명' },
  { name: 'specs', label: '상세 스펙' },
  { name: 'reviews', label: '리뷰' },
  { name: 'shipping', label: '배송/교환/반품' }
];

const getComponent = (tabName) => {
  switch (tabName) {
    case 'description':
      return DescriptionTab;
    case 'specs':
      return SpecsTab;
    case 'reviews':
      return ReviewsTab;
    case 'shipping':
      return ShippingTab;
    default:
      return DescriptionTab;
  }
};

// 탭 컴포넌트 불러오기 (필요 시 별도 컴포넌트로 분리 가능)
import DescriptionTab from './tabs/DescriptionTab.vue';
import SpecsTab from './tabs/SpecsTab.vue';
import ReviewsTab from './tabs/ReviewsTab.vue';
import ShippingTab from './tabs/ShippingTab.vue';
</script>

<style scoped>
.product-tabs {
  margin-top: 24px;
}

.tab-menu {
  display: flex;
  border-bottom: 1px solid #ddd;
  margin-bottom: 16px;
}

.tab-menu li {
  padding: 10px 16px;
  cursor: pointer;
  font-weight: 500;
  color: #666;
}

.tab-menu li.active {
  border-bottom: 2px solid #333;
  color: #000;
}

.tab-content {
  min-height: 100px;
}
</style>