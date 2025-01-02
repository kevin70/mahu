import { useMemo, useState } from 'react';
import dayjs from 'dayjs';
import rsqlBuilder from '@rsql/builder';
import { ComparisonNode, ExpressionNode } from '@rsql/ast';
import { emit } from '@rsql/emitter';

type Filter = {
  value: unknown;
  qname: string;
  op: 'eq' | 'ne' | 'gt' | 'lt' | 'gte' | 'lte' | 'contains' | 'icontains' | 'in' | 'nin' | 'between';
};

/**
 * RSQL 数据过滤器
 *
 * https://github.com/piotr-oles/rsql
 *
 * @returns
 */
export const useRSQLFilter = () => {
  const [rsqlFilters, setRSQLFilters0] = useState<ExpressionNode[]>([]);

  const queryFilter = useMemo(() => {
    if (rsqlFilters.length <= 0) {
      return '';
    }

    const node = rsqlBuilder.logic(rsqlFilters, 'and');
    return emit(node, { optimizeQuotes: true });
  }, [rsqlFilters]);

  const rsqlOps = {
    ...rsqlBuilder,
    contains(selector: string, value: string) {
      return rsqlBuilder.comparison(selector, '=contains=', value);
    },
    icontains(selector: string, value: string) {
      return rsqlBuilder.comparison(selector, '=icontains=', value);
    },
    between(selector: string, values: (string | number)[]) {
      if (values.length != 2) {
        throw new Error('between 操作符数据必须有两个值');
      }
      return rsqlBuilder.comparison(selector, '=between=', values);
    },
    comparisonEx(
      selector: string,
      operator:
        | '=='
        | '!='
        | '<='
        | '>='
        | '<'
        | '>'
        | '=in='
        | '=out='
        | '=le='
        | '=ge='
        | '=lt='
        | '=gt='
        | '=contains='
        | '=icontains=',
      value: string | number | (string | number)[] | undefined | null
    ): ComparisonNode | null {
      if (typeof value === 'undefined' || value === null || value === '') {
        return null;
      }
      if (Array.isArray(value) && value.length <= 0) {
        return null;
      }
      return rsqlBuilder.comparison(selector, operator, value);
    },
  };

  const setRSQLFilters = (nodes: Array<ExpressionNode | null>) => {
    const arr: ExpressionNode[] = [];
    for (const n of nodes) {
      if (n !== null) {
        arr.push(n);
      }
    }
    setRSQLFilters0(arr);
  };

  return {
    queryFilter,
    rsqlOps,
    setRSQLFilters,
  };
};

/**
 * 数据过滤.
 */
export const useDataFilter = () => {
  const [dataFilters, setDataFilters] = useState<Filter[]>([]);

  const queryFilter = useMemo(() => {
    const arr = [];
    for (const element of dataFilters) {
      if (element.value) {
        let v;
        if (element.value instanceof String) {
          v = element.value.trim();
        } else if (element.value instanceof Date) {
          v = dayjs(element.value).format('YYYY-MM-DDTHH:mm:ss');
        } else {
          v = new String(element.value).trim();
        }

        if (v && v !== '') {
          arr.push(`${element.qname} ${element.op} ${v}`);
        }
      }
    }

    return arr;
  }, [dataFilters]);

  /// 更新删除数据过滤器
  const upsertDataFilters = (filters: Filter[]) => {
    const arr = [...filters];
    for (const f of dataFilters) {
      const idx = arr.findIndex((o) => o.qname === f.qname);
      if (idx === -1) {
        arr.push(f);
      }
    }
    return setDataFilters(arr);
  };

  /// 删除数据过滤
  const removeDataFilters = (qnames: string[]) => {
    const filters = dataFilters.filter((o) => qnames.includes(o.qname));
    setDataFilters(filters);
  };

  return { setDataFilters, upsertDataFilters, removeDataFilters, queryFilter };
};
