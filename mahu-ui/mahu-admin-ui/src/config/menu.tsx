import { MenuDataItem } from '@ant-design/pro-components';
import { permits } from './permit';

import { AuditOutlined, DashboardOutlined, GroupOutlined, ProductOutlined, UserOutlined } from '@ant-design/icons';
import { BsJournalText } from 'react-icons/bs';
import { FaRegFile, FaTasks } from 'react-icons/fa';
import { FiFileText } from 'react-icons/fi';
import { LuListTree, LuPlus, LuStore } from 'react-icons/lu';
import { MdOutlineCategory, MdSecurity } from 'react-icons/md';
import { TiDeviceDesktop } from 'react-icons/ti';
import { VscSignIn } from 'react-icons/vsc';

export const MENUS: MenuDataItem[] = [
  {
    path: '/dashboard',
    icon: <DashboardOutlined />,
    name: '仪表盘',
  },
  {
    path: '/mart/product-new',
    icon: <LuPlus />,
    name: '新建产品',
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
        path: '/mart/shop-list',
        icon: <LuStore />,
        name: '商店列表',
        permits: [permits.MART_SHOP.R],
      },
      {
        path: '/mart/category-list',
        icon: <MdOutlineCategory />,
        name: '产品分类',
        permits: [permits.MART_ASSET.R],
      },
      {
        path: '/mart/asset-list',
        icon: <FaRegFile />,
        name: '产品图片',
        permits: [permits.MART_ASSET.R],
      },
      {
        path: '/mart/attribute-list',
        icon: <LuListTree />,
        name: '产品属性',
        permits: [permits.MART_ATTRIBUTE.R],
      },
      {
        path: '/mart/product-list',
        icon: <ProductOutlined />,
        name: '产品列表',
        permits: [permits.MART_ASSET.R],
      },
    ],
  },
  {
    name: '系统管理',
    type: 'group',
    atLeastOneChild: true,
    children: [
      {
        path: '/system/admin-list',
        icon: <UserOutlined />,
        name: '管理员列表',
        permits: [permits.ADMIN.R],
      },
      {
        path: '/system/department-list',
        icon: <GroupOutlined />,
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
        icon: <TiDeviceDesktop />,
        name: '终端配置',
        permits: [permits.CLIENT.R],
      },
      {
        path: '/system/scheduled-tasks',
        icon: <FaTasks />,
        name: '定时任务',
        permits: [permits.SCHEDULED_TASK.R],
      },
      {
        path: '/logs/admin-access-logs',
        icon: <BsJournalText />,
        name: '访问记录',
        permits: [permits.ACCESS_LOG.R],
      },
      {
        path: '/logs/admin-audit-logs',
        icon: <AuditOutlined />,
        name: '操作审计',
        permits: [permits.AUDIT_JOUR.R],
      },
      {
        path: '/logs/admin-auth-logs',
        icon: <VscSignIn />,
        name: '登录记录',
        permits: [permits.AUDIT_JOUR.R],
      },
    ],
  },
];

export const filterMenus = (items: any, codes: string[]) => {
  const list: MenuDataItem[] = [];
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

    const m: MenuDataItem = {
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
