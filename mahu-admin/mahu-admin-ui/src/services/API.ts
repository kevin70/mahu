import { MeApi, SystemApi, TokenApi } from './generated';

// 访问令牌
export const TOKEN_API = new TokenApi();
// 个人信息接口
export const ME_API = new MeApi();
// 系统接口
export const SYSTEM_API = new SystemApi();
