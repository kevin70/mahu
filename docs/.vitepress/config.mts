import { defineConfig } from 'vitepress';
import { withMermaid } from 'vitepress-plugin-mermaid';
import { tasklist } from '@mdit/plugin-tasklist';
import { mark } from '@mdit/plugin-mark';

// https://vitepress.dev/reference/site-config
export default withMermaid(
  defineConfig({
    lang: 'zh-CN',
    title: 'Mahu 文档',
    description: 'Mahu 开发文档',
    lastUpdated: true,
    markdown: {
      lineNumbers: true,
      config: (md) => {
        md.use(tasklist).use(mark);
      },
      container: {
        tipLabel: '提示',
        warningLabel: '警告',
        dangerLabel: '危险',
        infoLabel: '信息',
        detailsLabel: '详细信息',
      },
    },
    themeConfig: {
      logo: '/assets/logo.svg',
      outlineTitle: '本页目录',
      lastUpdatedText: '最后更新',
      darkModeSwitchLabel: '外观',
      sidebarMenuLabel: '菜单',
      returnToTopLabel: '返回顶部',
      docFooter: {
        prev: '上一页',
        next: '下一页',
      },
      footer: {
        message: '',
        copyright: `Copyright © 2025-${new Date().getFullYear()} houge.cool All Rights Reserved`,
      },
      search: {
        provider: 'local',
        options: {
          translations: {
            button: {
              buttonText: '搜索',
              buttonAriaLabel: '搜索',
            },
            modal: {
              displayDetails: '显示详细列表',
              resetButtonTitle: '重置搜索',
              backButtonTitle: '关闭搜索',
              noResultsText: '没有结果',
              footer: {
                selectText: '选择',
                selectKeyAriaLabel: '输入',
                navigateText: '导航',
                navigateUpKeyAriaLabel: '上箭头',
                navigateDownKeyAriaLabel: '下箭头',
                closeText: '关闭',
                closeKeyAriaLabel: 'ESC',
              },
            },
          },
        },
      },

      // https://vitepress.dev/reference/default-theme-config
      nav: [
        { text: '首页', link: '/' },
        { text: '开发准备', link: '/开发准备' },
        { text: 'Examples', link: '/markdown-examples' },
      ],

      sidebar: [
        {
          text: 'Examples',
          items: [
            { text: 'Markdown Examples', link: '/markdown-examples' },
            { text: 'Runtime API Examples', link: '/api-examples' },
            { text: '变更记录', link: '/CHANGELOG' },
          ],
        },
        {
          text: 'OpenAPI',
          items: [
            { text: 'OpenAPI 规范', link: '/openapi/component_spec' },
            { text: 'OpenAPI 最佳实践指南', link: '/openapi/OpenAPI 最佳实践指南' },
          ],
        },
      ],
    },
  })
);
