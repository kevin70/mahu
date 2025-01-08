import { Button, ButtonProps } from '@arco-design/web-react';
import { IconRedo } from '@arco-design/web-react/icon';
import { useTranslation } from 'react-i18next';

export const HResetButton = (props: ButtonProps) => {
  const { t } = useTranslation();
  return (
    <Button htmlType="reset" icon={<IconRedo />} {...props}>
      {t('button.reset')}
    </Button>
  );
};
