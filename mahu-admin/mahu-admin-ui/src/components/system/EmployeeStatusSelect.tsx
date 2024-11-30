import { EmployeeStatusEnum } from '@/services/generated';
import { Select, SelectProps } from 'antd';

export const EmployeeStatusSelect = (props: SelectProps) => {
  const options = [
    {
      value: EmployeeStatusEnum.Active,
      label: '正常',
    },
    {
      value: EmployeeStatusEnum.Blocked,
      label: '冻结',
    },
    {
      value: EmployeeStatusEnum.Resign,
      label: '离职',
    },
  ];
  return <Select options={options} {...props} />;
};
