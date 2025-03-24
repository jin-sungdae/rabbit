import { parseJwt } from '@/utils/jwt'

export default defineNuxtPlugin(async (nuxtApp) => {
  const auth = useAuth()
  const config = useRuntimeConfig()
  const route = useRoute()

//   if (route.path === '/login') return
  try {

    console.log('adfasf')
    const { data } = await useFetch(`${config.public.apiBase}/me`, {
    credentials: 'include',
    })

    console.log(data)

    if (data.value) {
      auth.isLoggedIn.value = true
      auth.userType.value = data.value.data.role
      auth.userName.value = data.value.data.userName
    } else {
      // 서버 응답 없으면 초기화
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
