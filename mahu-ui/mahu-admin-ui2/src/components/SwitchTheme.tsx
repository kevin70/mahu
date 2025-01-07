import { useAppStore } from '@/stores';
import { Button } from '@arco-design/web-react';
import { IconMoonFill, IconSunFill } from '@arco-design/web-react/icon';
import { useShallow } from 'zustand/shallow';

export const SwitchTheme = () => {
  const { theme, changeTheme } = useAppStore(
    useShallow((state) => ({ theme: state.theme, changeTheme: state.changeTheme }))
  );

  return (
    <Button
      shape="circle"
      onClick={() => {
        if (theme === 'dark') {
          changeTheme('light');
        } else {
          changeTheme('dark');
        }
      }}
      icon={theme === 'dark' ? <IconSunFill fontSize={20} /> : <IconMoonFill fontSize={20} />}
    ></Button>
  );
};
