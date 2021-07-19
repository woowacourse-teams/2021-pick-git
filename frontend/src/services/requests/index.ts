import axios from "axios";

switch (process.env.DEPLOY) {
  case "main":
    axios.defaults.baseURL = "http://api.pickgit.p-e.kr/api";
    break;
  case "develop":
    axios.defaults.baseURL = "http://devapi.pickgit.p-e.kr:8080/api";
    break;
  default:
    axios.defaults.baseURL = "/api";
}

export * from "./profile";
export * from "./account";
export * from "./posts";
export * from "./githubStats";
export * from "./github";
