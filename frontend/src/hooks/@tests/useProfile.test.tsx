import React from "react";
import { renderHook } from "@testing-library/react-hooks";
import { QueryClientProvider } from "react-query";

import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { mockHistoryPush, MY_PROFILE_DATA, profileServer, USER_PROFILE_DATA } from "../@mocks/mockProfile";
import {
  createQueryClient,
  mockFn,
  setLocalStorageInvalid,
  setLocalStorageValid,
  UNAUTHORIZED_TOKEN_ERROR,
  USERNAME,
} from "../@mocks/shared";
import useProfile from "../service/useProfile";
import { API_ERROR_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";
import { QUERY } from "../../constants/queries";

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

const setupHook = (isMyProfile: boolean, username: string) =>
  renderHook(() => useProfile(isMyProfile, username), { wrapper });

beforeAll(() => {
  profileServer.listen();
});

afterAll(() => {
  profileServer.close();
});

describe("Success Case", () => {
  beforeAll(() => {
    setLocalStorageValid();
  });

  test("success1: should load my data", async () => {
    const { result, waitFor } = setupHook(true, USERNAME);

    await waitFor(() => !result.current.isLoading);

    expect(result.current.data).toStrictEqual(MY_PROFILE_DATA);
  });

  test("success2: should load user data", async () => {
    const { result, waitFor } = setupHook(false, "chris");

    await waitFor(() => !result.current.isLoading);

    expect(result.current.data).toStrictEqual(USER_PROFILE_DATA);
  });
});

describe("Failure Case", () => {
  beforeAll(() => {
    setLocalStorageInvalid();
  });

  beforeEach(() => {
    queryClient.removeQueries([QUERY.GET_PROFILE, { isMyProfile: true, username: USERNAME }]);
    queryClient.removeQueries([QUERY.GET_PROFILE, { isMyProfile: false, username: "chris" }]);
    jest.clearAllMocks();
  });

  test("failure1: should handle http error in my profile page: 401", async () => {
    const { result, waitFor } = setupHook(true, USERNAME);

    await waitFor(() => !result.current.isLoading);

    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);
    expect(mockHistoryPush.mock.calls[0][0]).toBe(PAGE_URL.HOME);
  });

  test("failure2: should handle http error in user profile page: 401", async () => {
    const { result, waitFor } = setupHook(false, "chris");

    await waitFor(() => !result.current.isLoading);

    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);
    expect(mockFn.logout.mock.calls.length).toBe(1);
  });
});
