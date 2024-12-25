import { filterMenus, MENUS } from '@/config/menu';
import { Nav } from '@douyinfe/semi-ui';
import { NavProps } from '@douyinfe/semi-ui/lib/es/navigation';
import { useMemo } from 'react';
import { useNavigation } from 'react-router';

export const SiderNav = (props: NavProps) => {
  const menus = useMemo(() => {
    return filterMenus(MENUS, ['*']);
  }, []);

  console.log('menus', menus);
  const navigation = useNavigation();

  const selectedKeys = useMemo(() => {
    console.log('navigation', navigation.location);
    return [navigation.location?.pathname || '/dashboard'];
  }, [navigation]);

  return (
    <Nav
      {...props}
      items={menus}
      selectedKeys={selectedKeys}
      onSelect={(key) => console.log(key)}
      footer={{
        collapseButton: true,
      }}
    />
  );
};
