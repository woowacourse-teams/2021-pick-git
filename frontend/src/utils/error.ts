import { APIErrorCode, ClientErrorCode, ClientErrorHandler, HTTPErrorHandler, HTTPErrorStatus } from "../@types";
import { clientErrorCodeMap, httpErrorStatus } from "../constants/error";
import { API_ERROR_MESSAGE, CLIENT_ERROR_MESSAGE, UNKNOWN_ERROR_MESSAGE } from "../constants/messages";

export const handleHTTPError = (errorStatus: HTTPErrorStatus, handler: HTTPErrorHandler) => {
  const currentHandler = handler[httpErrorStatus[errorStatus]];

  if (!currentHandler) {
    throw Error("undefined handler");
  }

  currentHandler();
};

export const handleClientError = (errorCode: ClientErrorCode, handler: ClientErrorHandler) => {
  const currentHandler = handler[clientErrorCodeMap[errorCode]];

  if (!currentHandler) {
    throw Error("undefined handler");
  }

  currentHandler();
};

export const getAPIErrorMessage = (errorCode: APIErrorCode) => {
  return API_ERROR_MESSAGE[errorCode] ?? UNKNOWN_ERROR_MESSAGE;
};

export const getClientErrorMessage = (errorCode: ClientErrorCode) => {
  return CLIENT_ERROR_MESSAGE[errorCode] ?? UNKNOWN_ERROR_MESSAGE;
};

export const customError = {
  noAccessToken: Error("C0001"),
  fileReader: Error("C0002"),
};
