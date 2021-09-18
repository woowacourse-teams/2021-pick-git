import { usePortfolioQuery, useSetPortfolioMutation } from "../queries/portfolio";

const usePortfolio = (username: string) => {
  const { data, isError, isLoading } = usePortfolioQuery(username);
  const { mutateAsync: mutateSetPortfolio } = useSetPortfolioMutation(username);

  return {
    portfolio: data ?? null,
    isError,
    isLoading,
    mutateSetPortfolio,
  };
};

export default usePortfolio;
