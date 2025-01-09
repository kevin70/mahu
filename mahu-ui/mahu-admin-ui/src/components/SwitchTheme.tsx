import { useAppStore } from '@/stores';
import { MoonOutlined, SunOutlined } from '@ant-design/icons';
import { Button } from 'antd';
import { useShallow } from 'zustand/shallow';

export const SwitchTheme = () => {
  const appStore = useAppStore();
  const isLightTheme = useAppStore(useShallow((state) => state.isLightTheme()));

  return (
    <Button
      type="text"
      shape="circle"
      icon={isLightTheme ? <SunOutlined /> : <MoonOutlined />}
      onClick={() => {
        appStore.changeTheme(isLightTheme ? 'dark' : 'light');
      }}
    />
  );
};
