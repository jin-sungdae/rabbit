<template>
  <div class="layout-container">
    <!-- 헤더 -->
    <header class="header">
      <h1>회원가입 페이지</h1>
    </header>

    <!-- 페이지 내용 -->
    <main>
      <slot />
    </main>

    <!-- 푸터 -->
    <footer class="footer">
      <p>&copy; 2025 MyApp. All rights reserved.</p>
    </footer>
  </div>
</template>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  background-color: #007bff;
  color: white;
  padding: 15px;
  text-align: center;
}

main {
  flex: 1;
  padding: 20px;
  max-width: 600px;
  margin: auto;
}

.footer {
  background-color: #f8f9fa;
  color: #555;
  padding: 10px;
  text-align: center;
}
</style>