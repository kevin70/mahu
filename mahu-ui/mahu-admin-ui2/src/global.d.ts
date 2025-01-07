import { AlertProps } from '@arco-design/web-react';

declare global {
  interface Window {
    /**
     * 获取访问令牌.
     */
    $accessToken(): Promise<string>;
    /**
     * 校验是否拥有指定的权限.
     * @param args 权限代码
     */
    $checkPermit(args: string | string[]): boolean;
    /**
     * 校验是否不拥有指定的权限.
     * @param args 权限代码
     */
    $checkNotPermit(args: string | string[]): boolean;
    /**
     * 去到登录页.
     */
    $gotoLogin();
    /**
     * 全局警告提示.
     * @param args 警告消息
     */
    $showAlert(props: AlertProps): void;
  }

  declare const $accessToken = window.$accessToken;

  declare const $checkNotPermit = window.$checkPermit;

  declare const $checkNotPermit = window.$checkNotPermit;

  declare const $gotoLogin = window.$gotoLogin;

  declare const $showAlert = window.$showAlert;
}

export {};
