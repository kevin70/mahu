import { MART_API } from '@/services';
import { useQuery } from '@tanstack/react-query';
import { Form, Input, Select } from 'antd';

interface AttributeFormItem {
  name: any[];
  attributeId: number;
}

export const AttributeFormItem = (props: AttributeFormItem) => {
  const { data } = useQuery({
    queryKey: ['HAttributeComplexInput', props.attributeId],
    async queryFn() {
      return MART_API.getMartAttribute({
        id: props.attributeId,
      });
    },
  });

  if (data?.valueType === 'SELECT') {
    const options = data?.attributeValues?.map((o) => ({ label: o.value, value: o.id }));
    return (
      <Form.Item label={data?.name} name={[...props.name, 'attributeValueId']}>
        <Select options={options}></Select>
      </Form.Item>
    );
  }
  return (
    <Form.Item label={data?.name} name={[...props.name, 'value']}>
      <Input />
    </Form.Item>
  );
};
