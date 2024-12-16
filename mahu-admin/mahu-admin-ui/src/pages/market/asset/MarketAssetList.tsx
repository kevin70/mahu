import { permits } from '@/config/permit';
import { usePagination } from '@/hooks';
import { MARKET_API } from '@/services';
import { PageContainer } from '@ant-design/pro-components';
import { useInfiniteQuery } from '@tanstack/react-query';

export const MarketAssetList = () => {
  const noWrite = $checkNotPermit(permits.MARKET_ASSET.W);
  const { pagination, setPagination, setTotal, queryOffsetLimit } = usePagination();

  const { isLoading, fetchNextPage, data } = useInfiniteQuery({
    queryKey: ['MarketAssetList'],
    queryFn: async ({ pageParam }) => {
      // MARKET_API.listShopAssets();
      // const response = await fetch(`/api/projects?cursor=${pageParam}`);
      // return await response.json();
    },
    initialPageParam: queryOffsetLimit,
    getPreviousPageParam: (firstPage) => firstPage.previousId,
    getNextPageParam: (lastPage) => lastPage.nextId,
  });

  const onDelete = async (id: number) => {
    await MARKET_API.deleteShop(id);
    $message().success('删除商店成功');

    // await refetch();
  };

  return <PageContainer>//</PageContainer>;
};
