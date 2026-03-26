export default defineAppConfig({
  header: {
    title: 'Mahu 开发文档',
    logo: {
      light: '/logo/logo-dark.svg',
      dark: '/logo/logo-light.svg',
      alt: 'Mahu Logo',
      wordmark: {
        light: '/logo/wordmark-dark.svg',
        dark: '/logo/wordmark-light.svg',
      },
      favicon: '/favicon.svg',
    },
  },
  socials: {
    docus: 'https://docus.dev/',
    nuxt: 'https://nuxt.com',
  },
  github: {
    rootDir: 'docs',
  },
  assistant: {
    faqQuestions: [
      { category: '入门', items: [
        '如何安装 Docus？',
        '项目的结构是什么？',
        '如何部署我的文档？',
      ] },
      { category: '自定义', items: [
        '如何自定义主题？',
        '如何添加自定义组件？',
        '如何配置 llms.txt 生成？',
      ] },
      { category: 'AI 功能', items: [
        '如何启用助手？',
        '什么是 MCP 服务器？',
        '如何生成 llms-full.txt 文件？',
      ] },
    ],
  },
  toc: {
    bottom: {
      links: [
        {
          icon: 'i-lucide-book-open',
          label: 'Nuxt UI docs',
          to: 'https://ui.nuxt.com/getting-started/installation/nuxt',
          target: '_blank',
        },
        {
          icon: 'i-lucide-book-open',
          label: 'Nuxt Content docs',
          to: 'https://content.nuxt.com/docs/getting-started/installation/',
          target: '_blank',
        },
        {
          icon: 'i-lucide-book-open',
          label: 'Nuxt Studio docs',
          to: 'https://nuxt.studio/introduction',
          target: '_blank',
        },
      ],
    },
  },
  ui: {
    pageHero: {
      slots: {
        title: 'font-semibold sm:text-6xl',
        container: '!pb-0',
      },
    },
    pageCard: {
      slots: {
        container: 'lg:flex min-w-0',
        wrapper: 'flex-none',
      },
    },
  },
})
