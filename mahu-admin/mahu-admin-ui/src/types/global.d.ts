import { AlertProps } from 'antd';
import { MessageInstance } from 'antd/lib/message/interface';

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
     * 全局消息提示.
     */
    $message(): MessageInstance;

    /**
     * 全局警告提示.
     * @param args 警告消息
     */
    $showAlert(args: Pick<AlertProps, 'type' | 'message'>): void;
  }

  declare const $accessToken = window.$accessToken;

  declare const $checkNotPermit = window.$checkPermit;

  declare const $checkNotPermit = window.$checkNotPermit;

  declare const $gotoLogin = window.$gotoLogin;

  declare const $message = window.$message;

  declare const $showAlert = window.$showAlert;
}

export {};
