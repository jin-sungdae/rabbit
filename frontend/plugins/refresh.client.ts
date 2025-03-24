export default defineNuxtPlugin((nuxtApp) => {
    nuxtApp.hook('app:error', async (error) => {
      if (error.statusCode === 401) {
        try {
          // /refresh API 호출
          const { data } = await useFetch('/api/v1/user/refresh', {
            method: 'POST',
            credentials: 'include',
          })
  
          if (data.value?.success) {
            console.log('[Refresh] 새로운 accessToken 발급 완료')
  
            // 상태 복구 가능
            // 예: auth.isLoggedIn.value = true;
          } else {
            console.warn('[Refresh] 실패. 로그인 페이지로 이동')
            return navigateTo('/login')
          }
        } catch (e) {
          console.error('[Refresh] 오류:', e)
          return navigateTo('/login')
        }
      }
    })
  })