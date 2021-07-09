const useLocalStorage = () => {
  const accessToken = JSON.parse(localStorage.getItem("accessToken") ?? "testToken");

  return {
    accessToken,
  };
};

export default useLocalStorage;
