import { BaseApi, LogApi, MartApi, MeApi, SystemApi, TokenApi } from './generated';

// 基础接口
export const BASE_API = new BaseApi();
// 访问令牌
export const TOKEN_API = new TokenApi();
// 个人信息接口
export const ME_API = new MeApi();
// 系统接口
export const SYSTEM_API = new SystemApi();
// 日志接口
export const LOG_API = new LogApi();
// 商城接口
export const MART_API = new MartApi();
