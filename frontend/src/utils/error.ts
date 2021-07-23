import { ErrorResponse } from "../@types";
import { API_ERROR_MESSAGE, UNKNOWN_ERROR_MESSAGE } from "../constants/messages";

export const getAPIErrorMessage = (errorCode: ErrorResponse["errorCode"]) => {
  return API_ERROR_MESSAGE[errorCode] ?? UNKNOWN_ERROR_MESSAGE;
};
