const docsSiteUrl = (
  process.env.NUXT_SITE_URL ||
  process.env.NUXT_PUBLIC_SITE_URL ||
  ''
).replace(/\/$/, '')

const llmsDomain = docsSiteUrl || 'https://mahu-docs.invalid'

export default defineNuxtConfig({
  extends: ['docus'],
  css: ['~/assets/css/main.css'],
  site: {
    name: 'Mahu 开发文档',
    ...(docsSiteUrl ? { url: docsSiteUrl } : {}),
  },
  mdc: {
    highlight: {
      shikiEngine: 'javascript',
    },
  },
  compatibilityDate: '2026-03-26',
  fonts: {
    provider: "bunny",
  },
  devtools: {
    enabled: false 
  },
  vite: {
    build: {
      sourcemap: false,
    },
  },
  llms: {
    domain: llmsDomain,
    title: 'Mahu 开发文档',
    description: '面向 Mahu 内部开发者的项目总览、架构设计与数据库规范入口。',
    full: {
      title: 'Mahu 开发文档',
      description: '面向 Mahu 内部开发者的项目总览、架构设计与数据库规范入口。',
    },
  },
  mcp: {
    name: 'Mahu 开发文档',
    browserRedirect: '/ai/mcp',
  },
})
