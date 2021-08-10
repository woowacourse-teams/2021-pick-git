import axios from "axios";
import { Post, UserItem } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { API_URL } from "../../constants/urls";

export const requestGetPostLikePeople = async (postId: Post["id"], pageParam: number, accessToken: string | null) => {
  const response = await axios.get<UserItem[]>(
    API_URL.POST_LIKE_PEOPLE(postId, pageParam, LIMIT.POST_LIKE_PERSON_COUNT_PER_FETCH),
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );

  return response.data;
};
