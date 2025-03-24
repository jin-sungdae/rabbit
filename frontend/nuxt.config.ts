export default defineNuxtConfig({
  runtimeConfig: {
    public: {
      apiBase: process.env.API_BASE || 'http://localhost:1113', // 백엔드 API 설정
    }
  },

  css: ['@/assets/styles.css'],
  modules: ['@nuxt/ui'],
  compatibilityDate: '2025-03-17',
})