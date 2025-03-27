
import { useState } from '#app'

export const useAuth = () => {
    const isLoggedIn = useState<boolean>('auth:isLoggedIn', () => false)
    const userType = useState<'SELLER' | 'BUYER' | null>('auth:userType', () => null)
    const userName = useState<string | null>('auth:userName', () => null)
    const isInitialized = useState<boolean>('auth:isInitialized', () => false)
  

    async function fetchUser() {
        try {
        const config = useRuntimeConfig()
        const res = await fetch(`${config.public.apiBase}/api/v1/user/me`, {
            credentials: 'include'
        })
        const data = await res.json()
        console.log('asdfasdfas')

        if (res.ok && data.success) {
            isLoggedIn.value = true
            userType.value = data.data.role
            userName.value = data.data.userName
        } else {
            isLoggedIn.value = false
            userType.value = null
            userName.value = null
        }
        } catch (err) {
        isLoggedIn.value = false
        userType.value = null
        userName.value = null
        }
    }

    return {
      isLoggedIn,
      userType,
      userName,
      isInitialized,
      fetchUser
    }
  }
