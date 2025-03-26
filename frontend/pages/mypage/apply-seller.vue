<template>
  <section>
    <h2 class="section-title">판매자 신청</h2>

    <form class="seller-form" @submit.prevent="submitSellerApplication">
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

      <button type="submit">신청하기</button>
    </form>
  </section>
</template>

<script setup>
import { ref } from 'vue'

const config = useRuntimeConfig()
const router = useRouter()
const { fetchUser } = useAuth()


const form = ref({
  shopName: '',
  contact: '',
  description: ''
})
const isSubmitting = ref(false)

const submitSellerApplication = async () => {
    if (isSubmitting.value) return
    isSubmitting.value = true

    try {
        const response = await fetch(`${config.public.apiBase}/api/v1/seller/apply`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(form.value)
        })

        const data = await response.json();
        console.log('adfasdf', data)

        if (response.ok) {
            await fetchUser() 
            alert('상점이 등록되었습니다.')
            await router.push('/mypage/profile') 
        } else {
            alert('등록 실패')
        }
    } catch (err) {
        alert('네트워크 오류 발생')
        console.error('[fetchUser 오류]', e)
    } finally {
        isSubmitting.value = false
    }
}
</script>

<style scoped>
.section-title {
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
