const STORAGE_KEY = {
  ACCESS_TOKEN: "accessToken",
  USER_NAME: "userName",
};

const useLocalStorage = () => {
  const accessToken = localStorage.getItem(STORAGE_KEY.ACCESS_TOKEN);
  const userName = localStorage.getItem(STORAGE_KEY.USER_NAME);

  const setAccessToken = (accessToken: string) => localStorage.setItem(STORAGE_KEY.ACCESS_TOKEN, accessToken);
  const setUserName = (userName: string) => localStorage.setItem(STORAGE_KEY.USER_NAME, userName);

  return {
    accessToken,
    userName,
    setAccessToken,
    setUserName,
  };
};

export default useLocalStorage;
