import { useProfileStore } from '@/stores';
import { ShopOutlined } from '@ant-design/icons';
import { Button, Dropdown, MenuProps } from 'antd';
import { useMemo } from 'react';
import { useShallow } from 'zustand/shallow';

export const HSwitchShop = () => {
  const { shopId, shops, setShopId } = useProfileStore(
    useShallow((state) => ({
      shopId: state.shopId,
      shops: state.shops,
      setShopId: state.setShopId,
    }))
  );

  // 商店选择器
  const shopLabel = useMemo(() => {
    return shops.find((o) => o.id === shopId)?.name || '切换商店';
  }, [shopId, shops]);

  if (shops.length <= 0) {
    return <></>;
  }

  const items: MenuProps['items'] = shops.map((o) => ({ key: o.id, label: o.name }));
  return (
    <Dropdown
      trigger={['click']}
      menu={{
        items,
        selectable: true,
        selectedKeys: [`${shopId}`],
        onSelect(info) {
          setShopId(parseInt(info.key));
        },
      }}
      arrow
    >
      <Button type="text" icon={<ShopOutlined />}>
        {shopLabel}
      </Button>
    </Dropdown>
  );
};
