import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('i18next')) {
              return 'i18next';
            }
            if (id.includes('antd') || id.includes('@ant-design')) {
              return 'antd-pro';
            }
            return 'vendor';
          }
        },
      },
    },
  },
});
