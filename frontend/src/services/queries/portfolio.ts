import { AxiosError } from "axios";
import { useMutation, useQuery } from "react-query";
import { ErrorResponse, PortfolioData, PortfolioUploadData } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { customError } from "../../utils/error";
import { requestGetPortfolio, requestGetMyPortfolio, requestSetPortfolio } from "../requests/portfolio";

export const usePortfolioQuery = (username: string, isMyPortfolio: boolean) => {
  const portfolioQueryFunction = async () => {
    const accessToken = getAccessToken();

    if (isMyPortfolio) {
      if (!accessToken) throw customError.noAccessToken;

      return await requestGetMyPortfolio(username, accessToken);
    }

    return await requestGetPortfolio(username);
  };

  return useQuery<PortfolioData, AxiosError<ErrorResponse>>([QUERY.GET_PORTFOLIO], portfolioQueryFunction, {
    refetchOnWindowFocus: false,
    cacheTime: 0
  });
};

export const useSetPortfolioMutation = () => {
  return useMutation((portfolio: PortfolioUploadData) => requestSetPortfolio(portfolio, getAccessToken()));
};
