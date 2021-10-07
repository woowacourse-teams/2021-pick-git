import { QueryClient } from "react-query";
import { UserItem } from "../../@types";

export class Blob {
  size: number;

  constructor(parts: (string | Blob | ArrayBuffer | ArrayBufferView)[]) {
    this.size = parts.length;
  }
}

export class File extends Blob {
  name: string;

  constructor(parts: (string | Blob | ArrayBuffer | ArrayBufferView)[], name: string, properties?: FilePropertyBag) {
    super(parts);
    this.name = name;
  }
}

export const USERNAME = "tanney-102";
export const VALID_ACCESS_TOKEN = "valid_access_token";
export const INVALID_ACCESS_TOKEN = "invalid_access_token";
export const DEFAULT_PROFILE_QUERY_DATA = {
  name: USERNAME,
  imageUrl: "",
  description: "",
  followerCount: 0,
  followingCount: 0,
  postCount: 0,
  githubUrl: "",
  company: "",
  location: "",
  website: "",
  twitter: "",
  following: false,
};
export const MOCK_USER: UserItem = {
  username: "chris",
  imageUrl: "imageUrl",
  following: true,
};
export const EMPTY_PAGE = [[]];

export const INVALID_ERROR_CODE = "invalid_error_code";
export const UNAUTHORIZED_TOKEN_ERROR = "A0001";

export const mockFn = {
  pushSnackbarMessage: jest.fn(),
  logout: jest.fn(),
  messageViewer: jest.fn(),
  submitEffect: jest.fn(),
};

export const setLocalStorageValid = () => {
  localStorage.setItem("username", USERNAME);
  localStorage.setItem("accessToken", VALID_ACCESS_TOKEN);
};

export const setLocalStorageInvalid = () => {
  localStorage.setItem("username", USERNAME);
  localStorage.setItem("accessToken", INVALID_ACCESS_TOKEN);
};
export const setLocalStorageEmpty = () => {
  localStorage.setItem("username", "");
  localStorage.setItem("accessToken", "");
};

export const createQueryClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
      mutations: {
        retry: false,
      },
    },
  });

export const keyword = (() => {
  let currentValue = "";

  return {
    get current() {
      return currentValue;
    },

    change(value: string) {
      currentValue = value;
    },
  };
})();
