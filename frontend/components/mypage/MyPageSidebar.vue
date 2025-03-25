<template>
    <ul class="menu">
      <li><NuxtLink to="/mypage/orders" active-class="active">주문 내역</NuxtLink></li>
      <li><NuxtLink to="/mypage/wishlist" active-class="active">찜한 상품</NuxtLink></li>
      <li><NuxtLink to="/mypage/profile" active-class="active">내 정보 수정</NuxtLink></li>
    </ul>
  </template>
  
  <style scoped>
  .menu {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .menu a {
    text-decoration: none;
    color: #333;
    font-size: 15px;
    padding: 8px 12px;
    border-radius: 6px;
    transition: background 0.2s ease;
  }
  
  .menu a:hover {
    background-color: #f0f0f0;
  }
  
  .menu a.active {
    background-color: #2d6cdf;
    color: white;
  }
  </style>