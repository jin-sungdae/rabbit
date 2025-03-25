<template>
    <div class="icon-box" :title="tooltip">
      <component :is="iconComponent" class="icon" />
    </div>
  </template>
  
  <script setup>
  import { computed } from 'vue'
  import { icons } from 'lucide-vue-next' // 2028 기준 심플 아이콘 라이브러리
  const props = defineProps({
    icon: { type: String, required: true },
    tooltip: { type: String, default: '' }
  })
  const iconComponent = computed(() => icons[props.icon])
  </script>
  
  <style scoped>
  .icon-box {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 38px;
    height: 38px;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.2s ease;
  }
  
  .icon-box:hover {
    background-color: rgba(0, 0, 0, 0.05);
  }
  
  .icon {
    width: 20px;
    height: 20px;
    color: #222;
  }
  </style>