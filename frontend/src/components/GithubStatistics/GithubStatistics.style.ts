import styled from "styled-components";

export const Container = styled.div`
  padding: 1.2rem 1.5625rem;
  background-color: ${({ theme }) => theme.color.white};

  h2 {
    font-size: 1rem;
    font-weight: bold;
    color: ${({ theme }) => theme.color.textColor};
  }
`;

export const GithubStatsWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: 3rem;
`;

export const Stat = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 0.625rem;
`;

export const ContributionGraphWrapper = styled.div`
  width: 100%;
  overflow-x: auto;
`;

export const Empty = styled.div`
  width: 100%;
  height: 23.5625rem;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;
