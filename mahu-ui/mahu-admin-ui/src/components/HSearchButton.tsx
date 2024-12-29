import { SearchOutlined } from '@ant-design/icons';
import { Button, ButtonProps } from 'antd';
import { useTranslation } from 'react-i18next';

export const HSearchButton = (props: ButtonProps) => {
  const { t } = useTranslation();
  return (
    <Button icon={<SearchOutlined />} {...props}>
      {t('search')}
    </Button>
  );
};
