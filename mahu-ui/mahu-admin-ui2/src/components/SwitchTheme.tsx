import IconParkOutlineMoon from '@/icons/IconParkOutlineMoon';
import IconParkOutlineSun from '@/icons/IconParkOutlineSun';
import { useAppStore } from '@/stores';
import { Button } from '@arco-design/web-react';
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
    >
      {theme === 'dark' ? <IconParkOutlineSun width={'2rem'} /> : <IconParkOutlineMoon width={'2rem'} />}
    </Button>
  );
};
