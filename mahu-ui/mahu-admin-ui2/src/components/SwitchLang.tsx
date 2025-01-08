import { useAppStore } from '@/stores';
import { Button, Dropdown, Menu, Select } from '@arco-design/web-react';
import { useShallow } from 'zustand/shallow';
import { IconLanguage } from '@arco-design/web-react/icon';

export const SwitchLang = () => {
  const { lng, changeLng } = useAppStore(useShallow((state) => ({ lng: state.lng, changeLng: state.changeLng })));

  return (
    <Dropdown
      position={'bl'}
      trigger={'click'}
      droplist={
        <Menu>
          <Menu.Item
            key="zh-CN"
            onClick={() => {
              changeLng('zh-CN');
            }}
          >
            中文简体
          </Menu.Item>
          <Menu.Item
            key="en-US"
            onClick={() => {
              changeLng('en-US');
            }}
          >
            English
          </Menu.Item>
        </Menu>
      }
    >
      <Button shape="circle" icon={<IconLanguage fontSize={20} />} />
    </Dropdown>
  );
};
