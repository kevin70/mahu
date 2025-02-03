import { EditOutlined } from '@ant-design/icons';
import { css } from '@emotion/react';
import { Button, InputNumber, Popconfirm } from 'antd';
import { useState } from 'react';

interface BatchSetNumberProps {
  label: string;
  onChange: (value: number | null) => void;
}

export const BatchSetNumber = (props: BatchSetNumberProps) => {
  const [value, setValue] = useState<number | null>(null);
  return (
    <Popconfirm
      title={props.label}
      icon={<EditOutlined />}
      description={
        <InputNumber
          min={0}
          css={css`
            margin: var(--ant-margin-xs) 0;
            min-width: 240px;
          `}
          value={value}
          onChange={setValue}
          placeholder={`请输入`}
        />
      }
      onOpenChange={() => {
        setValue(null);
      }}
      onConfirm={() => {
        props.onChange(value);
      }}
    >
      <Button type="text" size="small">
        {props.label}
      </Button>
    </Popconfirm>
  );
};
