import { PlusOutlined } from '@ant-design/icons';
import { Button, ButtonProps } from 'antd';

export const HNewButton = (props: ButtonProps) => {
  return (
    <Button icon={<PlusOutlined />} {...props}>
      新建
    </Button>
  );
};
