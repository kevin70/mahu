import { useMemo, useState } from 'react';
import { useDebounce } from '@uidotdev/usehooks';
import { PaginationProps, TableProps } from '@arco-design/web-react';
import { SorterInfo } from '@arco-design/web-react/es/Table/interface';

export const useTableHelper = ({
  pagination,
  sort,
}: {
  pagination?: PaginationProps;
  sort?: SorterInfo | SorterInfo[];
}) => {
  // 分页
  const [innerPagination, setInnerPagination] = useState({
    current: 1,
    pageSize: 20,
    sizeCanChange: true,
    sizeOptions: [20, 50, 100, 500],
    bufferSize: 3,
    showTotal: true,
    ...pagination,
  });
  const debouncedInnerPagination = useDebounce(innerPagination, 10);

  // 重置分页
  const gotoFirstPage = () => {
    setInnerPagination((prev) => ({ ...prev, current: 1 }));
  };

  const queryOffsetLimit = useMemo(() => {
    const current = debouncedInnerPagination.current || 1;
    const pageSize = debouncedInnerPagination.pageSize || 20;
    return {
      offset: (current - 1) * pageSize,
      limit: pageSize,
    };
  }, [debouncedInnerPagination]);

  // 排序
  const [innerSort, setInnerSort] = useState(sort);
  const querySort: string[] = useMemo(() => {
    const arr = [];
    if (Array.isArray(innerSort)) {
      innerSort.forEach((o) => {
        if (o.field) {
          arr.push(o.direction === 'ascend' ? `${o.field}` : `-${o.field}`);
        }
      });
    } else {
      if (innerSort && innerSort.field) {
        arr.push(innerSort.direction === 'ascend' ? `${innerSort.field}` : `-${innerSort.field}`);
      }
    }
    return arr;
  }, [innerSort]);

  const onTableChange: TableProps['onChange'] = (pagination, sorter, _filters, extra) => {
    if (extra.action === 'paginate') {
      setInnerPagination((prev) => ({
        ...prev,
        ...pagination,
      }));
    }
    if (extra.action === 'sort') {
      setInnerSort(sorter);
    }
  };

  return { onTableChange, pagination: debouncedInnerPagination, gotoFirstPage, queryOffsetLimit, querySort };
};
