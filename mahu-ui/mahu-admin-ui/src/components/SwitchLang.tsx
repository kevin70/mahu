import { Button, Dropdown } from 'antd';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { IoLanguage } from 'react-icons/io5';

const items = [
  {
    label: '中文简体',
    key: 'zhCN',
  },
  {
    label: 'English',
    key: 'enUS',
  },
];

export const SwitchLang = () => {
  const { i18n } = useTranslation();
  const [lng, setLng] = useState(i18n.language);

  i18n.on('languageChanged', (o) => {
    setLng(o);
  });

  return (
    <Dropdown
      menu={{
        items,
        multiple: false,
        selectable: true,
        selectedKeys: [lng],
        onSelect(o) {
          i18n.changeLanguage(o.key);
        },
      }}
      trigger={['click']}
    >
      <Button type="text" shape="circle" icon={<IoLanguage fontSize={16} />} />
    </Dropdown>
  );
};
