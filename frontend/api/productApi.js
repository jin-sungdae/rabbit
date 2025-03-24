import api from './base'

// 상품 탭 관련
export const fetchFeaturedProducts = () => api.get('/products/featured').then(res => res.data)
export const fetchLatestProducts = () => api.get('/products/latest').then(res => res.data)
export const fetchDiscountedProducts = () => api.get('/products/discounted').then(res => res.data)

// 태그 관련
export const fetchPopularTags = () => api.get('/tags/popular').then(res => res.data)
export const fetchProductsByTag = (tagName) => api.get(`/products?tag=${encodeURIComponent(tagName)}`).then(res => res.data)

// 브랜드 관련
export const fetchPopularBrands = () => api.get('/brands/popular').then(res => res.data)
export const fetchProductsByBrand = (brandId) => api.get(`/brands/${brandId}/products`).then(res => res.data)

// 시리즈 관련
export const fetchSeriesList = () => api.get('/series').then(res => res.data)
export const fetchProductsBySeries = (seriesId) => api.get(`/series/${seriesId}/products`).then(res => res.data)

// 전체 상품 목록 조회
export const fetchProducts = () => api.get('/brands/products').then(res => res.data)