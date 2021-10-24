import React from "react";
import { renderHook } from "@testing-library/react-hooks";
import { QueryClientProvider } from "react-query";

import { API_ERROR_MESSAGE } from "../../constants/messages";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { githubStatisticsServer, GITHUB_STATS } from "../@mocks/mockGithubStatistics";
import {
  createQueryClient,
  mockFn,
  setLocalStorageEmpty,
  setLocalStorageInvalid,
  setLocalStorageValid,
  UNAUTHORIZED_TOKEN_ERROR,
  USERNAME,
} from "../@mocks/shared";
import useGithubStatistics from "../service/useGithubStatistics";

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

const setupHook = (username: string) => renderHook(() => useGithubStatistics(username, true), { wrapper });

beforeAll(() => {
  githubStatisticsServer.listen();
});

afterAll(() => {
  githubStatisticsServer.close();
});

describe("Success Case", () => {
  beforeAll(() => {
    setLocalStorageValid();
  });

  test("success1: should load data", async () => {
    const { result, waitFor } = setupHook(USERNAME);

    await waitFor(() => !result.current.isLoading);

    expect(result.current.data).toStrictEqual(GITHUB_STATS);
  });
});

describe("Failure Case", () => {
  test("failure1: should handle case that one didn't login", async () => {
    setLocalStorageEmpty();

    const { result, waitFor } = setupHook(USERNAME);

    await waitFor(() => !result.current.isLoading);

    expect(result.current.data).toStrictEqual(null);
  });

  test("failure2: should handle http error: 401", async () => {
    setLocalStorageInvalid();

    const { result, waitFor } = setupHook(USERNAME);

    await waitFor(() => result.current.isError);

    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);
    expect(mockFn.logout.mock.calls.length).toBe(1);
  });
});
