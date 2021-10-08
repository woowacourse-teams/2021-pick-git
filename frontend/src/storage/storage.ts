const STORAGE_KEY = {
  ACCESS_TOKEN: "accessToken",
  USER_NAME: "username",
  PORTFOLIO_LOCAL_UPDATE_TIME: "portfolioLocalUpdateTime",
  PORTFOLIO_SERVER_UPDATE_TIME: "portfolioServerUpdateTime",
};

export const getAccessToken: () => string | null = () => localStorage.getItem(STORAGE_KEY.ACCESS_TOKEN);
export const getUsername = () => localStorage.getItem(STORAGE_KEY.USER_NAME);
export const getPortfolioLocalUpdateTime = () =>
  new Date(JSON.parse(localStorage.getItem(STORAGE_KEY.PORTFOLIO_LOCAL_UPDATE_TIME) ?? "0"));
export const getPortfolioLocalUpdateTimeString = () => {
  const updateTime = new Date(JSON.parse(localStorage.getItem(STORAGE_KEY.PORTFOLIO_LOCAL_UPDATE_TIME) ?? "0"));
  const ISOString = updateTime.toISOString();
  const closeIndex = ISOString.indexOf(".");
  return ISOString.substring(0, closeIndex);
};

export const setAccessToken = (accessToken: string) => localStorage.setItem(STORAGE_KEY.ACCESS_TOKEN, accessToken);
export const setUsername = (username: string) => localStorage.setItem(STORAGE_KEY.USER_NAME, username);
export const setPortfolioLocalUpdateTime = (updateTime: Date) =>
  localStorage.setItem(STORAGE_KEY.PORTFOLIO_LOCAL_UPDATE_TIME, JSON.stringify(updateTime));
