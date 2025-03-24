export const useLogout = () => {
    const auth = useAuth()
    const router = useRouter()
    const config = useRuntimeConfig()
  
    const logout = async () => {
      try {
        const res = await fetch(`${config.public.apiBase}/api/v1/user/logout`, {
          method: 'POST',
          credentials: 'include',
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
          },
        })
  
    
        auth.isLoggedIn.value = false
        auth.userType.value = null
  
        localStorage.removeItem('accessToken')
  
        await router.push('/')
      } catch (e) {
        console.error('Logout failed:', e)
      }
    }
  
    return { logout }
  }