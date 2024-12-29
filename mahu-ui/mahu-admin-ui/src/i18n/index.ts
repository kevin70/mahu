import i18n from 'i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import { initReactI18next } from 'react-i18next';

// 中文
import translationZhCN from './zhCN/translation_zhCN.json';

// 英文
import translationEnUS from './en/translation_en.json';

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    debug: import.meta.env.DEV,
    supportedLngs: ['zhCN', 'enUS'],
    fallbackLng: 'zhCN',
    resources: {
      zhCN: {
        translation: translationZhCN,
      },
      enUS: {
        translation: translationEnUS,
      },
    },
  });

export default i18n;
