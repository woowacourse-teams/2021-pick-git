import { useSelfProfileQuery } from "../../services/queries";

const Profile = () => {
  const { isLoading, error, data } = useSelfProfileQuery();

  if (error) {
    alert("프로필 데이터를 가져오는데 실패하였습니다!");

    if (data) {
      data.userName = "이용자";
      data.tmi = "프로필 데이터를 가져오는데 실패하였습니다";
    }
  }

  if (isLoading) {
    return <div>loading</div>;
  }

  return <div>{JSON.stringify(data)}</div>;
};

export default Profile;
