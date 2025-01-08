import i18next from 'i18next';
import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

type Theme = 'light' | 'dark';
type Lng = 'zh-CN' | 'en-US';

const getSysTheme = (): Theme => {
  const media = window.matchMedia('(prefers-color-scheme: dark)');
  if (media.matches) {
    return 'dark';
  }
  return 'light';
};

export const useAppStore = create(
  persist<{
    // 主题
    theme: Theme;
    isLightTheme(): boolean;
    changeTheme(theme: Theme): void;
    // 语言
    lng: Lng;
    changeLng(lng: Lng): void;
  }>(
    (set, get) => ({
      theme: getSysTheme(),
      isLightTheme() {
        return get().theme === 'light';
      },
      changeTheme(theme) {
        set({ theme });
      },
      lng: i18next.language === 'en-US' ? 'en-US' : 'zh-CN',
      changeLng(lng) {
        set({ lng: lng });
        i18next.changeLanguage(lng);
      },
    }),
    {
      name: `${import.meta.env.VITE_APP_NAME}.data`,
      storage: createJSONStorage(() => localStorage),
    }
  )
);
