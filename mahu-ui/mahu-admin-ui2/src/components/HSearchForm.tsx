import { Button, Form, FormInstance, Space } from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import { flex, hstack } from '@styled-system/patterns';
import { PropsWithChildren } from 'react';

export const HSearchForm = ({
  isPending,
  onSearch,
  children,
  ref,
}: {
  isPending?: boolean;
  onSearch(values: any): void;
} & PropsWithChildren & {
    ref?: React.Ref<FormInstance>;
  }) => {
  return (
    <Form ref={ref} onSubmit={onSearch} colon>
      <div
        className={flex({
          gap: 4,
          '& > *': {
            maxW: 280,
          },
        })}
      >
        {children}
        <Button htmlType="submit" type="primary" icon={<IconSearch />} loading={isPending}>
          搜索
        </Button>
      </div>
    </Form>
  );
};
