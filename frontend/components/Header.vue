<template>
  <header class="header">
    <div class="logo">
      <router-link to="/">MyCommerce</router-link>
    </div>
    <nav class="nav">
      <router-link to="/products">전체 상품</router-link>
      <router-link to="/brands">브랜드</router-link>
      <router-link to="/series">시리즈</router-link>
      <router-link to="/tags">태그</router-link>
    </nav>
  </header>
</template>

<script setup>
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  background-color: #f8f8f8;
  border-bottom: 1px solid #ddd;
}

.logo a {
  font-size: 24px;
  font-weight: bold;
  text-decoration: none;
  color: #222;
}

.nav a {
  margin-left: 20px;
  text-decoration: none;
  color: #555;
}

.nav a.router-link-active {
  font-weight: bold;
  color: #000;
}
</style>