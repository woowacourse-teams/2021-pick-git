const useLocalStorage = () => {
  const accessToken = localStorage.getItem("accessToken") ?? "testToken";

  return {
    accessToken,
  };
};

export default useLocalStorage;
