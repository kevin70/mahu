import { MART_API } from '@/services';
import { useQuery } from '@tanstack/react-query';

/**
 * 获取属性的数据
 * @param attributeId 属性 ID
 * @returns 属性
 */
export const useMartAttributeData = (attributeId: number) => {
  return useQuery({
    queryKey: ['useMartAttributeData', attributeId],
    async queryFn() {
      return MART_API.getMartAttribute({
        id: attributeId,
      });
    },
  });
};
