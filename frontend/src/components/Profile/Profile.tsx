import axios, { AxiosError } from "axios";
import { useContext } from "react";
import { useHistory } from "react-router-dom";

import { CompanyIcon, GithubDarkIcon, LocationIcon, WebsiteLinkIcon, TwitterIcon } from "../../assets/icons";
import { PAGE_URL } from "../../constants/urls";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { useProfileQuery } from "../../services/queries";
import PageLoading from "../@layout/PageLoading/PageLoading";
import ProfileHeader from "../@shared/ProfileHeader/ProfileHeader";
import { Container, Description, DetailInfo } from "./Profile.style";

export interface Props {
  isMyProfile: boolean;
  username: string | null;
}

const Profile = ({ isMyProfile, username }: Props) => {
  const history = useHistory();
  const { isLoggedIn, logout } = useContext(UserContext);
  const { pushMessage } = useContext(SnackBarContext);
  const { isLoading, error, data, refetch } = useProfileQuery(isMyProfile, username);

  const handleAxiosError = (error: AxiosError) => {
    const { status } = error.response ?? {};

    if (status === 401) {
      if (isMyProfile) {
        pushMessage("로그인한 사용자만 사용할 수 있는 서비스입니다.");

        history.push(PAGE_URL.HOME);
      } else {
        isLoggedIn && pushMessage("사용자 정보가 유효하지 않아 자동으로 로그아웃합니다.");
        logout();
        refetch();
      }
    }
  };

  if (error) {
    console.error(error);

    if (axios.isAxiosError(error)) {
      handleAxiosError(error);
    } else {
      pushMessage("프로필을 확인할 수 없습니다.");
      history.push(PAGE_URL.HOME);
    }
  }

  if (isLoading) {
    return <PageLoading />;
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
