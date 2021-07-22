import { useContext } from "react";
import { ThemeContext } from "styled-components";

import { GithubStats } from "../../@types";
import { BookIcon, ClockIcon, IssueIcon, PrIcon, StarIcon } from "../../assets/icons";
import ProfileContext from "../../contexts/ProfileContext";
import PageLoading from "../@layout/PageLoading/PageLoading";
import CircleIcon from "../@shared/CircleIcon/CircleIcon";
import { Container, ContributionGraphWrapper, GithubStatsWrapper, Stat } from "./GithubStatistics.style";

// TODO: typing
const stats = {
  stars: { name: "Stars", icon: <StarIcon /> },
  commits: { name: "Commits", icon: <ClockIcon /> },
  prs: { name: "PRs", icon: <PrIcon /> },
  issues: { name: "Issues", icon: <IssueIcon /> },
  contributes: { name: "Contributes", icon: <BookIcon /> },
};

const GithubStatistics = () => {
  const { color } = useContext(ThemeContext);
  const { username, githubStatisticsProps } = useContext(ProfileContext) ?? {};
  const { data, isLoading, error } = githubStatisticsProps ?? {};

  if (isLoading) {
    return (
      <div>
        <PageLoading />
      </div>
    );
  }

  const GithubStats = () => {
    if (error) {
      return <div>Github Stats을 표시할 수 없습니다.</div>;
    }

    return (
      <>
        <h2>Github Stats</h2>
        <GithubStatsWrapper>
          {Object.entries(stats).map(([key, content]) => (
            <Stat key={key}>
              <CircleIcon diameter="2.375rem" fontSize="0.625rem" name={content.name}>
                {content.icon}
              </CircleIcon>
              <span>{data?.[key as keyof GithubStats] ?? 0}</span>
            </Stat>
          ))}
        </GithubStatsWrapper>
      </>
    );
  };

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
