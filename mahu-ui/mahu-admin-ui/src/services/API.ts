import { BaseApi, BasisApi, MartApi, MeApi, SystemApi, TokenApi } from './generated';

// 基础接口
export const BASE_API = new BaseApi();
// 访问令牌
export const TOKEN_API = new TokenApi();
// 个人信息接口
export const ME_API = new MeApi();
// 系统接口
export const SYSTEM_API = new SystemApi();
// 基础接口
export const BASIS_API = new BasisApi();
// 商城接口
export const MART_API = new MartApi();
