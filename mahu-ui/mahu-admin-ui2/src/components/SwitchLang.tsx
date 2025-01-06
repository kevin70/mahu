import IonLanguage from '@/icons/IonLanguage';
import { useAppStore } from '@/stores';
import { Button, Dropdown, Menu } from '@arco-design/web-react';
import { useMemo } from 'react';
import { useShallow } from 'zustand/shallow';

export const SwitchLang = () => {
  const { lng, changeLng } = useAppStore(useShallow((state) => ({ lng: state.lng, changeLng: state.changeLng })));
  const selectedKeys = useMemo(() => {
    return [lng];
  }, [lng]);

  return (
    <Dropdown
      droplist={
        <Menu
          selectable
          selectedKeys={selectedKeys}
          onClickMenuItem={(key: any) => {
            changeLng(key);
          }}
        >
          <Menu.Item key="zh-CN">中文简体</Menu.Item>
          <Menu.Item key="en-US">English</Menu.Item>
        </Menu>
      }
      trigger={'click'}
      position="bl"
    >
      <Button shape="circle">
        <IonLanguage width={'2rem'} />
      </Button>
    </Dropdown>
  );
};
