import { EditOutlined } from '@ant-design/icons';
import { Button, ButtonProps } from 'antd';

export const HEditButton = (props: ButtonProps) => {
  return <Button color="default" variant="link" icon={<EditOutlined />} {...props} />;
};
