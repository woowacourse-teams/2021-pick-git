const STORAGE_KEY = {
  ACCESS_TOKEN: "accessToken",
  USER_NAME: "username",
};

export const getAccessToken: () => string | null = () => localStorage.getItem(STORAGE_KEY.ACCESS_TOKEN);
export const getUsername = () => localStorage.getItem(STORAGE_KEY.USER_NAME);

export const setAccessToken = (accessToken: string) => localStorage.setItem(STORAGE_KEY.ACCESS_TOKEN, accessToken);
export const setUsername = (username: string) => localStorage.setItem(STORAGE_KEY.USER_NAME, username);
