export default defineNuxtConfig({
  extends: ['docus'],
  modules: ['nuxt-studio'],
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
  studio: {
    route: '/admin',
    repository: {
      provider: 'github',
      owner: 'nuxt-content',
      repo: 'docus',
      rootDir: 'docs',
    },
  },
  mcp: {
    name: 'Mahu 开发文档',
    browserRedirect: '/ai/mcp',
  },
})
