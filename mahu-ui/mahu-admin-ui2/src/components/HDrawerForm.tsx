import { Drawer, Form, FormInstance, FormProps, Space, Spin } from '@arco-design/web-react';
import { PropsWithChildren, JSX, useState, useMemo, cloneElement, ReactNode } from 'react';
import { HSaveButton } from './HSaveButton';
import { HResetButton } from './HResetButton';
import useForm from '@arco-design/web-react/es/Form/useForm';
import { css } from '@emotion/react';

export const HDrawerForm = ({
  trigger,
  title,
  onInit,
  onFinish,
  formProps,
  children,
}: {
  trigger: JSX.Element;
  title?: ReactNode;
  onInit?: (form: FormInstance) => Promise<void> | void;
  onFinish: (values: any, form: FormInstance) => Promise<boolean> | void;
  formProps?: Omit<FormProps, 'children'>;
} & PropsWithChildren) => {
  const [visible, setVisible] = useState(false);
  const [initing, setIniting] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const loading = useMemo(() => {
    return initing || submitting;
  }, [initing, submitting]);

  const triggerDom = useMemo(() => {
    return cloneElement(trigger, {
      ...trigger.props,
      async onClick(e: any) {
        setVisible(true);
        trigger.props?.onClick?.(e);
      },
    });
  }, [trigger]);

  const [form] = useForm();
  return (
    <>
      {triggerDom}
      <Drawer
        title={title}
        width={640}
        visible={visible}
        onCancel={() => {
          setVisible(false);
        }}
        afterOpen={async () => {
          try {
            await onInit?.(form);
          } finally {
            setIniting(false);
          }
        }}
        footer={
          <Space>
            <HResetButton disabled={loading} onClick={() => form.resetFields()} />
            <HSaveButton disabled={loading} loading={submitting} onClick={() => form.submit()} />
          </Space>
        }
      >
        {initing && (
          <div
            css={css`
              display: flex;
              justify-content: center;
              padding-top: 30px;
            `}
          >
            <Spin size={32} />
          </div>
        )}
        {visible && !initing && (
          <Form
            layout="vertical"
            {...formProps}
            form={form}
            onSubmit={async (values) => {
              try {
                setSubmitting(true);
                const rs = await onFinish(values, form);
                if (rs) {
                  setVisible(false);
                }
              } finally {
                setSubmitting(false);
              }
            }}
          >
            {children}
          </Form>
        )}
      </Drawer>
    </>
  );
};
