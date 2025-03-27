<template>
    <div>
      <v-md-editor v-model="text" height="400px" />
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  const text = ref('# Hello Markdown!')
  </script>