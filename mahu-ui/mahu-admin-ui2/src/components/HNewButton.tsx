import { Button, ButtonProps } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';

export const HNewButton = (props: ButtonProps) => {
  return (
    <Button icon={<IconPlus />} {...props}>
      新建
    </Button>
  );
};
