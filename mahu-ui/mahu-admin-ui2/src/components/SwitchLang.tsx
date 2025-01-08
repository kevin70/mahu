import { useAppStore } from '@/stores';
import { Button, Dropdown, Menu } from '@arco-design/web-react';
import { useShallow } from 'zustand/shallow';
import { IconArrowRight, IconCheck, IconLanguage } from '@arco-design/web-react/icon';
import { css } from '@emotion/react';

export const SwitchLang = () => {
  const { lng, changeLng } = useAppStore(useShallow((state) => ({ lng: state.lng, changeLng: state.changeLng })));

  const items = [
    { key: 'zh-CN', label: '中文简体' },
    { key: 'en-US', label: 'English' },
  ].map((o) => (
    <Menu.Item
      key={o.key}
      onClick={() => {
        // @ts-ignore
        changeLng(o.key);
      }}
      css={css`
        display: flex;
        align-items: center;
        gap: 8px;
      `}
    >
      {o.key === lng ? (
        <IconCheck />
      ) : (
        <IconArrowRight
          css={css`
            opacity: 0;
          `}
        />
      )}
      {o.label}
    </Menu.Item>
  ));

  return (
    <Dropdown position={'br'} trigger={'click'} droplist={<Menu>{items}</Menu>}>
      <Button shape="circle" icon={<IconLanguage fontSize={20} />} />
    </Dropdown>
  );
};
