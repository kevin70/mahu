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

  MART_SHOP: p('MART_SHOP'),
  MART_ASSET: p('MART_ASSET'),
  MART_CATEGORY: p('MART_CATEGORY'),
  MART_SHOP_IMAGE: p('MART_SHOP_IMAGE'),
  MART_SHOP_PRODUCT: p('MART_SHOP_PRODUCT'),
  MART_SHOP_ORDER: p('MART_SHOP_ORDER'),
  MART_ATTRIBUTE: p('MART_ATTRIBUTE'),

  ORDER_DEPOSIT: p('ORDER_DEPOSIT'),
  ORDER_PRODUCT: p('ORDER_PRODUCT'),
};
