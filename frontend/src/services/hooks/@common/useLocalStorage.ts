const STORAGE_KEY = {
  ACCESS_TOKEN: "accessToken",
};

const useLocalStorage = () => {
  const accessToken = localStorage.getItem(STORAGE_KEY.ACCESS_TOKEN);

  const setAccessToken = (accessToken: string) => localStorage.setItem(STORAGE_KEY.ACCESS_TOKEN, accessToken);

  return {
    accessToken,
    setAccessToken,
  };
};

export default useLocalStorage;
