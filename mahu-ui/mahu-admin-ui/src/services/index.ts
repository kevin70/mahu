import { DefaultConfig, Configuration, ResponseError, FetchError, RequiredError } from './generated';

// 接口请求路径
const BASE_PATH = import.meta.env.VITE_API_BASE_URL;

/**
 * 接口错误响应.
 */
export interface ApiError extends Error {
  /**
   * 错误码.
   */
  code: number | 'FETCH_ERROR' | 'CLIENT_ERROR' | 'UNKNOWN';
  message: string;
}

/**
 * 默认配置.
 */
DefaultConfig.config = new Configuration({
  basePath: BASE_PATH,
  accessToken: async () => {
    return $accessToken();
  },
  middleware: [
    {
      async onError(context) {
        console.error('接口请求错误', context);

        // 401 需要重新登录
        if (context.response?.status === 401) {
          $gotoLogin();
        }
      },
    },
  ],
});

/**
 * 解决接口请求错误.
 * @param e 错误
 * @returns
 */
export const resolveApiError = async (e: unknown) => {
  if (e instanceof ResponseError) {
    const { response } = e;
    if (response.status === 503) {
      const _err: Error = {
        name: '503',
        message: '服务接口不可用',
      };
      return _err;
    }

    const { error } = await response.json();
    console.error('接口请求响应错误', error);

    const _err: Error = {
      name: error.code.toString(),
      message: error.message,
    };
    return _err;
  }

  if (e instanceof FetchError) {
    console.error('请求错误', e);
    const _err: Error = {
      name: 'FETCH_ERROR',
      message: '服务器连接失败',
    };
    return _err;
  }

  if (e instanceof RequiredError) {
    console.error('缺少请求参数', e.field, e);
    const _err: Error = {
      name: 'CLIENT_ERROR',
      message: `请求缺少字段[${e.field}]`,
    };
    return _err;
  }

  console.error('请求未知错误', e);
  const _err: Error = {
    name: 'UNKNOWN',
    message: `出现未知错误 - ${e}`,
  };
  return _err;
};

export * from './API';
export * from './aliyun';
