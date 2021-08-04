import React from "react";
import { QueryClient, QueryClientProvider, useQueryClient } from "react-query";
import { renderHook, act } from "@testing-library/react-hooks";
import nock from "nock";

import { ProfileData } from "../../../@types";
import { QUERY } from "../../../constants/queries";
import useFollow from "../useFollow";
import UserContext from "../../../contexts/UserContext";
import SnackBarContext from "../../../contexts/SnackbarContext";
import { API_ERROR_MESSAGE, CLIENT_ERROR_MESSAGE, UNKNOWN_ERROR_MESSAGE } from "../../../constants/messages";

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
const unAuthorizedTokenError = "A0001";
const invalidErrorCode = "asdf";

const mocks = {
  pushSnackbarMessage: jest.fn(),
  logout: jest.fn(),
};

const queryClient = new QueryClient();
const wrapper = ({ children }: { children: React.ReactNode }) => {
  const QueryClientSetting = () => {
    const queryClient = useQueryClient();
    queryClient.setQueryData<ProfileData>(currentProfileQueryKey, prevQueryData);

    return <></>;
  };

  const userContextValue = { isLoggedIn: true, currentUsername: "beuccol", login: () => {}, logout: mocks.logout };
  const snackbarContextValue = { pushSnackbarMessage: mocks.pushSnackbarMessage };

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

const expectations: { [K: string]: nock.Scope | null } = {
  addFollowExpectation: null,
  deleteFollowExpectation: null,
};

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

  Object.defineProperties(global, {
    localStorage: {
      value: localStorageMock,
    },
  });
});

afterAll(() => {
  expectations.addFollowExpectation?.done();
  expectations.deleteFollowExpectation?.done();
});

describe("Success Case", () => {
  beforeAll(() => {
    expectations.addFollowExpectation = nock("http://localhost:3000")
      .post(`/api/profiles/${username}/followings`)
      .reply(200, {
        followerCount: addedFollowerCount,
        following: true,
      });

    expectations.deleteFollowExpectation = nock("http://localhost:3000")
      .delete(`/api/profiles/${username}/followings`)
      .reply(200, {
        followerCount: deletedFollowerCount,
        following: false,
      });
  });

  test("success1: should add Follow", async () => {
    const { result, waitFor } = renderHook(() => useFollow(), { wrapper });

    await act(() => result.current.toggleFollow(username, false, false));
    await waitFor(() => !result.current.isFollowLoading);

    const currentQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(currentQueryData?.followerCount).toBe(addedFollowerCount);
    expect(currentQueryData?.following).toBe(true);
  });

  test("success2: should add Unfollow", async () => {
    const { result, waitFor } = renderHook(() => useFollow(), { wrapper });

    await act(() => result.current.toggleFollow(username, true, false));
    await waitFor(() => !result.current.isFollowLoading);

    const currentQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(currentQueryData?.followerCount).toBe(deletedFollowerCount);
    expect(currentQueryData?.following).toBe(false);
  });
});

describe("FAILURE CASE", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("failure1: should handle empty accessToken error", async () => {
    localStorage.setItem("accessToken", "");

    const { result, waitFor } = renderHook(() => useFollow(), { wrapper });

    await act(() => result.current.toggleFollow(username, false, false));
    await waitFor(() => !result.current.isFollowLoading);

    const currentQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(currentQueryData?.followerCount).toBe(prevQueryData.followerCount);
    expect(currentQueryData?.following).toBe(prevQueryData.following);
    expect(mocks.pushSnackbarMessage.mock.calls[0][0]).toBe(CLIENT_ERROR_MESSAGE.C0001);
    expect(mocks.logout.mock.calls.length).toBe(1);

    localStorage.setItem("accessToken", "asdfasdf");
  });

  test("failure2: should handle http error: 401", async () => {
    const { result, waitFor } = renderHook(() => useFollow(), { wrapper });

    expectations.addFollowExpectation = nock("http://localhost:3000")
      .post(`/api/profiles/${username}/followings`)
      .reply(401, {
        errorCode: unAuthorizedTokenError,
      });

    await act(() => result.current.toggleFollow(username, false, false));
    await waitFor(() => !result.current.isFollowLoading);

    const currentQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(currentQueryData?.followerCount).toBe(prevQueryData.followerCount);
    expect(currentQueryData?.following).toBe(prevQueryData.following);
    expect(mocks.logout.mock.calls.length).toBe(1);
  });

  test("failure3: should show http error message", async () => {
    const { result, waitFor } = renderHook(() => useFollow(), { wrapper });

    expectations.addFollowExpectation = nock("http://localhost:3000")
      .post(`/api/profiles/${username}/followings`)
      .reply(401, {
        errorCode: unAuthorizedTokenError,
      });

    await act(() => result.current.toggleFollow(username, false, false));
    await waitFor(() => !result.current.isFollowLoading);

    expect(mocks.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[unAuthorizedTokenError]);
  });

  test("failure4: should show unknown error message for invalid error code", async () => {
    const { result, waitFor } = renderHook(() => useFollow(), { wrapper });

    expectations.addFollowExpectation = nock("http://localhost:3000")
      .post(`/api/profiles/${username}/followings`)
      .reply(401, {
        errorCode: invalidErrorCode,
      });

    await act(() => result.current.toggleFollow(username, false, false));
    await waitFor(() => !result.current.isFollowLoading);

    expect(mocks.pushSnackbarMessage.mock.calls[0][0]).toBe(UNKNOWN_ERROR_MESSAGE);
  });
});
