import { PageHeader, PageHeaderProps } from '@arco-design/web-react';
import { PropsWithChildren } from 'react';

export const PageContainer = ({ children, ...headerProps }: PageHeaderProps & PropsWithChildren) => {
  return <PageHeader {...headerProps}>{children}</PageHeader>;
};
