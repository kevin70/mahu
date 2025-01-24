import { CloseOutlined } from '@ant-design/icons';
import { Button, ButtonProps } from 'antd';

export const HCloseButton = (props: ButtonProps) => {
  return (
    <Button
      size="small"
      variant="text"
      color="danger"
      icon={<CloseOutlined />}
      onClick={(e) => {
        e.preventDefault();
        props.onClick?.(e);
      }}
    ></Button>
  );
};
