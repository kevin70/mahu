import MaterialSymbolsLogin from '@/icons/MaterialSymbolsLogin';
import { Button, Checkbox, Form } from '@douyinfe/semi-ui';
import { css } from '@styled-system/css';
import { Flex, VStack } from '@styled-system/jsx';

export const Login = () => {
  const LoginForm = () => {
    //
    return (
      <Form
        className={css({
          width: 'md',
          display: 'flex',
          flexDir: 'column',
          rowGap: 4,
        })}
      >
        <Form.Input label={{ text: '用户名', required: true }} field="username" placeholder="输入用户名" />
        <Form.Input label={{ text: '密码', required: true }} field="field1" placeholder="输入密码" />
        <Checkbox type="default">记住我</Checkbox>
        <Button block theme="solid" icon={<MaterialSymbolsLogin />}>
          登录
        </Button>
      </Form>
    );
  };

  return (
    <Flex
      justify={'flex-end'}
      align={'center'}
      className={css({
        h: '100vh',
        w: '100vw',
      })}
    >
      <div
        className={css({
          mr: '10%',
          bg: 'var(--semi-color-bg-0)',
          px: 10,
          py: 12,
          boxShadow: 'token(shadows.md)',
          borderRadius: 'md',
        })}
      >
        <VStack>
          <img
            className={css({
              w: 20,
              h: 20,
            })}
            src="https://lf6-static.semi.design/obj/semi-tos/template/99042ce4-7934-4188-b15a-90ea03b3f63d.svg"
          />
          <div
            className={css({
              display: 'inline-flex',
              flexDirection: 'column',
              flexShrink: 0,
              alignItems: 'center',
              justifyContent: 'center',
              rowGap: 6,
            })}
          >
            <p>欢迎回来</p>
            <p
              className={css({
                color: 'var(--semi-color-text-2)',
                letterSpacing: 'normal',
                fontSize: 16,
                fontWeight: 400,
              })}
            >
              <span>登录</span>
              <span> Semi Design </span>
              <span>账户</span>
            </p>
          </div>
        </VStack>

        <LoginForm />
      </div>
    </Flex>
  );
};
