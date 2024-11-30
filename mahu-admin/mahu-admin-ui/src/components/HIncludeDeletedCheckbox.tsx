import { Checkbox, Typography } from 'antd';
import { CheckboxProps } from 'antd/lib';
import { useState } from 'react';

export const HIncludeDetedCheckBox = (props: CheckboxProps) => {
  const [checked, setChecked] = useState(false);
  return (
    <Checkbox
      title="包含已删除的数据"
      {...props}
      onChange={(e) => {
        setChecked(e.target.checked);
        props.onChange?.(e);
      }}
    >
      <Typography.Text type={checked ? 'danger' : 'secondary'}>已删除</Typography.Text>
    </Checkbox>
  );
};
