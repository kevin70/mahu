import { permits } from '@/config/permit';
import { BASIS_API, MART_API, resolveApiError, uploadFile } from '@/services';
import { useProfileStore } from '@/stores';
import { DeleteOutlined, LoadingOutlined, UploadOutlined } from '@ant-design/icons';
import { CheckCard, ModalForm, PageContainer, ProFormUploadDragger } from '@ant-design/pro-components';
import { css } from '@emotion/css';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useCounter, useMap } from 'ahooks';
import { Alert, Button, Flex, FormInstance, Image, message, Modal, Space, Typography } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { useInView } from 'react-intersection-observer';
import { useShallow } from 'zustand/shallow';

export const MartAssetList = () => {
  const noWrite = $checkNotPermit(permits.MART_ASSET.W);
  const shopId = useProfileStore(useShallow((state) => state.shopId));
  const { ref, inView } = useInView();
  const { hasNextPage, fetchNextPage, data, refetch } = useInfiniteQuery({
    queryKey: ['MarketAssetList', shopId],
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

  const uploadPolicy = async (fileName: string) => {
    return BASIS_API.makeOssDirectUpload({
      makeOssDirectUploadRequest: {
        kind: 'MART_ASSET',
        fileName: fileName,
      },
    });
  };

  const UploadModal = () => {
    const [uc, ucOps] = useCounter(0);
    const formRef = useRef<FormInstance>();
    const submit = async (values: any) => {
      const uris = values.files.filter((f: any) => f.status === 'done').map((f: any) => f.response.accessUrl);
      await MART_API.addShopAsset({
        shopId,
        addShopAssetRequest: {
          uris,
        },
      });
      message.success('上传成功');
    };

    return (
      <ModalForm
        title="上传资源"
        modalProps={{
          centered: true,
          afterClose() {
            if (uc > 0) {
              refetch();
            }
            ucOps.reset();
          },
        }}
        trigger={
          <Button type="primary" icon={<UploadOutlined />}>
            上传资源
          </Button>
        }
        formRef={formRef}
        onFinish={async (values) => {
          await submit(values);
          formRef.current?.resetFields();
          ucOps.inc();
          return false;
        }}
      >
        <ProFormUploadDragger
          name="files"
          max={99}
          fieldProps={{
            multiple: true,
            accept: 'image/*',
            height: 260,
            beforeUpload(file, fileList) {
              const limit = 1 * 1024 * 1024;
              if (file.size > limit) {
                message.error(`${file.name} 文件大小超出 1 MB`);
                return false;
              }
            },
            async customRequest(opts) {
              try {
                const policy = await uploadPolicy(opts.filename!);
                await uploadFile(
                  policy.endpoint,
                  {
                    key: policy.key,
                    policy: policy.policy,
                    accessKeyId: policy.accessKeyId,
                    signature: policy.signature,
                    file: opts.file,
                  },
                  (event) => {
                    const percent = Math.round((event.loaded * 100) / (event.total || 1));
                    opts.onProgress?.({ percent });
                  }
                );
                opts.onSuccess?.({ accessUrl: policy.accessUrl });
              } catch (error) {
                const err = await resolveApiError(error);
                opts.onError?.({
                  ...err,
                });
              }
            },
          }}
        />
      </ModalForm>
    );
  };

  const DeleteModal = () => {
    const [open, setOpen] = useState(false);

    const onDelete = async () => {
      const assetIds = Array.from(selectAssetMap.keys());
      await MART_API.batchDeleteShopAsset({
        shopId,
        batchDeleteShopAssetRequest: {
          assetIds,
        },
      });

      message.success('删除资源成功');
      resetSelectAsset();
      refetch();
    };
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
          onOk={onDelete}
        >
          <Flex
            justify="center"
            wrap
            gap={'small'}
            className={css`
              max-height: 480px;
              overflow: auto;
            `}
          >
            {Array.from(selectAssetMap.entries()).map((entry) => (
              <Image src={entry[1]} width={120} />
            ))}
          </Flex>
        </Modal>
      </>
    );
  };

  if (shopId <= 0) {
    return <Alert type="error" showIcon banner message={'请在右上角选择商店'} />;
  }

  return (
    <PageContainer
      fixedHeader
      extra={
        <Space>
          <UploadModal />
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
