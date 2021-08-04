import React from "react";
import { renderHook, act } from "@testing-library/react-hooks";
import nock from "nock";
import { QueryClient, QueryClientProvider, useQueryClient } from "react-query";
import { ProfileData } from "../../../@types";
import { QUERY } from "../../../constants/queries";
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

const pushSnackbarMessage = jest.fn();
const logout = jest.fn();

const queryClient = new QueryClient();
const wrapper = ({ children }: { children: React.ReactNode }) => {
  const QueryClientSetting = () => {
    const queryClient = useQueryClient();
    queryClient.setQueryData<ProfileData>(currentProfileQueryKey, prevQueryData);

    return <></>;
  };

  const userContextValue = { isLoggedIn: true, currentUsername: username, login: () => {}, logout };
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

describe("Success Case", () => {});
