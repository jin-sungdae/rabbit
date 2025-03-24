
import { useState } from '#app'

export const useAuth = () => {
    const isLoggedIn = useState<boolean>('auth:isLoggedIn', () => false)
    const userType = useState<'SELLER' | 'BUYER' | null>('auth:userType', () => null)
    const userName = useState<string | null>('auth:userName', () => null)
    const isInitialized = useState<boolean>('auth:isInitialized', () => false)
  
    return {
      isLoggedIn,
      userType,
      userName,
      isInitialized
    }
  }
