import { useContext } from "react";
import { ThemeContext } from "styled-components";

import { GithubStats } from "../../@types";
import { BookIcon, ClockIcon, IssueIcon, PrIcon, StarIcon } from "../../assets/icons";
import UserContext from "../../contexts/UserContext";
import useGithubStatistics from "../../services/hooks/useGithubStatistics";
import PageLoading from "../@layout/PageLoading/PageLoading";
import CircleIcon from "../@shared/CircleIcon/CircleIcon";
import { Container, ContributionGraphWrapper, GithubStatsWrapper, Stat, Empty } from "./GithubStatistics.style";

const stats: Stats = {
  stars: { name: "Stars", icon: <StarIcon />, countVariable: "starsCount" },
  commits: { name: "Commits", icon: <ClockIcon />, countVariable: "commitsCount" },
  prs: { name: "PRs", icon: <PrIcon />, countVariable: "prsCount" },
  issues: { name: "Issues", icon: <IssueIcon />, countVariable: "issuesCount" },
  contributes: { name: "Contributes", icon: <BookIcon />, countVariable: "contributesCount" },
};

interface Stats {
  [K: string]: {
    name: string;
    icon: React.ReactElement;
    countVariable: keyof GithubStats;
  };
}

export interface Props {
  username: string | null;
  githubStatisticQueryResult: ReturnType<typeof useGithubStatistics>;
}

const GithubStatistics = ({ username, githubStatisticQueryResult }: Props) => {
  const { color } = useContext(ThemeContext);
  const { isLoggedIn } = useContext(UserContext);
  const { data, isLoading, isError } = githubStatisticQueryResult;

  const GithubStats = () => {
    const Content = () => {
      if (isError) {
        return <div>Github Stats을 표시할 수 없습니다.</div>;
      }

      return (
        <>
          {Object.entries(stats).map(([key, content]) => (
            <Stat key={key}>
              <CircleIcon diameter="2.375rem" fontSize="0.625rem" name={content.name}>
                {content.icon}
              </CircleIcon>
              <span>{data?.[content.countVariable] ?? 0}</span>
            </Stat>
          ))}
        </>
      );
    };

    return (
      <>
        <h2>Github Stats</h2>
        <GithubStatsWrapper>
          <Content />
        </GithubStatsWrapper>
      </>
    );
  };

  if (!isLoggedIn) {
    return <Empty>로그인 후 이용할 수 있는 서비스입니다.</Empty>;
  }

  if (isLoading) {
    return (
      <Empty>
        <PageLoading />
      </Empty>
    );
  }

  return (
    <Container>
      <GithubStats />
      <h2>Contribution Graph</h2>
      <ContributionGraphWrapper>
        <img
          src={`https://ghchart.rshah.org/${color.primaryColor.slice(1)}/${username}`}
          alt={`${username}의 contribution`}
        />
      </ContributionGraphWrapper>
    </Container>
  );
};

export default GithubStatistics;
