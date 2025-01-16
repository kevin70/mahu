import { MART_API } from '@/services';
import { useProfileStore } from '@/stores';
import { LoadingOutlined } from '@ant-design/icons';
import { CheckCard } from '@ant-design/pro-components';
import { css } from '@emotion/react';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useMap } from 'ahooks';
import { Button, Col, Flex, Modal, Row, Space, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { useInView } from 'react-intersection-observer';
import { useShallow } from 'zustand/shallow';

interface HAssetSelectProps {
  value?: string[];
  max?: number;
  onChange?(value: string[]): void;
}

export const HAssetSelect = (props: HAssetSelectProps) => {
  const [open, setOpen] = useState(false);

  const shopId = useProfileStore(useShallow((state) => state.shopId));
  const { ref, inView } = useInView();
  const { hasNextPage, fetchNextPage, data, refetch } = useInfiniteQuery({
    queryKey: ['HAssetSelect', shopId],
    queryFn: async ({ pageParam: offset }) => {
      const limit = 50;
      const resp = await MART_API.listShopAssets({
        shopId,
        limit,
        offset,
        noTotalCount: 1,
      });
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

  return (
    <Flex>
      <Button variant="outlined" onClick={() => setOpen(true)}>
        选择
      </Button>

      <Modal title={'选择图片'} open={open} onCancel={() => setOpen(false)} onOk={() => setOpen(false)} width={1000}>
        <div
          css={css`
            max-height: 700px;
            overflow: auto;
          `}
        >
          <Row>
            {data?.pages.map((page) =>
              page.items.map((o) => (
                <Col
                  span={6}
                  css={css`
                    padding: var(--ant-padding-xs);
                  `}
                >
                  <CheckCard
                    css={css`
                      max-width: 100%;
                    `}
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
                </Col>
              ))
            )}
          </Row>

          <div
            ref={ref}
            css={css`
              display: flex;
              justify-content: center;
            `}
          >
            {hasNextPage ? (
              <Space>
                <LoadingOutlined />
                加载更多数据中 ...
              </Space>
            ) : (
              <Typography.Text type="secondary">没有更多数据</Typography.Text>
            )}
          </div>
        </div>
      </Modal>
    </Flex>
  );
};
