import { getDateString } from "../utils/date";

const STORAGE_KEY = {
  ACCESS_TOKEN: "accessToken",
  USER_NAME: "username",
  PORTFOLIO_LOCAL_UPDATE_TIME: (username: string) => `${username}-portfolioLocalUpdateTime`,
};

export const getAccessToken: () => string | null = () => localStorage.getItem(STORAGE_KEY.ACCESS_TOKEN);
export const getUsername = () => localStorage.getItem(STORAGE_KEY.USER_NAME);
export const getPortfolioLocalUpdateTime = () => {
  const localUpdateTime = localStorage.getItem(STORAGE_KEY.PORTFOLIO_LOCAL_UPDATE_TIME(getUsername() ?? ""));

  if (!localUpdateTime) {
    return null;
  }

  return new Date(localUpdateTime);
};

export const getPortfolioLocalUpdateTimeString = () => {
  const updateTime = new Date(
    localStorage.getItem(STORAGE_KEY.PORTFOLIO_LOCAL_UPDATE_TIME(getUsername() ?? "")) ?? "0"
  );
  const hoursAddedTime = new Date(updateTime.getTime() + 9 * 60 * 60 * 1000);
  const ISOString = hoursAddedTime.toISOString();
  const closeIndex = ISOString.indexOf(".");
  return ISOString.substring(0, closeIndex);
};

export const setAccessToken = (accessToken: string) => localStorage.setItem(STORAGE_KEY.ACCESS_TOKEN, accessToken);
export const setUsername = (username: string) => localStorage.setItem(STORAGE_KEY.USER_NAME, username);
export const setPortfolioLocalUpdateTime = (updateTime: Date) => {
  const dateTime = getDateString(updateTime);

  return localStorage.setItem(STORAGE_KEY.PORTFOLIO_LOCAL_UPDATE_TIME(getUsername() ?? ""), dateTime);
};
