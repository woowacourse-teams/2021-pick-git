import axios from "axios";
import { Post, UserItem } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestGetPostLikePeople = async (postId: Post["id"], accessToken: string | null) => {
  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};

  const response = await axios.get<UserItem[]>(API_URL.POST_LIKE_PEOPLE(postId), config);

  return response.data;
};
