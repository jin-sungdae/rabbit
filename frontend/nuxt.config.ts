export default defineNuxtConfig({
  runtimeConfig: {
    public: {
      apiBase: process.env.API_BASE || 'http://localhost:1113',
      kakaoClientId: process.env.KAKAO_CLIENT_ID,
      googleClientId: process.env.GOOGLE_CLIENT_ID,
      naverClientId: process.env.NAVER_CLIENT_ID
    }
  },

  css: ['@/assets/styles.css'],
  modules: ['@nuxt/ui'],
  compatibilityDate: '2025-03-17',
})