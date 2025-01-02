import { useMemo, useState } from 'react';
import rsqlBuilder from '@rsql/builder';
import { ComparisonNode, ExpressionNode } from '@rsql/ast';
import { emit } from '@rsql/emitter';

/**
 * RSQL 操作
 */
export const rsqlOps = {
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
  // RSQL 查询编码
  encode(node: ExpressionNode) {
    return emit(node, { optimizeQuotes: true });
  },
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
