export default function localPlugin() {
  return {
    id: 'local',
    assertions: {
      checkWordsStarts: (value, options, ctx) => {
        const regexp = new RegExp(`^${options.words.join('|')}`);
        if (regexp.test(value)) {
          return [];
        }
        return [
          {
            message: '前缀不匹配',
            location: ctx.baseLocation,
          },
        ];
      },
      checkWordsCount: (value, options, ctx) => {
        const words = value.split(' ');
        if (words.length >= options.min) {
          return [];
        }
        return [
          {
            message: `最少 ${options.min} 字符`,
            location: ctx.baseLocation,
          },
        ];
      },
    },
  };
}
