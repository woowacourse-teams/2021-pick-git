import React from "react";
import ReactDOM from "react-dom";
import { QueryClientProvider, QueryClient } from "react-query";
import { ThemeProvider } from "styled-components";

import App from "./App";
import { theme, GlobalStyle } from "./App.style";
import { UserContextProvider } from "./contexts/UserContext";
import { PostAddStepContextProvider } from "./contexts/PostAddStepContext";
import { SnackBarContextProvider } from "./contexts/SnackbarContext";

const queryClient = new QueryClient();

ReactDOM.render(
  <ThemeProvider theme={theme}>
    <QueryClientProvider client={queryClient}>
      <UserContextProvider>
        <PostAddStepContextProvider>
          <SnackBarContextProvider>
            <GlobalStyle />
            <App />
          </SnackBarContextProvider>
        </PostAddStepContextProvider>
      </UserContextProvider>
    </QueryClientProvider>
  </ThemeProvider>,
  document.querySelector("#root")
);
