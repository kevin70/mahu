import { defineConfig } from 'vitepress';
import { withMermaid } from 'vitepress-plugin-mermaid';
import { tasklist } from '@mdit/plugin-tasklist';
import { mark } from '@mdit/plugin-mark';
import { icon, iconfontRender } from '@mdit/plugin-icon';
import { embed } from '@mdit/plugin-embed';
import { configureDiagramsPlugin } from 'vitepress-plugin-diagrams';

// https://vitepress.dev/reference/site-config
export default withMermaid(
  defineConfig({
    srcDir: './src',
    lang: 'zh-CN',
    title: 'Mahu',
    description: 'Mahu | Houge',
    lastUpdated: true,
    head: [
      // Iconfont
      [
        'link',
        {
          rel: 'stylesheet',
          type: 'text/css',
          href: 'https://at.alicdn.com/t/c/font_4945214_pg0yh7hfaln.css',
        },
      ],
    ],
    markdown: {
      lineNumbers: true,
      config: (md) => {
        md.use(tasklist)
          .use(mark)
          .use(icon, { render: iconfontRender })
          .use(embed, {
            config: [
              // Bilibili
              {
                name: 'bilibili',
                setup: (bvid) =>
                  `<iframe src="https://player.bilibili.com/player.html?isOutside=true&bvid=${bvid}" width="560" height="315" frameborder="0" allowfullscreen></iframe>`,
              },
            ],
          });

        configureDiagramsPlugin(md, {
          diagramsDir: 'src/public/diagrams', // Optional: custom directory for SVG files
          publicPath: '/diagrams', // Optional: custom public path for images
        });
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
      logo: '/logo.svg',
      outline: {
        label: '本页目录',
      },
      lastUpdated: {
        text: '最后更新',
      },
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
        { text: 'OpenAPI', link: '/openapi' },
        { text: '开发准备', link: '/开发准备' },
        {
          text: '开发规范',
          items: [
            {
              text: '角色定义',
              link: '/角色定义',
            },
            {
              text: "OpenAPI",
              link: "/openapi"
            },
            {
              text: '性能优化原则',
              link: '/性能优化原则',
            },
            {
              text: '数据状态字典',
              link: '/数据状态字典',
            },
            {
              text: '标准化：现代软件开发的效率引擎',
              link: '/标准化_现代软件开发的效率引擎',
            },
            {
              text: '数据库规范',
              link: '/database_standards',
            },
          ],
        },
        { text: '环境配置', link: '/env_configuration' },
      ],
      //
    },
  })
);
