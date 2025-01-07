import { Button, Popconfirm, PopconfirmProps } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';

export const HTableDelButton = ({ title, onOk }: Pick<PopconfirmProps, 'title' | 'onOk'>) => {
  return (
    <Popconfirm focusLock title={title || '确认删除指定数据？'} onOk={onOk}>
      <Button status="danger" type="text" icon={<IconDelete fontSize={16} />} />
    </Popconfirm>
  );
};
