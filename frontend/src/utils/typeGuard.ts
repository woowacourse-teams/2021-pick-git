import { InfiniteData } from "react-query";
import { ClientErrorCode, HTTPErrorStatus } from "../@types";
import { httpErrorStatus } from "../constants/error";
import { CLIENT_ERROR_MESSAGE } from "../constants/messages";

export const isClientErrorCode = (errorCode: string): errorCode is ClientErrorCode => errorCode in CLIENT_ERROR_MESSAGE;

export const isHttpErrorStatus = (status: number): status is HTTPErrorStatus => status in httpErrorStatus;

export const isInfiniteData = <TData>(data: InfiniteData<TData> | TData): data is InfiniteData<TData> =>
  "pages" in data;
