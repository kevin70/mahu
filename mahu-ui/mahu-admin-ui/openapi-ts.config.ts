import { defineConfig, defaultPlugins } from '@hey-api/openapi-ts';

export default defineConfig({
  input: {
    path: `dist/openapi.yaml`,
  },
  output: {
    format: 'prettier',
    lint: 'eslint',
    path: './src/client',
    //case: 'camelCase',
  },

  plugins: [
    ...defaultPlugins,
    '@hey-api/client-axios',
    '@tanstack/react-query',
    {
      dates: true,
      name: '@hey-api/transformers',
    },
  ],
});
