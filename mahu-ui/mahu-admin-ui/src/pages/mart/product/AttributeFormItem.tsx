import { HMartAttributeMeta } from '@/components/mart/HMartAttributeMeta';
import { MartAttributeResponse } from '@/services/generated';
import { Form, FormItemProps, Input, Select, Space } from 'antd';

interface AttributeFormItem {
  attributeId: number;
  onLoaded?: (data: MartAttributeResponse) => void;
}

export const AttributeFormItem = ({
  attributeId,
  onLoaded,
  name,
  label,
  ...otherProps
}: AttributeFormItem & FormItemProps) => {
  return (
    <HMartAttributeMeta attributeId={attributeId}>
      {(data) => {
        const required = data?.required ?? false;
        if (data && onLoaded) {
          onLoaded(data);
        }

        if (data?.valueType === 'SELECT') {
          const options = data?.attributeValues?.map((o) => ({ label: o.value, value: o.value }));
          return (
            <Form.Item
              label={
                <Space>
                  {data?.name}
                  {label}
                </Space>
              }
              name={[...name, 'value']}
              rules={[{ required, message: '请选择属性值' }]}
              {...otherProps}
            >
              <Select options={options} allowClear placeholder={'请选择属性值'}></Select>
            </Form.Item>
          );
        }

        return (
          <Form.Item
            label={
              <Space>
                {data?.name}
                {label}
              </Space>
            }
            name={[...name, 'value']}
            rules={[{ required, message: '请输入属性值' }]}
            {...otherProps}
          >
            <Input allowClear placeholder="请输入属性值" />
          </Form.Item>
        );
      }}
    </HMartAttributeMeta>
  );
};
