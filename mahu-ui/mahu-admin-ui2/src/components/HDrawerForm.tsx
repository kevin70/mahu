import { Drawer, Form, FormInstance, FormProps, Space } from '@arco-design/web-react';
import { PropsWithChildren, JSX, useState, useMemo, cloneElement, ReactNode } from 'react';
import { HSaveButton } from './HSaveButton';
import { HResetButton } from './HResetButton';
import useForm from '@arco-design/web-react/es/Form/useForm';

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
  const [submitting, setSubmitting] = useState(false);

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
        width={560}
        visible={visible}
        onCancel={() => {
          setVisible(false);
        }}
        afterOpen={() => {
          onInit?.(form);
        }}
        footer={
          <Space>
            <HResetButton disabled={submitting} onClick={() => form.resetFields()} />
            <HSaveButton loading={submitting} onClick={() => form.submit()} />
          </Space>
        }
      >
        {visible && (
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
