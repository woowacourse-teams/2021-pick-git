import { useContext } from "react";

import { CompanyIcon, GithubDarkIcon, LocationIcon, WebsiteLinkIcon, TwitterIcon } from "../../assets/icons";
import ProfileContext from "../../contexts/ProfileContext";
import PageLoading from "../@layout/PageLoading/PageLoading";
import ProfileHeader from "../@shared/ProfileHeader/ProfileHeader";
import { Container, Description, DetailInfo } from "./Profile.style";

const Profile = () => {
  const { profileProps } = useContext(ProfileContext) ?? {};
  const { data, isLoading } = profileProps ?? {};

  if (isLoading) {
    return <PageLoading />;
  }

  return (
    <Container>
      <ProfileHeader />
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
