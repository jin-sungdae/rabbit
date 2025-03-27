import { defineNuxtPlugin } from '#app'
import VMdEditor from '@kangc/v-md-editor'
import GithubTheme from '@kangc/v-md-editor/lib/theme/github.js'
import '@kangc/v-md-editor/lib/style/base-editor.css'
import '@kangc/v-md-editor/lib/theme/style/github.css'
import Prism from 'prismjs'

export default defineNuxtPlugin((nuxtApp) => {
  VMdEditor.use(GithubTheme, { Prism })
  nuxtApp.vueApp.use(VMdEditor)
})