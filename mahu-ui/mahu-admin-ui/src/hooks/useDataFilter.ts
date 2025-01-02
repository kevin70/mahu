import { useMemo, useState } from 'react';
import dayjs from 'dayjs';
import rsqlBuilder from '@rsql/builder';
import { ExpressionNode, getSelector, isComparisonNode, isLogicNode } from '@rsql/ast';
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
  const [rsqlFilters, setRSQLFilters] = useState<ExpressionNode[]>([]);

  const queryFilter = useMemo(() => {
    const node = rsqlBuilder.logic(rsqlFilters, 'and');
    return emit(node, { optimizeQuotes: true });
  }, [rsqlFilters]);

  const rsqlOps = {
    ...rsqlBuilder,
    like(selector: string, value: string) {
      return rsqlBuilder.comparison(selector, '=like=', value);
    },
    ilike(selector: string, value: string) {
      return rsqlBuilder.comparison(selector, '=ilike=', value);
    },
    between(selector: string, values: (string | number)[]) {
      if (values.length != 2) {
        throw new Error('between 操作符数据必须有两个值');
      }
      return rsqlBuilder.comparison(selector, '=between=', values);
    },
  };

  /// 更新删除数据过滤器
  const upsertRSQLFilters = (filters: ExpressionNode[]) => {
    const arr = [...filters];
    for (const f of rsqlFilters) {
      if (isLogicNode(f)) {
        continue;
      }

      const idx = arr.findIndex((o) => {
        if (isComparisonNode(o)) {
          const selector = getSelector(o);
          return selector === getSelector(f);
        }
        return false;
      });

      if (idx === -1) {
        arr.push(f);
      }
    }
    return setRSQLFilters(filters);
  };

  /// 删除数据过滤
  const removeRSQLFilters = (selectors: string[]) => {
    const filters = rsqlFilters.filter((o) => {
      if (isLogicNode(o)) {
        return false;
      }
      const selector = getSelector(o);
      return selectors.includes(selector);
    });
    setRSQLFilters(filters);
  };

  return {
    queryFilter,
    rsqlOps,
    setRSQLFilters,
    upsertRSQLFilters,
    removeRSQLFilters,
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
