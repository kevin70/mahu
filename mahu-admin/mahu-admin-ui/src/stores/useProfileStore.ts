import { ME_API } from '@/services';
import { create } from 'zustand';

type Profile = {
  uid: number;
  nickname: string;
  avatar: string;
  permits: string[];

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

  async refreshProfile() {
    const data = await ME_API.getMeProfile();
    set({
      uid: data.uid,
      nickname: data.nickname,
      avatar: data.avatar,
      permits: data.permits || [],
    });
    return get();
  },
}));
