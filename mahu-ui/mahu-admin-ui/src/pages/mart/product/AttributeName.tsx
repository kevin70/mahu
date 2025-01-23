import { useMartAttributeData } from '@/hooks';

export const AttributeName = ({ attributeId }: { attributeId: number }) => {
  const { data } = useMartAttributeData(attributeId);
  return <>{data?.name}</>;
};
