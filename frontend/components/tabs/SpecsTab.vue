<template>
  <div class="specs-tab">
    <h3>상세 스펙</h3>
    <ul>
      <li v-for="(spec, index) in specs" :key="index">
        <strong>{{ spec.key }}:</strong> {{ spec.value }}
      </li>
    </ul>
  </div>
</template>

<script setup>
defineProps({
  specs: {
    type: Array,
    required: true,
    default: () => []
  }
});
</script>