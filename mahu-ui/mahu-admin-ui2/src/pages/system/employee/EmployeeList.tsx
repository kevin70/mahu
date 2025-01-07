import { HNewButton } from '@/components/HNewButton';
import { HSearchForm } from '@/components/HSearchForm';
import { HTableDelButton } from '@/components/HTableDelButton';
import { PageContainer } from '@/components/PageContainer';
import { Button, Form, Input, Table, TableColumnProps } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { css } from '@styled-system/css';

export const EmployeeList = () => {
  const columns: TableColumnProps[] = [
    {
      title: 'Name',
      dataIndex: 'name',
    },
    {
      title: 'Salary',
      dataIndex: 'salary',
    },
    {
      title: 'Address',
      dataIndex: 'address',
    },
    {
      title: 'Email',
      dataIndex: 'email',
    },
    {
      title: '操作',
      align: 'right',
      render(col, item, index) {
        return <HTableDelButton />;
      },
    },
  ];
  const data = [
    {
      key: '1',
      name: 'Jane Doe',
      salary: 23000,
      address: '32 Park Road, London',
      email: 'jane.doe@example.com',
    },
    {
      key: '2',
      name: 'Alisa Ross',
      salary: 25000,
      address: '35 Park Road, London',
      email: 'alisa.ross@example.com',
    },
    {
      key: '3',
      name: 'Kevin Sandra',
      salary: 22000,
      address: '31 Park Road, London',
      email: 'kevin.sandra@example.com',
    },
    {
      key: '4',
      name: 'Ed Hellen',
      salary: 17000,
      address: '42 Park Road, London',
      email: 'ed.hellen@example.com',
    },
    {
      key: '5',
      name: 'William Smith',
      salary: 27000,
      address: '62 Park Road, London',
      email: 'william.smith@example.com',
    },
  ];

  return (
    <PageContainer
      title={'职员列表'}
      extra={
        <div>
          <HNewButton />
        </div>
      }
    >
      <HSearchForm
        onSearch={() => {
          //
        }}
      >
        <Form.Item label="名称">
          <Input />
        </Form.Item>
        <Form.Item label="名称">
          <Input />
        </Form.Item>
      </HSearchForm>

      <Table columns={columns} data={data} />
    </PageContainer>
  );
};
