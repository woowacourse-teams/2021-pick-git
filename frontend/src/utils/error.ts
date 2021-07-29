import { ErrorCode, HTTPErrorHandler, HTTPErrorStatus } from "../@types";
import { httpErrorStatus } from "../constants/error";
import { API_ERROR_MESSAGE, UNKNOWN_ERROR_MESSAGE } from "../constants/messages";

export const handleHTTPError = (errorStatus: HTTPErrorStatus, handler: HTTPErrorHandler) => {
  const currentHandler = handler[httpErrorStatus[errorStatus]];

  if (!currentHandler) {
    throw Error("undefined handler");
  }

  currentHandler();
};

export const getAPIErrorMessage = (errorCode: ErrorCode) => {
  return API_ERROR_MESSAGE[errorCode] ?? UNKNOWN_ERROR_MESSAGE;
};

export const customError = {
  noAccessToken: Error("C0001"),
};
