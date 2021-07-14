export const PAGE_URL = {
  HOME: "/",
  LOGIN: "/login",
  PROFILE: "/profile",
  ADD_POST: "/add-post",
  SEARCH: "/search",
};

export const API_URL = {
  SELF_PROFILE: "/profiles/me",
  AFTER_LOGIN: (code: string) => `afterlogin?code=${code}`,
  AUTH: {
    GITHUB: "/authorization/github",
  },
};
