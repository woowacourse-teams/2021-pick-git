import { usePortfolioQuery, useSetPortfolioMutation } from "../queries/portfolio";

const usePortfolio = (username: string) => {
  const { data: portfolio, isError, isLoading } = usePortfolioQuery(username);

  const { mutateAsync: mutateSetPortfolio } = useSetPortfolioMutation(username);

  return {
    portfolio,
    isError,
    isLoading,
    mutateSetPortfolio,
  };
};

export default usePortfolio;
