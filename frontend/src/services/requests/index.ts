import axios from "axios";

switch (process.env.DEPLOY) {
  case "main":
    axios.defaults.baseURL = "http://api.pick-git.com/api";
    break;
  case "develop":
    axios.defaults.baseURL = "http://api.pick-git.com/api";
    break;
  default:
    axios.defaults.baseURL = "https://api.pick-git.com/api/";
}

export * from "./profile";
export * from "./account";
export * from "./posts";
export * from "./githubStats";
export * from "./github";
export * from "./search";
