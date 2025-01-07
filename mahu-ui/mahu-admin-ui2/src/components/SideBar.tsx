import { filterMenus, MENUS } from '@/config/menu';
import { useProfileStore } from '@/stores';
import { Menu } from '@arco-design/web-react';
import { hstack } from '@styled-system/patterns';
import { ReactNode, useMemo } from 'react';
import { Link, useLocation } from 'react-router';
import { useShallow } from 'zustand/shallow';

export const SideBar = () => {
  const permits = useProfileStore(useShallow((state) => state.permits));

  // 系统菜单
  const sysMenus = useMemo(() => filterMenus(MENUS, permits), [permits]);
  const renderItem = (name: string, icon?: ReactNode) => {
    return (
      <div
        className={hstack({
          alignItems: 'center',
          '& > svg': {
            mr: '0!',
          },
        })}
      >
        {icon}
        {name}
      </div>
    );
  };

  const location = useLocation();
  const renderItems = (menus: typeof sysMenus) => {
    return menus.map((m, i) => {
      if (m.type === 'group') {
        return (
          <Menu.ItemGroup key={`${i}-${m.name}`} title={renderItem(m.name, m.icon)}>
            {m.children && renderItems(m.children)}
          </Menu.ItemGroup>
        );
      } else {
        return (
          <Menu.Item key={m.path || `${i}-${m.name}`}>
            <Link to={m.path!}>
              <div
                className={hstack({
                  alignItems: 'center',
                  '& > svg': {
                    mr: '0!',
                  },
                })}
              >
                {m.icon}
                {m.name}
              </div>
            </Link>
          </Menu.Item>
        );
      }
    });
  };

  return <Menu selectedKeys={[location.pathname]}>{renderItems(sysMenus)}</Menu>;
};
