import { CompanyIcon, GithubDarkIcon, LocationIcon, WebsiteLinkIcon, TwitterIcon } from "../../assets/icons";
import useProfile from "../../hooks/service/useProfile";
import ProfileHeader from "../@shared/ProfileHeader/ProfileHeader";
import { Container, Description, DetailInfo } from "./Profile.style";

export interface Props {
  isMyProfile: boolean;
  username: string;
}

const Profile = ({ isMyProfile, username }: Props) => {
  const { data } = useProfile(isMyProfile, username);

  return (
    <Container>
      <ProfileHeader isMyProfile={isMyProfile} profile={data ?? null} username={username} />
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
