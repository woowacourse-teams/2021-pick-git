import { useContext } from "react";
import { useParams } from "react-router-dom";

import Tabs from "../../components/@shared/Tabs/Tabs";
import GithubStatistics from "../../components/GithubStatistics/GithubStatistics";
import Profile from "../../components/Profile/Profile";
import ProfileFeed from "../../components/ProfileFeed/ProfileFeed";
import { URL_PARAMS } from "../../constants/urls";
import UserContext from "../../contexts/UserContext";
import { Container } from "./ProfilePage.style";

interface Params {
  userType: typeof URL_PARAMS[keyof typeof URL_PARAMS];
  userName: string;
}

const ProfilePage = () => {
  const { userType, userName } = useParams<Params>();
  const isMyProfile = userType === URL_PARAMS.ME;
  const { currentUserName } = useContext(UserContext);

  const tabItems = [
    {
      name: "게시물",
      content: <ProfileFeed isMyFeed={isMyProfile} userName={userName} />,
    },
    {
      name: "활동통계",
      content: <GithubStatistics userName={isMyProfile ? currentUserName : userName} />,
    },
  ];

  return (
    <Container>
      <Profile isMyProfile={isMyProfile} userName={userName} />
      <Tabs tabItems={tabItems} />
    </Container>
  );
};

export default ProfilePage;
