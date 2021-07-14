import { useContext } from "react";
import { useHistory } from "react-router-dom";

import { CompanyIcon, GithubDarkIcon, LocationIcon, WebsiteLinkIcon, TwitterIcon } from "../../assets/icons";
import { PAGE_URL } from "../../constants/urls";
import UserContext from "../../contexts/UserContext";
import { useProfileQuery } from "../../services/queries";
import ProfileHeader from "../@shared/ProfileHeader/ProfileHeader";
import { Container, Description, DetailInfo } from "./Profile.style";

export interface Props {
  userName: string;
}

const Profile = ({ userName }: Props) => {
  const history = useHistory();
  const { currentUserName } = useContext(UserContext);

  const isMyProfile = userName === currentUserName;
  const { isLoading, error, data } = useProfileQuery(isMyProfile, userName);

  if (error) {
    console.error(error);
    alert("프로필을 확인할 수 없습니다.");

    history.push(PAGE_URL.HOME);
  }

  if (isLoading) {
    return <div>loading</div>;
  }

  return (
    <Container>
      <ProfileHeader profile={data} isMyProfile={isMyProfile} />
      <Description>{data?.description}</Description>
      <DetailInfo>
        <CompanyIcon />
        {data?.company ? data?.company : "-"}
      </DetailInfo>
      <DetailInfo>
        <LocationIcon />
        {data?.location ? data?.location : "-"}
      </DetailInfo>
      <DetailInfo>
        <GithubDarkIcon />
        <a href={data?.githubUrl ?? ""}>{data?.githubUrl ? data?.githubUrl : "-"}</a>
      </DetailInfo>
      <DetailInfo>
        <WebsiteLinkIcon />
        <a href={data?.website ?? ""}>{data?.website ? data?.website : "-"}</a>
      </DetailInfo>
      <DetailInfo>
        <TwitterIcon />
        {data?.twitter ? data?.twitter : "-"}
      </DetailInfo>
    </Container>
  );
};

export default Profile;
