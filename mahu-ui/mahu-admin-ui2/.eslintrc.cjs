module.exports = {
  extends: ['plugin:@tanstack/query/recommended'],
  parserOptions: {
    project: './tsconfig.json',
  },
  rules: {
    'react/react-in-jsx-scope': 'off',
  },
  'import/extensions': [
    'error',
    'ignorePackages',
    {
      ts: 'never',
      tsx: 'never',
    },
  ],
};
