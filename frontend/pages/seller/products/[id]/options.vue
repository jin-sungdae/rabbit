<template>
    <div class="product-option-form">
      <h2 class="title">옵션 및 재고 등록</h2>
  
      <form @submit.prevent="submitOptions">
        <div v-for="(group, index) in optionGroups" :key="index" class="option-group">
          <label>
            옵션 그룹명
            <input v-model="group.name" placeholder="예: 색상, 사이즈" required />
          </label>
  
          <label>
            옵션 항목 (쉼표로 구분)
            <input v-model="group.values" placeholder="예: 빨강, 파랑" required />
          </label>
  
          <button type="button" @click="removeGroup(index)">삭제</button>
        </div>
  
        <button type="button" @click="addGroup">옵션 그룹 추가</button>
        <button type="submit">재고 조합 만들기</button>
      </form>
  
      <div v-if="variants.length" class="variant-table">
        <h3>조합별 재고 입력</h3>
        <table>
          <thead>
            <tr>
              <th v-for="group in optionGroups" :key="group.name">{{ group.name }}</th>
              <th>재고</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(variant, idx) in variants" :key="idx">
              <td v-for="value in variant.options" :key="value">{{ value }}</td>
              <td><input type="number" v-model.number="variant.stock" /></td>
            </tr>
          </tbody>
        </table>
  
        <button @click="submitVariants">저장</button>
      </div>
    </div>
  </template>

<script setup>
import { ref } from 'vue'

definePageMeta({ layout: 'seller' })

const optionGroups = ref([
  { name: '', values: '' }
])

const variants = ref([])

const addGroup = () => {
  optionGroups.value.push({ name: '', values: '' })
}

const removeGroup = (index) => {
  optionGroups.value.splice(index, 1)
}

const submitOptions = () => {
  const parsedOptions = optionGroups.value.map(group => group.values.split(',').map(v => v.trim()))
  const combinations = generateCombinations(parsedOptions)

  variants.value = combinations.map(comb => ({
    options: comb,
    stock: 0
  }))
}

// 조합 생성
function generateCombinations(arr, prefix = []) {
  if (!arr.length) return [prefix]
  const [head, ...rest] = arr
  return head.flatMap(v => generateCombinations(rest, [...prefix, v]))
}

const submitVariants = async () => {
  const payload = variants.value.map(v => ({
    options: v.options,
    stock: v.stock
  }))

  console.log('등록할 조합:', payload)
  // TODO: API 연동
}
</script>