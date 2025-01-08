import { Button, ButtonProps } from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import { useTranslation } from 'react-i18next';

export const HSearchButton = (props: ButtonProps) => {
  const { t } = useTranslation();
  return (
    <Button htmlType="submit" type="primary" icon={<IconSearch />} {...props}>
      {t('button.search')}
    </Button>
  );
};
