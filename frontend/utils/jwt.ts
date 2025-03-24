// utils/jwt.ts
export function parseJwt(token: string): any {
    try {
      const payload = token.split('.')[1]
      return JSON.parse(atob(payload))
    } catch (e) {
      return {}
    }
  }