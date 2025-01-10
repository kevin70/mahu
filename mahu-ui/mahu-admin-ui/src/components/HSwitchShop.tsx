import { useProfileStore } from '@/stores';
import { ShopOutlined } from '@ant-design/icons';
import { Button, Dropdown, MenuProps } from 'antd';
import { useMemo } from 'react';
import { useShallow } from 'zustand/shallow';

export const HSwitchShop = () => {
  const { shops } = useProfileStore(
    useShallow((state) => ({
      shops: state.shops,
    }))
  );

  // 商店选择器
  const selectedShopId = 1;
  const shopLabel = useMemo(() => {
    return shops.find((o) => o.id === selectedShopId)?.name || '切换商店';
  }, [selectedShopId, shops]);

  if (shops.length <= 0) {
    return <></>;
  }

  const items: MenuProps['items'] = shops.map((o) => ({ key: o.id, label: o.name }));
  return (
    <Dropdown
      menu={{
        items,
        selectable: true,
        selectedKeys: [`${selectedShopId}`],
        onSelect(info) {
          // FIXME 选择商店
        },
      }}
      arrow
      trigger={['click']}
    >
      <Button type="text" icon={<ShopOutlined />}>
        {shopLabel}
      </Button>
    </Dropdown>
  );
};
