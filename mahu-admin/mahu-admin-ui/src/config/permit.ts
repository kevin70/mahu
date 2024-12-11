interface Permit {
  R: string;
  W: string;
  D: string;
}

const p = (name: string): Permit => {
  return {
    R: `${name}:R`,
    W: `${name}:W`,
    D: `${name}:D`,
  };
};

/**
 * 系统权限定义.
 */
export const permits = {
  EMPLOYEE: p('EMPLOYEE'),
  ROLE: p('ROLE'),
  DEPARTMENT: p('DEPARTMENT'),
  DICT: p('DICT'),
  CLIENT: p('CLIENT'),
  ACCESS_LOG: p('ACCESS_LOG'),
  AUDIT_JOUR: p('AUDIT_JOUR'),
  BRAND: p('BRAND'),

  MARKET_SHOP: p('MARKET_SHOP'),
  MARKET_BRAND: p('MARKET_BRAND'),
  MARKET_CATEGORY: p('MARKET_CATEGORY'),
  MARKET_SHOP_IMAGE: p('MARKET_SHOP_IMAGE'),
  MARKET_SHOP_PRODUCT: p('MARKET_SHOP_PRODUCT'),
  MARKET_SHOP_ORDER: p('MARKET_SHOP_ORDER'),

  ORDER_DEPOSIT: p('ORDER_DEPOSIT'),
  ORDER_PRODUCT: p('ORDER_PRODUCT'),
};
