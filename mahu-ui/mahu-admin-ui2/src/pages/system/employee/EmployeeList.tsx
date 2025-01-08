import { HDrawerForm } from '@/components/HDrawerForm';
import { HNewButton } from '@/components/HNewButton';
import { HSearchButton } from '@/components/HSearchButton';
import { HTableDelButton } from '@/components/HTableDelButton';
import { PageContainer } from '@/components/PageContainer';
import { useTableHelper } from '@/hooks';
import { Form, Input, Table, TableColumnProps } from '@arco-design/web-react';
import { useEffect } from 'react';

export const EmployeeList = () => {
  const { onTableChange, pagination, queryOffsetLimit, querySort } = useTableHelper({});
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

  useEffect(() => {
    console.log('page', queryOffsetLimit);
  }, [queryOffsetLimit]);

  const NewForm = () => {
    return (
      <HDrawerForm
        trigger={<HNewButton />}
        title={'新建职员'}
        onInit={async (form) => {
          form.setFieldValue('name', 'hello');
        }}
        onFinish={async (values, form) => {
          //
          return true;
        }}
      >
        <Form.Item label="名称" field={'name'} rules={[{ required: true }]}>
          <Input />
        </Form.Item>
      </HDrawerForm>
    );
  };

  return (
    <PageContainer title={'职员列表'} extra={<NewForm />}>
      <Form
        layout="inline"
        colon
        onSubmit={() => {
          //
        }}
      >
        <Form.Item label="名称">
          <Input />
        </Form.Item>
        <HSearchButton />
      </Form>

      <Table
        columns={columns}
        data={data}
        onChange={onTableChange}
        pagination={{
          ...pagination,
          total: 1000,
        }}
      />
    </PageContainer>
  );
};
