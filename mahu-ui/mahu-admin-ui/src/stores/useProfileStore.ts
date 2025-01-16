import { ME_API } from '@/services';
import { create } from 'zustand';

type Profile = {
  uid: number;
  nickname: string;
  avatar: string;
  permits: string[];
  shops: Array<{
    id: number;
    name: string;
  }>;
  shopId: number;
  // 修改商店 ID
  setShopId(shopId: number): void;
  // 刷新个人信息
  refreshProfile: () => Promise<Profile>;
};

/**
 * 个人信息.
 */
export const useProfileStore = create<Profile>((set, get) => ({
  uid: 0,
  nickname: '未知',
  avatar: '',
  permits: [],
  shops: [],
  shopId: -1,
  setShopId(shopId) {
    set({
      shopId,
    });
    setShopIdToLocal(get().uid, shopId);
  },
  async refreshProfile() {
    const data = await ME_API.getMeProfile();

    const shops = data.shops ?? [];
    const shopId = getShopIdForLocal(data.uid);

    set({
      uid: data.uid,
      nickname: data.nickname,
      avatar: data.avatar,
      permits: data.permits || [],
      shops,
      shopId: shops.findIndex((o) => o.id === shopId) >= 0 ? shopId : -1,
    });

    return get();
  },
}));

const getShopIdForLocal = (uid: number) => {
  // 加载缓存中的商店 ID
  const key = `user.${uid}.shopId`;
  const cacheShopId = localStorage.getItem(key);
  if (cacheShopId) {
    const shopId = parseInt(cacheShopId);
    return shopId;
  }
  return -1;
};

const setShopIdToLocal = (uid: number, shopId: number) => {
  const key = `user.${uid}.shopId`;
  localStorage.setItem(key, shopId.toString());
};
