import { MART_API } from '@/services';
import { useQuery } from '@tanstack/react-query';
import { TreeSelectProps } from 'antd';

export const HMartCategoryTreeSelect = ({
  nonSelectIds,
  ...otherProps
}: TreeSelectProps & {
  nonSelectIds?: number[];
}) => {
  const { data } = useQuery({
    queryKey: ['HMartCategoryTreeSelect'],
    async queryFn() {
      const res = await MART_API.listMartCategories({ limit: 5000 });
      return res.items;
    },
  });

  // ======
};
