import { SearchOutlined } from '@ant-design/icons';
import { Button, ButtonProps } from 'antd';

export const HSearchButton = (props: ButtonProps) => {
  return (
    <Button icon={<SearchOutlined />} {...props}>
      查询
    </Button>
  );
};
