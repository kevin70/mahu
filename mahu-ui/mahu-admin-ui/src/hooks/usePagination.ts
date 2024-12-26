import { PaginationProps } from 'antd';
import { useMemo, useState } from 'react';

/**
 * ant-design 表格分页的勾子.
 */
export const usePagination = (dv?: PaginationProps) => {
  const [pagination, setPagination] = useState<PaginationProps>({
    pageSize: 10,
    showSizeChanger: true,
    ...dv,
  });

  const setTotal = (total?: number | null) => {
    setPagination((prev) => ({
      ...prev,
      total: total || 0,
    }));
  };

  const queryOffsetLimit = useMemo(() => {
    const current = pagination.current || 1;
    const pageSize = pagination.pageSize || 20;
    console.log('current page', current);
    return {
      offset: (current - 1) * pageSize,
      limit: pageSize,
    };
  }, [pagination]);

  const resetPagination = () => {
    setPagination((old) => ({ ...old, current: 1 }));
  };

  return { pagination, setPagination, resetPagination, setTotal, queryOffsetLimit };
};
