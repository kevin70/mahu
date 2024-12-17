import { permits } from '@/config/permit';
import { MARKET_API } from '@/services';
import { useAppStore } from '@/stores';
import { DeleteOutlined, LoadingOutlined } from '@ant-design/icons';
import { CheckCard, PageContainer } from '@ant-design/pro-components';
import { useInfiniteQuery } from '@tanstack/react-query';
import { Button, Flex, Image, Modal, Space, Typography } from 'antd';
import { useShallow } from 'zustand/shallow';
import { useInView } from 'react-intersection-observer';
import { useEffect, useState } from 'react';
import { useMap } from 'ahooks';
import { css } from '@styled-system/css';

export const MarketAssetList = () => {
  const shopId = useAppStore(useShallow((state) => state.selectedShopId));
  const { ref, inView } = useInView();
  const noWrite = $checkNotPermit(permits.MARKET_ASSET.W);

  const { hasNextPage, fetchNextPage, data } = useInfiniteQuery({
    queryKey: ['MarketAssetList', shopId],
    queryFn: async ({ pageParam: offset }) => {
      const limit = 50;
      const resp = await MARKET_API.listShopAssets(shopId, limit, offset, undefined, undefined, undefined, 1);
      const items = resp.items || [];
      return {
        items,
        offset: items.length >= limit ? offset + limit : null,
      };
    },
    initialPageParam: 0,
    getNextPageParam: (page) => page.offset,
  });

  useEffect(() => {
    if (inView) {
      fetchNextPage();
    }
  }, [fetchNextPage, inView]);

  // 选中的资源图片
  const [selectAssetMap, { set: setSelectAsset, remove: removeSelectAsset, reset: resetSelectAsset }] = useMap<
    number,
    string
  >([]);

  const DeleteModal = () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <Button
          disabled={noWrite || selectAssetMap.size === 0}
          icon={<DeleteOutlined />}
          type="primary"
          danger
          onClick={() => setOpen(true)}
        >
          删除
        </Button>

        <Modal
          title="确认删除选中的资源"
          open={open}
          onCancel={() => setOpen(false)}
          okButtonProps={{
            danger: true,
          }}
          cancelButtonProps={{
            hidden: true,
          }}
          okText="确认删除"
        >
          <Flex justify="center" wrap gap={'small'} className={css({ maxH: 480, overflow: 'auto' })}>
            {Array.from(selectAssetMap.entries()).map((entry) => (
              <Image src={entry[1]} width={120} />
            ))}
          </Flex>
        </Modal>
      </>
    );
  };

  return (
    <PageContainer
      fixedHeader
      extra={
        <Space>
          <DeleteModal />
        </Space>
      }
    >
      <Flex wrap>
        {data?.pages.map((page) =>
          page.items.map((o) => (
            <CheckCard
              key={o.id}
              cover={<img src={o.uri} />}
              onChange={(checked) => {
                if (checked) {
                  setSelectAsset(o.id, o.uri);
                } else {
                  removeSelectAsset(o.id);
                }
              }}
            ></CheckCard>
          ))
        )}
      </Flex>

      <Flex justify="center">
        <div ref={ref}>
          {hasNextPage ? (
            <Space>
              <LoadingOutlined />
              加载更多数据中 ...
            </Space>
          ) : (
            <Typography.Text type="secondary">没有更多数据</Typography.Text>
          )}
        </div>
      </Flex>
    </PageContainer>
  );
};
