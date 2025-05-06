import { AdminStatusEnum } from '@/services/generated';
import { Select, SelectProps } from 'antd';

export const AdminStatusSelect = (props: SelectProps) => {
  const options = [
    {
      value: AdminStatusEnum.Active,
      label: '正常',
    },
    {
      value: AdminStatusEnum.Blocked,
      label: '冻结',
    },
  ];
  return <Select options={options} {...props} />;
};
