<template>
    <div>
      <h1 class="text-xl font-bold">마이페이지</h1>
      <ul>
        <li><NuxtLink to="/mypage/orders">주문 내역</NuxtLink></li>
        <li><NuxtLink to="/mypage/wishlist">찜한 상품</NuxtLink></li>
        <li><NuxtLink to="/mypage/profile">내 정보 수정</NuxtLink></li>
      </ul>
      <NuxtPage />
    </div>
  </template>