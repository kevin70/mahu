import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

type Theme = 'light' | 'dark';

export const useAppStore = create(
  persist<{
    theme: Theme;
    isLightTheme(): boolean;
    changeTheme(theme: Theme): void;
  }>(
    (set, get) => ({
      theme: 'light',
      selectedShopId: -1,
      isLightTheme() {
        return get().theme === 'light';
      },
      changeTheme(theme) {
        set({ theme });
      },
    }),
    {
      name: `${import.meta.env.VITE_APP_NAME}.data`,
      storage: createJSONStorage(() => localStorage),
    }
  )
);
