import { useAppStore } from '@/stores';
import { Button, Select } from '@arco-design/web-react';
import { useShallow } from 'zustand/shallow';
import { IconLanguage } from '@arco-design/web-react/icon';

export const SwitchLang = () => {
  const { lng, changeLng } = useAppStore(useShallow((state) => ({ lng: state.lng, changeLng: state.changeLng })));

  return (
    <Select
      trigger={'click'}
      triggerElement={<Button shape="circle" icon={<IconLanguage fontSize={20} />} />}
      triggerProps={{
        autoAlignPopupWidth: false,
        autoAlignPopupMinWidth: true,
        position: 'br',
      }}
      options={[
        { value: 'zh-CN', label: '中文简体' },
        { value: 'en-US', label: 'English' },
      ]}
      value={lng}
      onChange={changeLng}
      defaultActiveFirstOption={false}
    />
  );
};
