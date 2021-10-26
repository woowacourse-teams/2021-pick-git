import { useContext, useEffect, useState } from "react";
import { ThemeContext } from "styled-components";

import { GithubStats } from "../../@types";
import { BookIcon, ClockIcon, IssueIcon, PrIcon, StarIcon } from "../../assets/icons";
import UserContext from "../../contexts/UserContext";
import useGithubStatistics from "../../hooks/service/useGithubStatistics";
import { getImagePreloadPromise } from "../../utils/preloaders";
import PageLoading from "../@layout/PageLoading/PageLoading";
import CircleIcon from "../@shared/CircleIcon/CircleIcon";
import {
  Container,
  ContributionGraphWrapper,
  GithubStatsWrapper,
  Stat,
  Empty,
  StatsWrapper,
  Heading,
  ContributionGraph,
} from "./GithubStatistics.style";

const stats: Stats = {
  stars: { name: "Stars", icon: <StarIcon />, countVariable: "starsCount" },
  commits: { name: "Commits", icon: <ClockIcon />, countVariable: "commitsCount" },
  prs: { name: "PRs", icon: <PrIcon />, countVariable: "prsCount" },
  issues: { name: "Issues", icon: <IssueIcon />, countVariable: "issuesCount" },
  repos: { name: "Repositories", icon: <BookIcon />, countVariable: "reposCount" },
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
  isFocused: boolean;
}

const GithubStatistics = ({ username, githubStatisticQueryResult, isFocused }: Props) => {
  const [isContributionGraphLoading, setIsContributionGraphLoading] = useState(true);
  const { color } = useContext(ThemeContext);
  const { isLoggedIn } = useContext(UserContext);
  const { data, isError, isLoading, isFetching } = githubStatisticQueryResult;
  const contributionGraphUrl = isFocused ? `https://ghchart.rshah.org/${color.primaryColor.slice(1)}/${username}` : "";

  const GithubStats = () => {
    const Content = () => {
      if (isLoading || isFetching) {
        return <PageLoading />;
      }

      if (isError) {
        return <div>Github Stats을 표시할 수 없습니다.</div>;
      }

      return (
        <StatsWrapper>
          {Object.entries(stats).map(([key, content]) => (
            <Stat key={key}>
              <CircleIcon diameter="2.375rem" fontSize="0.625rem" name={content.name}>
                {content.icon}
              </CircleIcon>
              <span>{data?.[content.countVariable] ?? 0}</span>
            </Stat>
          ))}
        </StatsWrapper>
      );
    };

    return (
      <>
        <Heading>Github Stats</Heading>
        <GithubStatsWrapper>
          <Content />
        </GithubStatsWrapper>
      </>
    );
  };

  useEffect(() => {
    if (!contributionGraphUrl) return;

    getImagePreloadPromise(contributionGraphUrl).then(() => setIsContributionGraphLoading(false));
  }, [contributionGraphUrl]);

  if (!isLoggedIn) {
    return <Empty>로그인 후 이용할 수 있는 서비스입니다.</Empty>;
  }

  return (
    <Container>
      <GithubStats />
      <Heading>Contribution Graph</Heading>
      <ContributionGraphWrapper>
        {isContributionGraphLoading ? (
          <PageLoading />
        ) : (
          <ContributionGraph
            src={contributionGraphUrl}
            alt={`${username}의 contribution`}
            onLoad={() => setIsContributionGraphLoading(false)}
          />
        )}
      </ContributionGraphWrapper>
    </Container>
  );
};

export default GithubStatistics;
