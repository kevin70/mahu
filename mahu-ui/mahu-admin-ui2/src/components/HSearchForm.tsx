import { Button, Form } from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import { hstack } from '@styled-system/patterns';
import { PropsWithChildren } from 'react';

export const HSearchForm = ({
  isPending,
  onSearch,
  children,
}: {
  isPending: boolean;
  onSearch(values: any): void;
} & PropsWithChildren) => {
  return (
    <Form onSubmit={onSearch} className={hstack({ flexWrap: 'wrap' })}>
      {children}
      <Button htmlType="submit" type="primary" icon={<IconSearch />} loading={isPending}>
        搜索
      </Button>
    </Form>
  );
};
