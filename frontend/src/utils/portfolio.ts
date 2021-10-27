import { TEMP_ID_INDICATOR } from "../constants/portfolio";

export const getTemporaryId = (weight?: number) => `${TEMP_ID_INDICATOR}${new Date().getTime() + (weight ?? 0)}`;

export const isTempId = (id: string | number): id is string => {
  return typeof id === "string" && id.includes(TEMP_ID_INDICATOR);
};
