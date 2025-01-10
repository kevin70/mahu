import { PaginationProps } from 'antd';
import { useMemo, useState } from 'react';

/**
 * ant-design 表格分页的勾子.
 */
export const usePagination = (dv?: PaginationProps) => {
  const [pagination, setPagination] = useState<PaginationProps>({
    pageSize: 15,
    showSizeChanger: true,
    pageSizeOptions: [15, 50, 100, 500],
    ...dv,
  });

  const queryOffsetLimit = useMemo(() => {
    const current = pagination.current || 1;
    const pageSize = pagination.pageSize || 20;
    return {
      offset: (current - 1) * pageSize,
      limit: pageSize,
    };
  }, [pagination]);

  const gotoFirstPage = () => {
    setPagination((old) => ({ ...old, current: 1 }));
  };

  return { pagination, setPagination, gotoFirstPage, queryOffsetLimit };
};
