import { AxiosError } from "axios";
import { useMutation, useQuery } from "react-query";
import { ErrorResponse, PortfolioData } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetPortfolio, requestSetPortfolio } from "../requests/portfolio";

export const usePortfolioQuery = (username: string) => {
  const portfolioQueryFunction = async () => {
    return await requestGetPortfolio(username);
  };

  return useQuery<PortfolioData, AxiosError<ErrorResponse>>([QUERY.GET_PORTFOLIO], portfolioQueryFunction, {
    refetchOnWindowFocus: false,
  });
};

export const useSetPortfolioMutation = (username: string) => {
  return useMutation((portfolio: PortfolioData) => requestSetPortfolio(username, portfolio, getAccessToken()));
};
