import React from "react";
import { QueryClient, QueryClientProvider, useQueryClient } from "react-query";
import { renderHook, act } from "@testing-library/react-hooks";
import nock from "nock";

import { ProfileData } from "../../../@types";
import { QUERY } from "../../../constants/queries";
import useFollow from "../useFollow";
import { getAPIErrorMessage } from "../../../utils/error";
import UserContext from "../../../contexts/UserContext";
import SnackBarContext from "../../../contexts/SnackbarContext";

const username = "aaaa";
const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: false, username }];
const prevQueryData = {
  name: username,
  imageUrl: "",
  description: "",
  followerCount: 3,
  followingCount: 2,
  postCount: 0,
  githubUrl: "",
  company: "",
  location: "",
  website: "",
  twitter: "",
  following: false,
};
const addedFollowerCount = prevQueryData.followerCount + 1;
const deletedFollowerCount = prevQueryData.followerCount - 1;

const pushSnackbarMessage = jest.fn();
const logout = jest.fn();

const queryClient = new QueryClient();
const wrapper = ({ children }: { children: React.ReactNode }) => {
  const QueryClientSetting = () => {
    const queryClient = useQueryClient();
    queryClient.setQueryData<ProfileData>(currentProfileQueryKey, prevQueryData);

    return <></>;
  };

  const userContextValue = { isLoggedIn: true, currentUsername: "beuccol", login: () => {}, logout };
  const snackbarContextValue = { pushSnackbarMessage };

  return (
    <QueryClientProvider client={queryClient}>
      <UserContext.Provider value={userContextValue}>
        <SnackBarContext.Provider value={snackbarContextValue}>
          <QueryClientSetting />
          {children}
        </SnackBarContext.Provider>
      </UserContext.Provider>
    </QueryClientProvider>
  );
};

const addFollowExpectation = nock("http://localhost:3000").post(`/api/profiles/${username}/followings`).reply(200, {
  followerCount: addedFollowerCount,
  following: true,
});

const deleteFollowExpectation = nock("http://localhost:3000")
  .delete(`/api/profiles/${username}/followings`)
  .reply(200, {
    followerCount: deletedFollowerCount,
    following: false,
  });

beforeAll(() => {
  const localStorageMock = (() => {
    const store: { [k: string]: string } = {
      username: "chris",
      accessToken: "asdfasdf",
    };

    return {
      getItem: (key: string) => {
        return store[key] || null;
      },
      setItem: (key: string, value: string) => {
        store[key] = value;
      },
    };
  })();

  Object.defineProperty(global, "localStorage", {
    value: localStorageMock,
  });
});

test("should add Follow", async () => {
  const { result, waitFor } = renderHook(() => useFollow(), { wrapper });

  await act(() => result.current.toggleFollow(username, false));
  await waitFor(() => !result.current.isFollowLoading);

  const currentQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

  expect(currentQueryData?.followerCount).toBe(addedFollowerCount);
  expect(currentQueryData?.following).toBe(true);
});

test("should add Unfollow", async () => {
  const { result, waitFor } = renderHook(() => useFollow(), { wrapper });

  await act(() => result.current.toggleFollow(username, true));
  await waitFor(() => !result.current.isFollowLoading);

  const currentQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

  expect(currentQueryData?.followerCount).toBe(deletedFollowerCount);
  expect(currentQueryData?.following).toBe(false);
});

test("should handle empty accessToken error", async () => {
  localStorage.setItem("accessToken", "");

  const { result, waitFor } = renderHook(() => useFollow(), { wrapper });

  await act(() => result.current.toggleFollow(username, false));
  await waitFor(() => !result.current.isFollowLoading);

  const currentQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

  expect(currentQueryData?.followerCount).toBe(prevQueryData.followerCount);
  expect(currentQueryData?.following).toBe(prevQueryData.following);
  expect(pushSnackbarMessage.mock.calls[0][0]).toBe(getAPIErrorMessage("C0001"));
  expect(logout.mock.calls.length).toBe(1);

  localStorage.setItem("accessToken", "asdfasdf");
});

afterAll(() => {
  addFollowExpectation.done();
  deleteFollowExpectation.done();
});
