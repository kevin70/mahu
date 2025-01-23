import { useMartAttributeData } from '@/hooks';
import { Form, FormItemProps, Input, Select } from 'antd';

interface AttributeFormItem {
  attributeId: number;
}

export const AttributeFormItem = ({ attributeId, name, ...otherProps }: AttributeFormItem & FormItemProps) => {
  const { data } = useMartAttributeData(attributeId);
  const required = data?.required ?? false;

  if (data?.valueType === 'SELECT') {
    const options = data?.attributeValues?.map((o) => ({ label: o.value, value: o.id }));
    return (
      <Form.Item label={data?.name} name={[...name, 'attributeValueId']} rules={[{ required }]} {...otherProps}>
        <Select options={options} allowClear placeholder={'请选择属性值'}></Select>
      </Form.Item>
    );
  }
  return (
    <Form.Item label={data?.name} name={[...name, 'value']} rules={[{ required }]} {...otherProps}>
      <Input allowClear placeholder="请输入属性值" />
    </Form.Item>
  );
};
