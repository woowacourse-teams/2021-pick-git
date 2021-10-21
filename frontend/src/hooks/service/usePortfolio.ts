import { usePortfolioQuery, useSetPortfolioMutation } from "../../services/queries/portfolio";

const usePortfolio = (username: string, isMyPortfolio: boolean = false) => {
  const { data, isError, isLoading, error, isFetching } = usePortfolioQuery(username, isMyPortfolio);
  const { mutateAsync: mutateSetPortfolio } = useSetPortfolioMutation();

  return {
    portfolio: data ?? null,
    isError,
    isLoading,
    error,
    isFetching,
    mutateSetPortfolio,
  };
};

export default usePortfolio;
