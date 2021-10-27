import React, { Suspense } from "react";
import ReactDOM from "react-dom";
import { QueryClientProvider, QueryClient } from "react-query";
import { ThemeProvider } from "styled-components";

import App from "./App";
import { theme, GlobalStyle } from "./App.style";
import { UserContextProvider } from "./contexts/UserContext";
import { SnackBarContextProvider } from "./contexts/SnackbarContext";
import { SearchContextProvider } from "./contexts/SearchContext";
import { PostEditDataContextProvider } from "./contexts/PostEditDataContext";
import { PostAddStepContextProvider } from "./contexts/PostAddStepContext";
import { SearchPostContextProvider } from "./contexts/SearchPostContext";
import { UserFeedContextProvider } from "./contexts/UserFeedContext";
import PageLoadingWithLogo from "./components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import { HomeFeedContextProvider } from "./contexts/HomeFeedContext";

const queryClient = new QueryClient();

ReactDOM.render(
  <Suspense fallback={<PageLoadingWithLogo />}>
    <ThemeProvider theme={theme}>
      <QueryClientProvider client={queryClient}>
        <UserContextProvider>
          <PostAddStepContextProvider>
            <SearchContextProvider>
              <PostEditDataContextProvider>
                <SearchPostContextProvider>
                  <HomeFeedContextProvider>
                    <UserFeedContextProvider>
                      <SnackBarContextProvider>
                        <GlobalStyle />
                        <App />
                      </SnackBarContextProvider>
                    </UserFeedContextProvider>
                  </HomeFeedContextProvider>
                </SearchPostContextProvider>
              </PostEditDataContextProvider>
            </SearchContextProvider>
          </PostAddStepContextProvider>
        </UserContextProvider>
      </QueryClientProvider>
    </ThemeProvider>
  </Suspense>,
  document.querySelector("#root")
);
