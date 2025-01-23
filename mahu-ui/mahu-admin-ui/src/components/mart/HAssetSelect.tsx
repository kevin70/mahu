import { MART_API } from '@/services';
import { useProfileStore } from '@/stores';
import { LoadingOutlined } from '@ant-design/icons';
import { CheckCard } from '@ant-design/pro-components';
import { css } from '@emotion/react';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useSet } from 'ahooks';
import { Button, Col, Flex, Image, Modal, Row, Space, Typography } from 'antd';
import { CSSProperties, useEffect, useState } from 'react';
import { useInView } from 'react-intersection-observer';
import { useShallow } from 'zustand/shallow';

interface HAssetSelectProps {
  width?: number;
  height?: number;
  value?: string;
  onChange?(value?: string): void;
}

interface HAssetMultipleSelectProps {
  value?: string[];
  max?: number;
  onChange?(value: string[]): void;
}

const ImagePanel = ({ value, onChange }: { value?: string[]; onChange(value: string[]): void }) => {
  const { ref, inView } = useInView();
  const shopId = useProfileStore(useShallow((state) => state.shopId));
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

  // 选中的资源
  const [selectedAssets, { add, remove }] = useSet(value || []);

  useEffect(() => {
    onChange?.(Array.from(selectedAssets));
  }, [onChange, selectedAssets]);

  return (
    <>
      <Row>
        {data?.pages.map((page) =>
          page.items.map((o) => (
            <Col
              key={o.id}
              span={6}
              css={css`
                padding: var(--ant-padding-xs);
              `}
            >
              <CheckCard
                css={css`
                  max-width: 100%;
                `}
                checked={selectedAssets.has(o.uri)}
                cover={<img src={o.uri} />}
                onChange={(checked) => {
                  if (checked) {
                    add(o.uri);
                  } else {
                    remove(o.uri);
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
    </>
  );
};

export const HAssetSelect = (props: HAssetSelectProps) => {
  const [open, setOpen] = useState(false);

  // 选中的资源图片
  const [selectedAsset, setSelectedAsset] = useState(props.value);

  return (
    <Flex gap={'middle'}>
      <div
        onClick={() => setOpen(true)}
        style={
          {
            '--w': (props.width || 96) + 'px',
            '--h': (props.height || 96) + 'px',
          } as CSSProperties
        }
        css={[
          css`
            width: var(--w);
            height: var(--h);
            cursor: pointer;
            border: 1px dashed var(--ant-color-border);
            color: var(--ant-color-text-secondary);
            display: inline-flex;
            justify-content: center;
            align-items: center;
          `,
          css([
            selectedAsset && {
              backgroundImage: `url(${selectedAsset})`,
              backgroundSize: 'cover',
              border: 'none',
            },
          ]),
        ]}
      ></div>
      {/* <Button
        type="dashed"
      >
      </Button> */}

      <Modal
        title={'选择图片'}
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => {
          setOpen(false);
          props.onChange?.(selectedAsset);
        }}
        width={1000}
      >
        <div
          css={css`
            max-height: 700px;
            overflow: auto;
          `}
        >
          <ImagePanel
            value={selectedAsset ? [selectedAsset] : undefined}
            onChange={(value) => {
              if (value && value.length > 0) {
                setSelectedAsset(value[value.length - 1]);
              }
            }}
          />
        </div>
      </Modal>
    </Flex>
  );
};

export const HAssetMultipleSelect = (props: HAssetMultipleSelectProps) => {
  const [open, setOpen] = useState(false);

  // 选中的资源图片
  const [selectedAssets, setSelectedAssets] = useState(props.value || []);

  return (
    <Flex gap={'middle'}>
      {(props.value || []).map((uri) => (
        <Image width={96} height={96} key={uri} src={uri} />
      ))}

      <Button
        type="dashed"
        onClick={() => setOpen(true)}
        css={css`
          width: 96px;
          height: 96px;
        `}
      >
        选择图片
      </Button>

      <Modal
        title={'选择图片'}
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => {
          setOpen(false);
          props.onChange?.(Array.from(selectedAssets.values()));
        }}
        width={1000}
      >
        <Row>
          <Col
            span={4}
            css={css`
              display: flex;
              flex-direction: column;
              row-gap: var(--ant-margin-sm);
              max-height: 700px;
              overflow: auto;
              padding: 0 var(--ant-padding-sm);
            `}
          >
            {Array.from(selectedAssets.values()).map((uri, i) => (
              <Image key={i} src={uri} preview={false} />
            ))}
          </Col>
          <Col
            span={20}
            css={css`
              max-height: 700px;
              overflow: auto;
              border-left: 4px solid var(--ant-color-border);
            `}
          >
            <ImagePanel value={selectedAssets} onChange={setSelectedAssets} />
          </Col>
        </Row>
      </Modal>
    </Flex>
  );
};
