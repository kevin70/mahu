import { useDebounce } from 'ahooks';
import { PaginationProps, TableProps } from 'antd';
import { SortOrder } from 'antd/es/table/interface';
import { usePagination } from './usePagination';
import { useTableSorter } from './useTableSorter';

export const useTableHelper = ({
  pagination,
  sort,
}: {
  pagination?: PaginationProps;
  sort?: { columnKey: string; order?: SortOrder }[];
}) => {
  // 分页
  const { pagination: innerPagination, setPagination, gotoFirstPage, queryOffsetLimit } = usePagination(pagination);
  const debouncedInnerPagination = useDebounce(innerPagination, { wait: 15 });

  // 排序
  const { setTableSorter, querySort } = useTableSorter(sort);
  const onTableChange: TableProps<any>['onChange'] = (pagination, filters, sorter, extra) => {
    if (extra.action === 'paginate') {
      setPagination((prev) => ({
        ...prev,
        ...pagination,
      }));
    }

    if (extra.action === 'sort') {
      setTableSorter(sorter);
    }
  };

  return { onTableChange, pagination: debouncedInnerPagination, gotoFirstPage, queryOffsetLimit, querySort };
};
