import { useMartAttributeData } from '@/hooks';
import { Form, FormItemProps, Input, Select, Space } from 'antd';

interface AttributeFormItem {
  attributeId: number;
}

export const AttributeFormItem = ({ attributeId, name, label, ...otherProps }: AttributeFormItem & FormItemProps) => {
  const { data } = useMartAttributeData(attributeId);
  const required = data?.required ?? false;

  if (data?.valueType === 'SELECT') {
    const options = data?.attributeValues?.map((o) => ({ label: o.value, value: o.id }));
    return (
      <Form.Item
        label={
          <Space>
            {data?.name}
            {label}
          </Space>
        }
        name={[...name, 'attributeValueId']}
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
};
