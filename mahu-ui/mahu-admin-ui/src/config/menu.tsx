import { NavItemPropsWithItems, NavItems, SubNavPropsWithItems } from '@douyinfe/semi-ui/lib/es/navigation';
import { permits } from './permit';
import MaterialSymbolsBrandFamilyOutline from '@/icons/MaterialSymbolsBrandFamilyOutline';
import {
  IconCode,
  IconInfoCircle,
  IconShoppingBag,
  IconUser,
  IconUserCardVideo,
  IconUserGroup,
} from '@douyinfe/semi-icons';
import MaterialSymbolsWebAsset from '@/icons/MaterialSymbolsWebAsset';
import MaterialSymbolsDictionaryOutline from '@/icons/MaterialSymbolsDictionaryOutline';
import CarbonUserRole from '@/icons/CarbonUserRole';
import { ReactNode } from 'react';

export type MenuItem = {
  link?: string;
  text: string;
  icon?: ReactNode;
  isOpen?: boolean;
  atLeastOneChild?: true;
  permits?: string[];
  items?: MenuItem[];
};

export type MenuItems = MenuItem[];

export const MENUS: MenuItems = [
  {
    link: '/dashboard',
    text: '仪表盘',
  },
  {
    text: '基础数据',
    isOpen: true,
    atLeastOneChild: true,
    items: [
      {
        link: '/brand-list',
        icon: <MaterialSymbolsBrandFamilyOutline />,
        text: '品牌管理',
        permits: [permits.BRAND.R],
      },
    ],
  },
  {
    text: '商城管理',
    atLeastOneChild: true,
    isOpen: true,
    items: [
      {
        link: '/market/shop-list',
        icon: <IconShoppingBag />,
        text: '商店列表',
        permits: [permits.MARKET_SHOP.R],
      },
      {
        link: '/market/asset-list',
        text: '商店资源',
        icon: <MaterialSymbolsWebAsset />,
        permits: [permits.MARKET_ASSET.R],
      },
    ],
  },
  {
    text: '系统管理',
    isOpen: true,
    atLeastOneChild: true,
    items: [
      {
        link: '/system/employee-list',
        text: '职员列表',
        icon: <IconUser />,
        permits: [permits.EMPLOYEE.R],
      },
      {
        link: '/system/department-list',
        text: '部门列表',
        icon: <IconUserGroup />,
        permits: [permits.DEPARTMENT.R],
      },
      {
        link: '/system/dict-list',
        text: '字典列表',
        icon: <MaterialSymbolsDictionaryOutline />,
        permits: [permits.DICT.R],
      },
      {
        link: '/system/role-list',
        text: '角色列表',
        icon: <CarbonUserRole />,
        permits: [permits.ROLE.R],
      },
      {
        link: '/system/client-list',
        text: '终端配置',
        icon: <IconCode />,
        permits: [permits.CLIENT.R],
      },
      {
        link: '/system/access-log-list',
        text: '访问记录',
        icon: <IconInfoCircle />,
        permits: [permits.ACCESS_LOG.R],
      },
      {
        link: '/system/audit-jour-list',
        text: '操作审计',
        icon: <IconUserCardVideo />,
        permits: [permits.AUDIT_JOUR.R],
      },
    ],
  },
];

export const filterMenus = (items: MenuItems, codes: string[]) => {
  const list: NavItems = [];

  for (const item of items) {
    if (codes.indexOf('*') == -1 && item.permits) {
      let exists = false;
      for (const permit of item.permits) {
        if (codes.indexOf(permit) >= 0) {
          exists = true;
          break;
        }
      }

      // 不存在权限不显示菜单
      if (!exists) {
        continue;
      }
    }

    const m: NavItemPropsWithItems = {
      itemKey: item.link || item.text,
      link: item.link,
      text: item.text,
      icon: item.icon,
    };

    if (item.items) {
      m.items = filterMenus(item.items, codes);
    }

    // 强制要求子节点的菜单，在没有子节点的情况下不显示
    if (item.atLeastOneChild && (!m.items || m.items.length <= 0)) {
      continue;
    }
    list.push(m);
  }
  return list;
};
