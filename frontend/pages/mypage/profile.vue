
  <template>
    <section>
      <h2 class="section-title">내 정보 수정</h2>
  
      <form class="profile-form" @submit.prevent="submitProfile">
        <label>
          이름
          <input v-model="form.userName" type="text" placeholder="이름을 입력하세요" />
        </label>
  
        <label>
          이메일
          <input v-model="form.email" type="email" placeholder="이메일을 입력하세요" />
        </label>
  
        <label>
          연락처
          <input v-model="form.phoneNo" type="tel" placeholder="전화번호를 입력하세요" />
        </label>
  
        <label>
          생년월일
          <input v-model="form.birthday" type="date" />
        </label>
  
        <label>
          우편번호
          <input v-model="form.zipcode" type="text" />
        </label>
  
        <label>
          주소 1
          <input v-model="form.address1" type="text" />
        </label>
  
        <label>
          주소 2
          <input v-model="form.address2" type="text" />
        </label>
  
        <label>
          메일 수신 동의
          <input type="checkbox" v-model="form.isMailing" />
        </label>
  
        <label>
          문자 수신 동의
          <input type="checkbox" v-model="form.isSms" />
        </label>
  
        <button :disabled="isSubmitting" type="submit">저장하기</button>
      </form>

      <div v-if="auth.userType.value === 'USER'" class="seller-section">
        <hr class="divider" />
        <h3 class="seller-title">판매자 신청</h3>
        <p class="info">상품을 판매하려면 판매자로 등록이 필요합니다.</p>
        <NuxtLink to="/mypage/apply-seller" class="apply-button">판매자 신청하기</NuxtLink>
      </div>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useAuth } from '@/composables/useAuth'
  
  const auth = useAuth()
  const config = useRuntimeConfig()
  
  const form = ref({
    userName: '',
    email: '',
    phoneNo: '',
    birthday: '',
    zipcode: '',
    address1: '',
    address2: '',
    isMailing: true,
    isSms: true
  })
  
  onMounted(async () => {

    const response = await fetch(`${config.public.apiBase}/api/v1/user/profile`, {
        method: 'GET',
        headers: {'Content-Type' : 'application/json'},
        credentials: 'include'
    })

    const data = await response.json();
 
    console.log(data)
    if (data.data) {
      Object.assign(form.value, data.data)
    }
  })

  const isSubmitting = ref(false)

  
  const submitProfile = async () => {
  if (isSubmitting.value) return
  isSubmitting.value = true

  try {
    const response = await fetch(`${config.public.apiBase}/api/v1/user/profile`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(form.value)
    })

    const data = await response.json();
    console.log('adfasdf', data)

    if (response.ok) {
      alert('정보가 수정되었습니다.')
    } else {
      alert('수정 실패')
    }
  } catch (err) {
    alert('네트워크 오류 발생')
  } finally {
    isSubmitting.value = false
  }
}
  </script>
  
  <style scoped>
  .section-title {
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 24px;
  }
  
  .profile-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
    max-width: 500px;
  }
  
  .profile-form label {
    display: flex;
    flex-direction: column;
    font-size: 14px;
    color: #444;
  }
  
  .profile-form input[type="text"],
  .profile-form input[type="email"],
  .profile-form input[type="tel"],
  .profile-form input[type="date"] {
    margin-top: 6px;
    padding: 10px 12px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 14px;
  }
  
  .profile-form input[type="checkbox"] {
    margin-top: 6px;
    width: 20px;
    height: 20px;
  }
  
  .profile-form button {
    padding: 12px;
    background-color: #2d6cdf;
    color: white;
    font-size: 15px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.2s;
  }
  
  .profile-form button:hover {
    background-color: #1f4ebb;
  }

  .seller-section {
    margin-top: 40px;
  }
  
  .divider {
    margin: 40px 0 20px;
    border: none;
    border-top: 1px solid #ddd;
  }
  
  .seller-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 10px;
  }

    
  .info {
    font-size: 14px;
    color: #555;
    margin-bottom: 12px;
  }
  
  .apply-button {
    display: inline-block;
    padding: 10px 16px;
    background-color: #0c7b5e;
    color: white;
    border-radius: 6px;
    text-decoration: none;
    font-size: 14px;
    font-weight: 500;
  }
  
  .apply-button:hover {
    background-color: #055f46;
  }
  </style>
  