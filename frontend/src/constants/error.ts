export const httpErrorStatus = {
  400: "badRequest",
  401: "unauthorized",
  403: "forbidden",
  404: "notFound",
  405: "methodNotAllowed",
  409: "conflict",
  429: "tooManyRequests",
  500: "serverError",
} as const;

export const httpErrorStatusName = Object.values(httpErrorStatus);

export const clientErrorCodeMap = {
  C0001: "noAccessToken",
  C0002: "fileReader",
} as const;

export const clientErrorCodeName = Object.values(clientErrorCodeMap);
