import axios from "axios";
import { PortfolioData } from "../../@types";
import { API_URL } from "../../constants/urls";
import { customError } from "../../utils/error";

export const requestSetPortfolio = async (username: string, portfolio: PortfolioData, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.put<PortfolioData>(API_URL.USER_PORTFOLIO(username), portfolio, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestGetPortfolio = async (username: string) => {
  const response = await axios.get<PortfolioData>(API_URL.USER_PORTFOLIO(username));

  return response.data;
};
