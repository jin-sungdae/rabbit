<template>
    <section>
      <h2 class="page-title">상품 수정</h2>
  
      <form class="form" @submit.prevent="submit">
        <label>
          상품명
          <input v-model="form.name" type="text" />
        </label>
  
        <label>
          가격
          <input v-model="form.price" type="number" />
        </label>
  
        <label>
          설명
          <textarea v-model="form.description" rows="4" />
        </label>
  
        <div class="actions">
          <button type="submit">수정 완료</button>
          <button type="button" @click="remove">삭제</button>
        </div>
      </form>
    </section>
  </template>
  
  <script setup>
  import { onMounted, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useFetch } from '#app'
  
  definePageMeta({ layout: 'seller' })
  
  const route = useRoute()
  const router = useRouter()
  const id = route.params.id
  
  const form = ref({
    name: '',
    price: 0,
    description: ''
  })
  
  // 🔁 상품 정보 로딩
  onMounted(async () => {
    const res = await fetch(`/api/v1/seller/products/${id}`, { credentials: 'include' })
    if (res.ok) {
      const data = await res.json()
      form.value = {
        name: data.name,
        price: data.price,
        description: data.description
      }
    } else {
      alert('상품 정보를 불러오지 못했습니다')
    }
  })
  
  // ✅ 수정 API
  const submit = async () => {
    const res = await fetch(`/api/v1/seller/products/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(form.value)
    })
    if (res.ok) {
      alert('수정 완료')
      await router.push('/seller/products')
    } else {
      alert('수정 실패')
    }
  }
  
  // ❌ 삭제 API
  const remove = async () => {
    if (!confirm('정말 삭제하시겠습니까?')) return
  
    const res = await fetch(`/api/v1/seller/products/${id}`, {
      method: 'DELETE',
      credentials: 'include'
    })
  
    if (res.ok) {
      alert('삭제 완료')
      await router.push('/seller/products')
    } else {
      alert('삭제 실패')
    }
  }
  </script>
  
  <style scoped>
  .page-title {
    font-size: 22px;
    font-weight: 600;
    margin-bottom: 24px;
  }
  
  .form {
    display: flex;
    flex-direction: column;
    gap: 20px;
    max-width: 500px;
  }
  
  .form label {
    display: flex;
    flex-direction: column;
  }
  
  .actions {
    display: flex;
    gap: 10px;
  }
  </style>