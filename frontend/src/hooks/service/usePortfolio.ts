import { usePortfolioQuery, useSetPortfolioMutation } from "../../services/queries/portfolio";

const usePortfolio = (username: string) => {
  const { data, isError, isLoading, error, isFetching } = usePortfolioQuery(username);
  const { mutateAsync: mutateSetPortfolio } = useSetPortfolioMutation(username);

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
