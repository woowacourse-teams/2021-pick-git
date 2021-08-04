import React from "react";
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

const queryClient = new QueryClient();

ReactDOM.render(
  <ThemeProvider theme={theme}>
    <QueryClientProvider client={queryClient}>
      <UserContextProvider>
        <PostAddStepContextProvider>
          <SearchContextProvider>
            <PostEditDataContextProvider>
              <SnackBarContextProvider>
                <GlobalStyle />
                <App />
              </SnackBarContextProvider>
            </PostEditDataContextProvider>
          </SearchContextProvider>
        </PostAddStepContextProvider>
      </UserContextProvider>
    </QueryClientProvider>
  </ThemeProvider>,
  document.querySelector("#root")
);
