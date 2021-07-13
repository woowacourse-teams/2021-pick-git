import axios from "axios";

axios.defaults.baseURL = "http://localhost:3001/api";

export * from "./profile";
export * from "./account";
