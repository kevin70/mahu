import { useMemo, useState } from 'react';
import { isDate, isString, toString, trim } from 'lodash-es';
import dayjs from 'dayjs';

type Filter = {
  value: unknown;
  qname: string;
  op: 'eq' | 'ne' | 'gt' | 'lt' | 'gte' | 'lte' | 'contains' | 'icontains' | 'in' | 'nin' | 'between';
};

/**
 * 数据过滤.
 */
export const useDataFilter = () => {
  const [dataFilters, setDataFilters] = useState<Filter[]>([]);

  const queryFilter = useMemo(() => {
    const arr = [];
    for (const element of dataFilters) {
      let v;
      if (isString(element.value)) {
        v = trim(element.value, ' \t');
      } else if (isDate(element.value)) {
        v = dayjs(element.value).format('YYYY-MM-DDTHH:mm:ss');
      } else {
        v = toString(element.value);
      }

      if (v && v !== '') {
        arr.push(`${element.qname} ${element.op} ${v}`);
      }
    }

    return arr;
  }, [dataFilters]);

  return { setDataFilters, queryFilter };
};
