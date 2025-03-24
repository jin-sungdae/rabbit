<template>
  <footer class="footer">
    <div class="container">
      <p>&copy; 2025 MyCommerce. All rights reserved.</p>
      <ul class="footer-links">
        <li><router-link to="/terms">이용약관</router-link></li>
        <li><router-link to="/privacy">개인정보처리방침</router-link></li>
        <li><router-link to="/about">회사소개</router-link></li>
      </ul>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.footer {
  background-color: #f4f4f4;
  padding: 20px 0;
  border-top: 1px solid #ddd;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.footer .container {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 10px 0 0 0;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.footer-links li a {
  color: #666;
  text-decoration: none;
}

.footer-links li a:hover {
  text-decoration: underline;
}
</style>