import { parseJwt } from '@/utils/jwt'

export default defineNuxtPlugin(async (nuxtApp) => {
  const auth = useAuth()
  const config = useRuntimeConfig()
  const route = useRoute()

//   if (route.path === '/login') return
  try {


    const response = await fetch(`${config.public.apiBase}/me`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
    })

    const data = await response.json();

    console.log(data)

    if (response.ok && data.success) {
      auth.isLoggedIn.value = true
      auth.userType.value = data.data.role
      auth.userName.value = data.data.userName
    } else {
      auth.isLoggedIn.value = false
      auth.userType.value = null
      auth.userName.value = null
    }

  } catch (e) {
    console.warn('[auth plugin] 인증 정보 로딩 실패', e)
    auth.isLoggedIn.value = false
    auth.userType.value = null
    auth.userName.value = null
  } finally {
    auth.isInitialized.value = true
  }
})
