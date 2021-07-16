import axios from "axios";

axios.defaults.baseURL = process.env.NODE_ENV === "production" ? "http://devapi.pickgit.p-e.kr:8080/api" : "/api";

export * from "./profile";
export * from "./account";
export * from "./posts";
export * from "./githubStats";
