import { DownOutlined } from '@ant-design/icons';
import { Button, Dropdown } from 'antd';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';

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

export const ChooseLocale = () => {
  const { i18n } = useTranslation();

  const [lng, setLng] = useState(i18n.language);
  const locale = useMemo(() => {
    return items.find((o) => o?.key === lng)?.label || '选择语言';
  }, [lng]);

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
      <Button type="text" icon={<DownOutlined />} iconPosition="end">
        {locale}
      </Button>
    </Dropdown>
  );
};
