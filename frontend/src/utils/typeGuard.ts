import { ErrorCode, HTTPErrorStatus } from "../@types";
import { httpErrorStatus } from "../constants/httpError";
import { API_ERROR_MESSAGE } from "../constants/messages";

export const isErrorCode = (errorCode: string): errorCode is ErrorCode => errorCode in API_ERROR_MESSAGE;

export const isHttpErrorStatus = (status: number): status is HTTPErrorStatus => status in httpErrorStatus;
