import { DeleteOutlined } from '@ant-design/icons';
import { Button, Popconfirm } from 'antd';
import { ButtonProps } from 'antd/lib';
import { RenderFunction } from 'antd/lib/_util/getRenderPropValue';

export const HDeletePopconfirmButton = ({
  description,
  onConfirm,
  ...otherProps
}: { description: RenderFunction; onConfirm: () => void } & ButtonProps) => {
  return (
    <Popconfirm
      placement="topLeft"
      title="确认"
      okButtonProps={{
        danger: true,
      }}
      description={description}
      onConfirm={onConfirm}
    >
      <Button color="default" variant="link" icon={<DeleteOutlined />} {...otherProps} />
    </Popconfirm>
  );
};
