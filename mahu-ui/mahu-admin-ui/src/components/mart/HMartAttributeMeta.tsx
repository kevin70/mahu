import { MART_API } from '@/services';
import { GetMartAttributeResponse } from '@/services/generated';
import { useQuery } from '@tanstack/react-query';
import { ReactNode } from 'react';

interface HMartAttributeMetaProps {
  attributeId: number;
  children: (attr: GetMartAttributeResponse) => ReactNode;
}

export const HMartAttributeMeta = ({ attributeId, children }: HMartAttributeMetaProps) => {
  const { data } = useQuery({
    queryKey: ['HMartAttributeMeta', attributeId],
    async queryFn() {
      return MART_API.getMartAttribute({
        id: attributeId,
      });
    },
  });

  if (data) {
    return children(data);
  }
  return <>属性加载错误</>;
};
