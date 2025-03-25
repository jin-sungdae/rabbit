<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

onMounted(async () => {
  const router = useRouter()
  const code = new URLSearchParams(window.location.search).get('code')
  if (!code) {
    return router.push('/login')
  }

  const config = useRuntimeConfig()
  const response = await fetch(`${config.public.apiBase}/oauth2/callback/kakao?code=${code}`, {
    method: 'GET',
    credentials: 'include'
  })

  if (response.ok) {
    // JWT는 HttpOnly 쿠키로 들어올 것
    router.push('/')
  } else {
    console.error('OAuth2 로그인 실패')
    router.push('/login')
  }
})
</script>

<template>
  <div>카카오 로그인 중입니다...</div>
</template>