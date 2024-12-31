import { resolveApiError, TOKEN_API } from '@/services';
import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

const tokenKey = `${import.meta.env.VITE_APP_NAME}.token`;

type AccessToken = {
  accessToken?: string;
  refreshToken?: string;
  expireTime?: number;
  createTime?: number;
  lastAccessTime?: number;
};

const tokenLocalState = create(
  persist<AccessToken>(() => ({}), { name: tokenKey, storage: createJSONStorage(() => localStorage) })
);

const tokenSessionState = create(
  persist<AccessToken>(() => ({}), { name: tokenKey, storage: createJSONStorage(() => sessionStorage) })
);

const isAccessTokenValid = (token: AccessToken) => {
  if (token.accessToken && token.expireTime) {
    const now = Date.now();
    if (token.expireTime > now) {
      return true;
    }
  }
  return false;
};

// 访问令牌
export const useTokenStore = create<{
  isRememberMe(): boolean;
  isAccessTokenValid: () => boolean;
  obtainAccessToken: () => Promise<string>;
  attachToken: (accessToken: string, refreshToken: string, expireSecs: number, rememberMe: boolean) => void;
  checkAndRefreshToken: () => void;
  clean: () => void;
}>((_, get) => ({
  isRememberMe() {
    if (tokenLocalState.getState().accessToken) {
      return true;
    }
    return false;
  },
  isAccessTokenValid() {
    const sessionState = tokenSessionState.getState();
    if (sessionState.accessToken) {
      return isAccessTokenValid(sessionState);
    }
    return isAccessTokenValid(tokenLocalState.getState());
  },
  async obtainAccessToken() {
    const sessionState = tokenSessionState.getState();
    if (sessionState.accessToken) {
      return sessionState.accessToken;
    }

    const localState = tokenLocalState.getState();
    tokenLocalState.setState((state) => ({ ...state, lastAccessTime: Date.now() }));
    return localState.accessToken!;
  },
  attachToken(accessToken, refreshToken, expireSecs, rememberMe) {
    const createTime = Date.now();
    const expireTime = createTime + expireSecs * 1000;
    if (rememberMe) {
      tokenLocalState.setState((state) => ({
        ...state,
        accessToken,
        refreshToken,
        expireTime,
        createTime,
        lastAccessTime: createTime,
      }));
    } else {
      tokenSessionState.setState((state) => ({
        ...state,
        accessToken,
        refreshToken,
        expireTime,
        createTime,
        lastAccessTime: createTime,
      }));
    }
  },
  async checkAndRefreshToken() {
    const rememberMe = get().isRememberMe();
    const token = rememberMe ? tokenLocalState.getState() : tokenSessionState.getState();
    if (token.accessToken && token.expireTime && token.refreshToken) {
      const diff = token.expireTime - Date.now();
      if (diff < 5 * 60 * 1000) {
        try {
          const rs = await TOKEN_API.login({
            clientId: import.meta.env.VITE_CLIENT_ID,
            grantType: 'refresh_token',
            refreshToken: token.refreshToken,
          });
          get().attachToken(rs.accessToken, rs.refreshToken, rs.expiresIn, rememberMe);
        } catch (e: any) {
          const err = await resolveApiError(e);
          console.error('刷新访问令牌出现异常', err);
          $gotoLogin();
        }
      }
    }
  },
  clean() {
    tokenLocalState.persist.clearStorage();
    tokenSessionState.persist.clearStorage();
  },
}));
