// import React from "react";
// import { renderHook, act } from "@testing-library/react-hooks";
// import { QueryClientProvider } from "react-query";

// import UserContext from "../../contexts/UserContext";
// import SnackBarContext from "../../contexts/SnackbarContext";
// import SearchContext from "../../contexts/SearchContext";
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
// import useSearchPostData from "../service/useSearchPostData";
// import {
//   searchPostDataServer,
//   SEARCH_POST_KEYWORD,
//   SEARCH_TYPE,
//   SEARCH_RESULT_PAGES,
//   EMPTY_RESULT_KEYWORD,
//   SEARCH_RESULT_MAX_PAGE_LENGTH,
// } from "../@mocks/mockSearchPostData";
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

// const setupHook = (searchType: string | null) => renderHook(() => useSearchPostData(searchType), { wrapper });

// beforeAll(() => {
//   searchPostDataServer.listen();
// });

// afterAll(() => {
//   searchPostDataServer.close();
// });

// describe("Success Case", () => {
//   beforeAll(() => {
//     keyword.change(SEARCH_POST_KEYWORD);
//   });

//   test("success1: should load search result", async () => {
//     const { result, waitFor } = setupHook(SEARCH_TYPE);

//     await waitFor(() => !result.current.isLoading);

//     expect(result.current.infinitePostsData?.pages).toStrictEqual(SEARCH_RESULT_PAGES.slice(0, 1));
//   });

//   test("success2: should load next page", async () => {
//     const { result, waitFor } = setupHook(SEARCH_TYPE);

//     await waitFor(() => !result.current.isLoading);
//     await act(async () => await result.current.handleIntersect());
//     await waitFor(() => !result.current.isFetchingNextPage);

//     expect(result.current.infinitePostsData?.pages).toStrictEqual(SEARCH_RESULT_PAGES.slice(0, 2));
//   });

//   test("success3: should format keyword", async () => {
//     const formatTester = async (input: string, expected: string) => {
//       keyword.change(input);
//       const { result, waitFor } = setupHook(SEARCH_TYPE);

//       await waitFor(() => !result.current.isLoading);
//       expect(result.current.formattedKeyword).toBe(expected);
//     };

//     const formatTestTable = [
//       { input: "   tanney     java ", expected: "tanney java" },
//       { input: "java, javascript", expected: "java javascript" },
//       { input: "java, , html", expected: "java html" },
//     ];

//     formatTestTable.forEach(async ({ input, expected }) => {
//       await formatTester(input, expected);
//     });
//   });
// });

// describe("Failure Case", () => {
//   test("failure1: should handle empty data case", async () => {
//     keyword.change(EMPTY_RESULT_KEYWORD);

//     const { result, waitFor } = setupHook(SEARCH_TYPE);

//     await waitFor(() => !result.current.isLoading);

//     expect(result.current.infinitePostsData?.pages).toStrictEqual(EMPTY_PAGE);

//     keyword.change(SEARCH_POST_KEYWORD);
//   });

//   test("failure2: should handle no extra page case", async () => {
//     const { result, waitFor } = setupHook(SEARCH_TYPE);

//     await waitFor(() => !result.current.isLoading);

//     for (let i = 1; i < SEARCH_RESULT_MAX_PAGE_LENGTH; i++) {
//       await act(async () => await result.current.handleIntersect());
//       await waitFor(() => !result.current.isFetchingNextPage);
//     }

//     const allFetchedResults = result.current.infinitePostsData;

//     await act(async () => await result.current.handleIntersect());
//     await waitFor(() => !result.current.isFetchingNextPage);

//     expect(result.current.infinitePostsData?.pages.filter((page) => page?.length)).toStrictEqual(
//       allFetchedResults?.pages
//     );
//   });

//   test("failure3: should handle empty type case", async () => {
//     const { result, waitFor } = setupHook(null);

//     await waitFor(() => !result.current.isLoading);

//     expect(result.current.infinitePostsData?.pages).toStrictEqual([null]);
//   });

//   test("failure4: should handle empty keyword case", async () => {
//     keyword.change("");

//     const { result, waitFor } = setupHook(SEARCH_TYPE);

//     await waitFor(() => !result.current.isLoading);

//     expect(result.current.infinitePostsData?.pages).toStrictEqual([null]);

//     keyword.change(SEARCH_POST_KEYWORD);
//   });

//   test("failure5: should handle http error: 401", async () => {
//     setLocalStorageInvalid();

//     const { result, waitFor } = setupHook(SEARCH_TYPE);

//     await waitFor(() => !result.current.isLoading);

//     expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);

//     setLocalStorageValid();
//   });
// });
