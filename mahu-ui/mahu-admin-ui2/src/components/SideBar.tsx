import { filterMenus, MENUS } from '@/config/menu';
import { useProfileStore } from '@/stores';
import { Menu } from '@arco-design/web-react';
import { css } from '@styled-system/css';
import { hstack } from '@styled-system/patterns';
import { ReactNode, useMemo, useState } from 'react';
import { NavLink } from 'react-router';
import { useShallow } from 'zustand/shallow';

export const SideBar = () => {
  const { nickname, avatar, permits, shops } = useProfileStore(
    useShallow((state) => ({
      nickname: state.nickname,
      avatar: state.avatar,
      permits: state.permits,
      shops: state.shops,
    }))
  );

  // 系统菜单
  const sysMenus = useMemo(() => filterMenus(MENUS, ['*']), [permits]);
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

  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const renderItems = (menus: typeof sysMenus) => {
    return menus.map((m) => {
      if (m.type === 'group') {
        return (
          <Menu.ItemGroup title={renderItem(m.name, m.icon)}>{m.children && renderItems(m.children)}</Menu.ItemGroup>
        );
      } else {
        return (
          <Menu.Item key={m.path || m.name}>
            <NavLink to={m.path!}> {renderItem(m.name, m.icon)}</NavLink>
          </Menu.Item>
        );
      }
    });
  };

  return (
    <Menu selectable selectedKeys={selectedKeys}>
      {renderItems(sysMenus)}
    </Menu>
  );
};
