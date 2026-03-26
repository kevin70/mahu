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
    // 保持为空：避免模板外链误导；如需入口可替换为公司内 Git/知识库链接
  },
  github: {
    rootDir: 'docs',
  },
  assistant: {
    faqQuestions: [
      { category: '入门', items: [
        '如何搭建 Mahu 本地开发环境？',
        '各模块（mahu-web / mahu-domain / mahu-db / mahu-task 等）职责是什么？',
        'Spotless/测试/OpenAPI 生成的常用命令有哪些？',
      ] },
      { category: '数据库', items: [
        '如何为数据库变更编写 Liquibase changeSet，并提供可用的 rollback？',
        '数据库 schema 变更应该放在哪个模块？',
        '如何确保数据库字段与实体/查询条件的一致性？',
      ] },
      { category: 'AI 功能', items: [
        '如何通过 MCP Server 将 Mahu 文档接入 Cursor/VS Code 等工具？',
        'MCP 的访问地址是什么，如何验证连通性？',
        'llms.txt / llms-full.txt 用于什么场景？',
      ] },
    ],
  },
  toc: {
    bottom: {
      links: [
        {
          icon: 'i-lucide-book-open',
          label: '本仓库文档入口（content/）',
          to: '/getting-started',
          target: undefined,
        },
        {
          icon: 'i-lucide-book-open',
          label: '数据库规范',
          to: '/architecture/database-spec',
          target: undefined,
        },
        {
          icon: 'i-lucide-cpu',
          label: 'MCP 接入说明',
          to: '/ai/mcp',
          target: undefined,
        },
        {
          icon: 'i-lucide-book-open',
          label: 'Nuxt Content 参考（外链）',
          to: 'https://content.nuxt.com/docs/getting-started/installation/',
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
