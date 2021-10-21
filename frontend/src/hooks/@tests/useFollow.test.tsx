import React from "react";
import { QueryClientProvider } from "react-query";
import { renderHook, act, WaitFor, RenderResult } from "@testing-library/react-hooks";

import { ProfileData } from "../../@types";
import { QUERY } from "../../constants/queries";
import useFollow from "../service/useFollow";
import UserContext from "../../contexts/UserContext";
import SnackBarContext from "../../contexts/SnackbarContext";
import { API_ERROR_MESSAGE, CLIENT_ERROR_MESSAGE } from "../../constants/messages";
import {
  followServer,
  mockQuerySetter,
  PREV_FOLLOWER_COUNT,
  PREV_FOLLOWING,
  TARGET_USERNAME,
} from "../@mocks/mockFollow";
import {
  createQueryClient,
  DEFAULT_PROFILE_QUERY_DATA,
  mockFn,
  setLocalStorageEmpty,
  setLocalStorageInvalid,
  setLocalStorageValid,
  UNAUTHORIZED_TOKEN_ERROR,
} from "../@mocks/shared";

const queryClient = createQueryClient();
const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: false, username: TARGET_USERNAME }];

const wrapper = ({ children }: { children: React.ReactNode }) => {
  const userContextValue = { isLoggedIn: true, currentUsername: "beuccol", login: () => {}, logout: mockFn.logout };
  const snackbarContextValue = { pushSnackbarMessage: mockFn.pushSnackbarMessage };

  queryClient.setQueryData<ProfileData>(currentProfileQueryKey, {
    ...DEFAULT_PROFILE_QUERY_DATA,
    followerCount: PREV_FOLLOWER_COUNT,
    following: PREV_FOLLOWING,
  });

  return (
    <QueryClientProvider client={queryClient}>
      <UserContext.Provider value={userContextValue}>
        <SnackBarContext.Provider value={snackbarContextValue}>{children}</SnackBarContext.Provider>
      </UserContext.Provider>
    </QueryClientProvider>
  );
};

const actions = {
  follow: async (username: string, result: RenderResult<ReturnType<typeof useFollow>>, waitFor: WaitFor) => {
    await result.current.toggleFollow(username, false, false);
    await waitFor(() => !result.current.isLoading);
  },
  unFollow: async (username: string, result: RenderResult<ReturnType<typeof useFollow>>, waitFor: WaitFor) => {
    await result.current.toggleFollow(username, true, false);
    await waitFor(() => !result.current.isLoading);
  },
};

beforeAll(() => {
  setLocalStorageValid();
  followServer.listen();
});

afterAll(() => {
  followServer.close();
});

describe("Success Case", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("success1: should add Follow", async () => {
    const { result, waitFor } = renderHook(() => useFollow(mockQuerySetter), { wrapper });

    await act(() => actions.follow(TARGET_USERNAME, result, waitFor));

    expect(mockQuerySetter.mock.calls[0][0]).toBe(true);
  });

  test("success2: should add Unfollow", async () => {
    const { result, waitFor } = renderHook(() => useFollow(mockQuerySetter), { wrapper });

    await act(() => actions.unFollow(TARGET_USERNAME, result, waitFor));

    expect(mockQuerySetter.mock.calls[0][0]).toBe(false);
  });
});

describe("FAILURE CASE", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("failure1: should handle empty accessToken error while one follow", async () => {
    setLocalStorageEmpty();

    const { result, waitFor } = renderHook(() => useFollow(mockQuerySetter), { wrapper });

    await act(() => actions.follow(TARGET_USERNAME, result, waitFor));

    expect(mockQuerySetter.mock.calls[0][0]).toBe(true);
    expect(mockQuerySetter.mock.calls[1][0]).toBe(false);
    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(CLIENT_ERROR_MESSAGE.C0001);
    expect(mockFn.logout.mock.calls.length).toBe(1);

    setLocalStorageValid();
  });

  test("failure2: should handle empty accessToken error while one unfollow", async () => {
    setLocalStorageEmpty();

    const { result, waitFor } = renderHook(() => useFollow(mockQuerySetter), { wrapper });

    await act(() => actions.unFollow(TARGET_USERNAME, result, waitFor));

    expect(mockQuerySetter.mock.calls[0][0]).toBe(false);
    expect(mockQuerySetter.mock.calls[1][0]).toBe(true);
    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(CLIENT_ERROR_MESSAGE.C0001);
    expect(mockFn.logout.mock.calls.length).toBe(1);

    setLocalStorageValid();
  });

  test("failure3: should handle http error while one follow: 401", async () => {
    setLocalStorageInvalid();

    const { result, waitFor } = renderHook(() => useFollow(mockQuerySetter), { wrapper });

    await act(() => actions.follow(TARGET_USERNAME, result, waitFor));

    expect(mockQuerySetter.mock.calls[0][0]).toBe(true);
    expect(mockQuerySetter.mock.calls[1][0]).toBe(false);
    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);
    expect(mockFn.logout.mock.calls.length).toBe(1);

    setLocalStorageValid();
  });

  test("failure4: should handle http error while one unfollow: 401", async () => {
    setLocalStorageInvalid();

    const { result, waitFor } = renderHook(() => useFollow(mockQuerySetter), { wrapper });

    await act(() => actions.unFollow(TARGET_USERNAME, result, waitFor));

    expect(mockQuerySetter.mock.calls[0][0]).toBe(false);
    expect(mockQuerySetter.mock.calls[1][0]).toBe(true);
    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);
    expect(mockFn.logout.mock.calls.length).toBe(1);

    setLocalStorageValid();
  });
});
