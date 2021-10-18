// import React from "react";
// import { renderHook, act } from "@testing-library/react-hooks";
// import { QueryClientProvider } from "react-query";

// import SnackBarContext from "../../contexts/SnackbarContext";
// import UserContext from "../../contexts/UserContext";
// import {
//   createQueryClient,
//   EMPTY_PAGE,
//   keyword,
//   mockFn,
//   setLocalStorageInvalid,
//   setLocalStorageValid,
//   UNAUTHORIZED_TOKEN_ERROR,
//   USERNAME,
// } from "../@mocks/shared";
// import useSearchUserData from "../service/useSearchUserData";
// import {
//   searchUserDataServer,
//   SEARCH_USER_KEYWORD,
//   SEARCH_RESULT_PAGES,
//   EMPTY_RESULT_KEYWORD,
//   SEARCH_RESULT_MAX_PAGE_LENGTH,
// } from "../@mocks/mockSearchUserData";
// import SearchContext from "../../contexts/SearchContext";
// import { API_ERROR_MESSAGE } from "../../constants/messages";

// const queryClient = createQueryClient();

// const wrapper = ({ children }: { children: React.ReactNode }) => {
//   const userContextValue = { isLoggedIn: true, currentUsername: USERNAME, login: () => {}, logout: mockFn.logout };
//   const snackbarContextValue = { pushSnackbarMessage: mockFn.pushSnackbarMessage };
//   const searchDataContextValue = { keyword: keyword.current, onKeywordChange: keyword.change };

//   return (
//     <QueryClientProvider client={queryClient}>
//       <UserContext.Provider value={userContextValue}>
//         <SnackBarContext.Provider value={snackbarContextValue}>
//           <SearchContext.Provider value={searchDataContextValue}>{children}</SearchContext.Provider>
//         </SnackBarContext.Provider>
//       </UserContext.Provider>
//     </QueryClientProvider>
//   );
// };

// const setupHook = () => renderHook(() => useSearchUserData(true), { wrapper });

// beforeAll(() => {
//   searchUserDataServer.listen();
// });

// afterAll(() => {
//   searchUserDataServer.close();
// });

// describe("Success Case", () => {
//   beforeAll(() => {
//     keyword.change(SEARCH_USER_KEYWORD);
//   });

//   test("success1: should load search result", async () => {
//     const { result, waitFor } = setupHook();

//     await waitFor(() => !result.current.isLoading);

//     expect(result.current.results).toStrictEqual(SEARCH_RESULT_PAGES[0]);
//   });

//   test("success2: should load next page", async () => {
//     const { result, waitFor } = setupHook();

//     await waitFor(() => !result.current.isLoading);
//     await act(async () => await result.current.handleIntersect());
//     await waitFor(() => !result.current.isFetchingNextPage);

//     expect(result.current.results).toStrictEqual([...SEARCH_RESULT_PAGES[0], ...SEARCH_RESULT_PAGES[1]]);
//   });
// });

// describe("Failure Case", () => {
//   test("failure1: should handle empty data case", async () => {
//     keyword.change(EMPTY_RESULT_KEYWORD);

//     const { result, waitFor } = setupHook();

//     await waitFor(() => !result.current.isLoading);

//     expect(result.current.results).toStrictEqual(EMPTY_PAGE[0]);

//     keyword.change(SEARCH_USER_KEYWORD);
//   });

//   test("failure2: should handle no extra page case", async () => {
//     const { result, waitFor } = setupHook();

//     await waitFor(() => !result.current.isLoading);

//     for (let i = 1; i < SEARCH_RESULT_MAX_PAGE_LENGTH; i++) {
//       await act(async () => await result.current.handleIntersect());
//       await waitFor(() => !result.current.isFetchingNextPage);
//     }

//     const allFetchedResults = result.current.results;

//     await act(async () => await result.current.handleIntersect());
//     await waitFor(() => !result.current.isFetchingNextPage);

//     expect(result.current.results).toStrictEqual(allFetchedResults);
//   });

//   test("failure3: should handle empty keyword case", async () => {
//     keyword.change("");

//     const { result, waitFor } = setupHook();

//     await waitFor(() => !result.current.isLoading);

//     expect(result.current.results).toStrictEqual([]);

//     keyword.change(SEARCH_USER_KEYWORD);
//   });

//   test("failure4: should handle http error: 401", async () => {
//     setLocalStorageInvalid();

//     const { result, waitFor } = setupHook();

//     await waitFor(() => !result.current.isLoading);

//     expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);
//     expect(result.current.results).toStrictEqual([]);

//     setLocalStorageValid();
//   });
// });
