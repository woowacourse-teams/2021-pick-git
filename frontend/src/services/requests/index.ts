import axios from "axios";

switch (process.env.DEPLOY) {
  case "main":
    axios.defaults.baseURL = "https://api.pick-git.com/api";
    break;
  case "develop":
    axios.defaults.baseURL = "http://devapi.pick-git.com:8080/api";
    break;
  default:
    axios.defaults.baseURL = "http://devapi.pick-git.com:8080/api";
}

export * from "./profile";
export * from "./account";
export * from "./posts";
export * from "./githubStats";
export * from "./github";
export * from "./search";
