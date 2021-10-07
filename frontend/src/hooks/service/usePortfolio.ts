import { usePortfolioQuery, useSetPortfolioMutation } from "../services/queries/portfolio";

const usePortfolio = (username: string) => {
  const { data, isError, isLoading, error } = usePortfolioQuery(username);
  const { mutateAsync: mutateSetPortfolio } = useSetPortfolioMutation(username);

  return {
    portfolio: data ?? null,
    isError,
    isLoading,
    error,
    mutateSetPortfolio,
  };
};

export default usePortfolio;
