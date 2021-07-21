const httpErrorStatus = {
  "400": "badRequest",
  "401": "unauthorized",
  "403": "forbidden",
  "404": "notFound",
  "405": "methodNotAllowed",
  "409": "conflict",
  "429": "tooManyRequests",
  "500": "serverError",
} as const;

const httpErrorStatusNames = Object.values(httpErrorStatus);

type HTTPErrorHandler = {
  [V in typeof httpErrorStatusNames[number]]?: () => void;
};

type HTTPErrorStatus = keyof typeof httpErrorStatus;

const isValidHTTPErrorStatus = (status: string): status is HTTPErrorStatus =>
  Object.keys(httpErrorStatus).includes(status);

export const handleHTTPError = (errorStatus: number | string, handler: HTTPErrorHandler) => {
  const stringifiedStatus = JSON.stringify(errorStatus);

  if (!isValidHTTPErrorStatus(stringifiedStatus)) {
    throw Error("Invalid status code");
  }

  const currentHandler = handler[httpErrorStatus[stringifiedStatus]];

  if (!currentHandler) {
    throw Error("undefined handler");
  }

  currentHandler();
};
