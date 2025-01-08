import { Button, ButtonProps } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { useTranslation } from 'react-i18next';

export const HNewButton = (props: ButtonProps) => {
  const { t } = useTranslation();
  return (
    <Button icon={<IconPlus />} {...props}>
      {t('button.new')}
    </Button>
  );
};
