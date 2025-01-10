import i18n from 'i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import { initReactI18next } from 'react-i18next';

// 中文
import translationZhCN from './zh-CN/translation.zh-CN.json';

// 英文
import translationEnUS from './en/translation.en.json';

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    debug: import.meta.env.DEV,
    fallbackLng: 'zh-CN',
    resources: {
      'zh-CN': {
        translation: translationZhCN,
      },
      'en-US': {
        translation: translationEnUS,
      },
    },
  });

export default i18n;
