export default defineNuxtRouteMiddleware(async (to, from) => {
    const auth = useAuth()
  
    // if (!auth.isInitialized.value) {
    //   await auth.fetchUser()
    // }

    console.log('aaaaaa', auth.userName.value)
  
    if (!auth.isLoggedIn.value) {
      return navigateTo('/login')
    }
  
    if (to.path.startsWith('/seller') && auth.userType.value !== 'SELLER') {
      return navigateTo('/mypage')
    }
  
    if (to.path.startsWith('/mypage') && auth.userType.value === 'SELLER') {
      return navigateTo('/seller/mypage')
    }
  })