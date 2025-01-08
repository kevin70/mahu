import { Button, ButtonProps } from '@arco-design/web-react';
import { IconSave } from '@arco-design/web-react/icon';
import { useTranslation } from 'react-i18next';

export const HSaveButton = (props: ButtonProps) => {
  const { t } = useTranslation();
  return (
    <Button htmlType="submit" type="primary" icon={<IconSave />} {...props}>
      {t('button.save')}
    </Button>
  );
};
