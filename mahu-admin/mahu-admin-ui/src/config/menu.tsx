import {
  AuditOutlined,
  CodeOutlined,
  DashboardOutlined,
  FileOutlined,
  FlagOutlined,
  FolderOutlined,
  GroupOutlined,
  InfoCircleOutlined,
  KeyOutlined,
  ShopOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { MenuDataItem } from '@ant-design/pro-components';
import { permits } from './permit';

export const MENUS: MenuDataItem[] = [
  {
    path: '/dashboard',
    icon: <DashboardOutlined />,
    name: '仪表盘',
  },
  {
    name: '基础数据',
    type: 'group',
    atLeastOneChild: true,
    children: [
      {
        path: '/brand-list',
        icon: <FlagOutlined />,
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
        icon: <ShopOutlined />,
        name: '商店列表',
        permits: [permits.MARKET_SHOP.R],
      },
      {
        path: '/market/asset-list',
        icon: <FolderOutlined />,
        name: '商店资源',
        permits: [permits.MARKET_SHOP.R],
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
        icon: <UserOutlined />,
        name: '职员列表',
        permits: [permits.EMPLOYEE.R],
      },
      {
        path: '/system/department-list',
        icon: <GroupOutlined />,
        name: '部门列表',
        permits: [permits.DEPARTMENT.R],
      },
      {
        path: '/system/dict-list',
        icon: <FileOutlined />,
        name: '字典列表',
        permits: [permits.DICT.R],
      },
      {
        path: '/system/role-list',
        icon: <KeyOutlined />,
        name: '角色列表',
        permits: [permits.ROLE.R],
      },
      {
        path: '/system/client-list',
        icon: <CodeOutlined />,
        name: '终端配置',
        permits: [permits.CLIENT.R],
      },
      {
        path: '/system/access-log-list',
        icon: <InfoCircleOutlined />,
        name: '访问记录',
        permits: [permits.ACCESS_LOG.R],
      },
      {
        path: '/system/audit-jour-list',
        icon: <AuditOutlined />,
        name: '操作审计',
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
