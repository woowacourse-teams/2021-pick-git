export const MAX_STACK_NUM = 3;
export const SNACKBAR_HEIGHT_REM = 2.3;
export const SNACKBAR_GAP_REM = 0.5;
export const SNACKBAR_DURATION = 4000;

export const snackBarOrders = Array.from({ length: MAX_STACK_NUM }, (_, index) => index + 1);
export type SnackBarOrder = typeof snackBarOrders[number];
