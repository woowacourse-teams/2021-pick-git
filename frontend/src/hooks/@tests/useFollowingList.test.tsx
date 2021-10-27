import React from "react";
import { renderHook, act } from "@testing-library/react-hooks";
import { QueryClientProvider } from "react-query";

import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import {
  createQueryClient,
  EMPTY_PAGE,
  mockFn,
  setLocalStorageEmpty,
  setLocalStorageInvalid,
  UNAUTHORIZED_TOKEN_ERROR,
  USERNAME,
} from "../@mocks/shared";
import { API_ERROR_MESSAGE } from "../../constants/messages";
import useFollowingList from "../service/useFollowingList";
import {
  followingListServer,
  USER_WITH_FOLLOWINGS,
  FOLLOWING_LIST_PAGES,
  USER_WITH_NO_FOLLOWINGS,
  FOLLOWING_LIST_MAX_PAGE_LENGTH,
} from "../@mocks/mockFollowingList";

const queryClient = createQueryClient();

const wrapper = ({ children }: { children: React.ReactNode }) => {
  const userContextValue = { isLoggedIn: true, currentUsername: USERNAME, login: () => {}, logout: mockFn.logout };
  const snackbarContextValue = { pushSnackbarMessage: mockFn.pushSnackbarMessage };

  return (
    <QueryClientProvider client={queryClient}>
      <UserContext.Provider value={userContextValue}>
        <SnackBarContext.Provider value={snackbarContextValue}>{children}</SnackBarContext.Provider>
      </UserContext.Provider>
    </QueryClientProvider>
  );
};

const setupHook = (username: string) => renderHook(() => useFollowingList(username), { wrapper });

beforeAll(() => {
  followingListServer.listen();
});

afterAll(() => {
  followingListServer.close();
});

describe("Success Case", () => {
  test("success1: should load results", async () => {
    const { result, waitFor } = setupHook(USER_WITH_FOLLOWINGS);

    await waitFor(() => !result.current.isLoading);

    expect(result.current.results).toStrictEqual(FOLLOWING_LIST_PAGES[0]);
  });

  test("succeess2: should load next page", async () => {
    const { result, waitFor } = setupHook(USER_WITH_FOLLOWINGS);

    await waitFor(() => !result.current.isLoading);
    await act(async () => await result.current.handleIntersect());
    await waitFor(() => !result.current.isFetchingNextPage);

    expect(result.current.results).toStrictEqual([...FOLLOWING_LIST_PAGES[0], ...FOLLOWING_LIST_PAGES[1]]);
  });
});

describe("Failure Case", () => {
  test("failure1: should handle empty data case", async () => {
    const { result, waitFor } = setupHook(USER_WITH_NO_FOLLOWINGS);

    await waitFor(() => !result.current.isLoading);

    expect(result.current.results).toStrictEqual(EMPTY_PAGE[0]);
  });

  test("failure2: should handle no extra page case", async () => {
    const { result, waitFor } = setupHook(USER_WITH_FOLLOWINGS);

    await waitFor(() => !result.current.isLoading);

    for (let i = 1; i < FOLLOWING_LIST_MAX_PAGE_LENGTH; i++) {
      await act(async () => await result.current.handleIntersect());
      await waitFor(() => !result.current.isFetchingNextPage);
    }

    const allFetchedResults = result.current.results;

    await act(async () => await result.current.handleIntersect());
    await waitFor(() => !result.current.isFetchingNextPage);

    expect(result.current.results).toStrictEqual(allFetchedResults);
  });

  // test("failure3: should handle http error: 401", async () => {
  //   setLocalStorageInvalid();

  //   const { result, waitFor } = setupHook(USER_WITH_FOLLOWINGS);

  //   await waitFor(() => !result.current.isLoading);

  //   expect(mockFn.pushSnackbarMessage.mock.calls.length).toBe(1);
  //   expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);
  //   expect(result.current.results).toStrictEqual([]);

  //   setLocalStorageEmpty();
  // });
});
