import { ReactElement } from 'react';
import { permits } from './permit';
import { IconApps } from '@arco-design/web-react/icon';
import { LuStore } from 'react-icons/lu';
import { FaRegFile } from 'react-icons/fa';
import { FiFileText } from 'react-icons/fi';
import { MdSecurity, MdOutlineCategory, MdOutlineDashboardCustomize } from 'react-icons/md';
import { AiOutlineAudit } from 'react-icons/ai';
import { HiOutlineUserGroup } from 'react-icons/hi';
import { SlOrganization } from 'react-icons/sl';
import { BsJournalText } from 'react-icons/bs';

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
    icon: <MdOutlineDashboardCustomize />,
    name: '仪表盘',
  },
  {
    name: '基础数据',
    type: 'group',
    atLeastOneChild: true,
    children: [
      {
        path: '/brand-list',
        icon: <MdOutlineCategory />,
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
        icon: <LuStore />,
        name: '商店列表',
        permits: [permits.MARKET_SHOP.R],
      },
      {
        path: '/market/asset-list',
        icon: <FaRegFile />,
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
        icon: <HiOutlineUserGroup />,
        name: '职员列表',
        permits: [permits.EMPLOYEE.R],
      },
      {
        path: '/system/department-list',
        icon: <SlOrganization />,
        name: '部门列表',
        permits: [permits.DEPARTMENT.R],
      },
      {
        path: '/system/dict-list',
        icon: <FiFileText />,
        name: '字典列表',
        permits: [permits.DICT.R],
      },
      {
        path: '/system/role-list',
        icon: <MdSecurity />,
        name: '角色列表',
        permits: [permits.ROLE.R],
      },
      {
        path: '/system/client-list',
        icon: <IconApps />,
        name: '终端配置',
        permits: [permits.CLIENT.R],
      },
      {
        path: '/system/access-log-list',
        icon: <BsJournalText />,
        name: '访问记录',
        permits: [permits.ACCESS_LOG.R],
      },
      {
        path: '/system/audit-jour-list',
        icon: <AiOutlineAudit />,
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
