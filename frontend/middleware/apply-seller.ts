// middleware/apply-seller.ts
export default defineNuxtRouteMiddleware(() => {
    const auth = useAuth()
    if (!auth.isInitialized.value) return // plugin 실행 전 방어
    if (auth.userType.value !== 'BUYER') return navigateTo('/mypage')
  })