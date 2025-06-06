import { defineConfig, defaultPlugins } from '@hey-api/openapi-ts';

export default defineConfig({
  input: {
    // filters: {
    //   operations: {
    //     exclude: ['GET /samples'],
    //   },
    // },
    path: `dist/openapi.yaml`,
  },
  output: {
    format: 'prettier',
    lint: 'eslint',
    path: './src/client',
  },
  plugins: [...defaultPlugins, '@hey-api/client-axios', '@tanstack/react-query'],
});
