export async function fetchWithAuth<T>(
    input: RequestInfo,
    init: RequestInit = {}
  ): Promise<T> {
    let response = await fetch(input, {
      ...init,
      credentials: 'include'
    });
  
    if (response.status === 401) {
      // 1차 실패 → refresh 시도
      const refresh = await fetch('/auth/refresh', {
        method: 'POST',
        credentials: 'include',
      });
  
      if (refresh.ok) {
        // 재시도
        response = await fetch(input, {
          ...init,
          credentials: 'include'
        });
      }
    }
  
    if (!response.ok) {
      const message = await response.text();
      throw new Error(`Fetch failed (${response.status}): ${message}`);
    }
  
    return response.json();
  }