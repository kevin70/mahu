export default defineNuxtConfig({
  extends: ['docus'],
  css: ['~/assets/css/main.css'],
  site: {
    name: 'Mahu 开发文档',
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
    domain: 'https://docus.dev',
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
