import { ReactElement, ReactNode, ReactSVGElement } from 'react';
import {
  IconApps,
  IconBold,
  IconBook,
  IconCode,
  IconCommand,
  IconUser,
  IconUserGroup,
} from '@arco-design/web-react/icon';
import { permits } from './permit';
import MaterialSymbolsBrandFamilyOutline from '@/icons/MaterialSymbolsBrandFamilyOutline';
import MaterialSymbolsWebAsset from '@/icons/MaterialSymbolsWebAsset';
import CarbonUserRole from '@/icons/CarbonUserRole';
import IconParkOutlineShop from '@/icons/IconParkOutlineShop';

type MenuItem = {
  name: string;
  path?: string;
  icon?: ReactElement;
  type?: 'group';
  atLeastOneChild?: boolean;
  permits?: string[];
  children?: MenuItem[];
};

export const MENUS: MenuItem[] = [
  {
    path: '/dashboard',
    icon: <IconApps />,
    name: '仪表盘',
  },
  {
    name: '基础数据',
    type: 'group',
    atLeastOneChild: true,
    children: [
      {
        path: '/brand-list',
        icon: <MaterialSymbolsBrandFamilyOutline />,
        name: '品牌管理',
        permits: [permits.BRAND.R],
      },
    ],
  },
  {
    name: '商城管理',
    type: 'group',
    atLeastOneChild: true,
    children: [
      {
        path: '/market/shop-list',
        icon: <IconParkOutlineShop />,
        name: '商店列表',
        permits: [permits.MARKET_SHOP.R],
      },
      {
        path: '/market/asset-list',
        icon: <MaterialSymbolsWebAsset />,
        name: '商店资源',
        permits: [permits.MARKET_ASSET.R],
      },
    ],
  },
  {
    name: '系统管理',
    type: 'group',
    atLeastOneChild: true,
    children: [
      {
        path: '/system/employee-list',
        icon: <IconUser />,
        name: '职员列表',
        permits: [permits.EMPLOYEE.R],
      },
      {
        path: '/system/department-list',
        icon: <IconUserGroup />,
        name: '部门列表',
        permits: [permits.DEPARTMENT.R],
      },
      {
        path: '/system/dict-list',
        icon: <IconBook />,
        name: '字典列表',
        permits: [permits.DICT.R],
      },
      {
        path: '/system/role-list',
        icon: <CarbonUserRole />,
        name: '角色列表',
        permits: [permits.ROLE.R],
      },
      {
        path: '/system/client-list',
        icon: <IconCode />,
        name: '终端配置',
        permits: [permits.CLIENT.R],
      },
      {
        path: '/system/access-log-list',
        icon: <IconCommand />,
        name: '访问记录',
        permits: [permits.ACCESS_LOG.R],
      },
      {
        path: '/system/audit-jour-list',
        icon: <IconBold />,
        name: '操作审计',
        permits: [permits.AUDIT_JOUR.R],
      },
    ],
  },
];

export const filterMenus = (items: any, codes: string[]) => {
  const list: MenuItem[] = [];
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

    const m: MenuItem = {
      path: item.path,
      icon: item.icon,
      name: item.name,
      type: item.type,
    };
    if (item.children) {
      m.children = filterMenus(item.children, codes);
    }

    // 强制要求子节点的菜单，在没有子节点的情况下不显示
    if (m.atLeastOneChild && (!m.children || m.children.length <= 0)) {
      continue;
    }
    list.push(m);
  }
  return list;
};
