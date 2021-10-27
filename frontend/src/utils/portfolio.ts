import { TEMP_ID_INDICATOR } from "../constants/portfolio";

export const getTemporaryId = (weight?: number) => `${TEMP_ID_INDICATOR}${new Date().getTime() + (weight ?? 0)}`;
