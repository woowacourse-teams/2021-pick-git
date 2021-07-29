import { ErrorCode } from "../@types";
import { API_ERROR_MESSAGE, UNKNOWN_ERROR_MESSAGE } from "../constants/messages";

export const getAPIErrorMessage = (errorCode: ErrorCode) => {
  return API_ERROR_MESSAGE[errorCode] ?? UNKNOWN_ERROR_MESSAGE;
};

export const customError = {
  noAccessToken: Error("C0001"),
};
