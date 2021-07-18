import { useState } from "react";

const STORAGE_KEY = {
  ACCESS_TOKEN: "accessToken",
  USER_NAME: "username",
};

const storage = () => {
  const getAccessToken: () => string | null = () => localStorage.getItem(STORAGE_KEY.ACCESS_TOKEN);
  const getUsername = () => localStorage.getItem(STORAGE_KEY.USER_NAME);

  const setAccessToken = (accessToken: string) => localStorage.setItem(STORAGE_KEY.ACCESS_TOKEN, accessToken);
  const setUsername = (username: string) => localStorage.setItem(STORAGE_KEY.USER_NAME, username);

  return {
    getAccessToken,
    getUsername,
    setAccessToken,
    setUsername,
  };
};

export default storage;
