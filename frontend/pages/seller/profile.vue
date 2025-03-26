<template>
    <section>
      <h2 class="title">상점 프로필 관리</h2>
  
      <form class="seller-form" @submit.prevent="updateProfile">
        <label>
            상점 이름
            <input v-model="form.shopName" type="text" placeholder="상점 이름을 입력하세요" />
        </label>

        <label>
            연락처
            <input v-model="form.contact" type="tel" placeholder="전화번호를 입력하세요" />
        </label>

        <label>
            소개글
            <textarea v-model="form.description" rows="4" placeholder="상점 소개를 입력하세요"></textarea>
        </label>
  
        <button type="submit">수정하기</button>
      </form>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useAuth } from '@/composables/useAuth'
  import { useRouter } from 'vue-router'
  definePageMeta({
  layout: 'seller'
})
  const config = useRuntimeConfig()
  const router = useRouter()
  const auth = useAuth()
  
  const form = ref({
    shopName: '',
    contact: '',
    description: ''
  })
  
  const isSubmitting = ref(false)
  
  const fetchSellerProfile = async () => {
    const response = await fetch(`${config.public.apiBase}/api/v1/seller/profile`, {
      method: 'GET',
      credentials: 'include'
    })
  
    const result = await response.json()
    if (response.ok && result.data) {
      form.value = result.data
    }
  }
  
  const updateProfile = async () => {
    if (isSubmitting.value) return
    isSubmitting.value = true
  
    try {
      const response = await fetch(`${config.public.apiBase}/api/v1/seller/profile`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(form.value)
      })
  
      if (response.ok) {
        alert('상점 정보가 수정되었습니다.')
        await auth.fetchUser()
      } else {
        alert('수정 실패')
      }
    } catch (err) {
      alert('네트워크 오류')
    } finally {
      isSubmitting.value = false
    }
  }
  
  onMounted(fetchSellerProfile)
  </script>
  
  <style scoped>
  .title {
    font-size: 22px;
    font-weight: 600;
    margin-bottom: 24px;
  }
  
  .seller-form {
        display: flex;
        flex-direction: column;
        gap: 20px;
        max-width: 500px;
    }

.seller-form label {
  display: flex;
  flex-direction: column;
  font-size: 14px;
  color: #444;
}

  .seller-form input,
.seller-form textarea {
  margin-top: 6px;
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
}

.seller-form button {
  padding: 12px;
  background-color: #0c7b5e;
  color: white;
  font-size: 15px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.seller-form button:hover {
  background-color: #055f46;
}
  </style>