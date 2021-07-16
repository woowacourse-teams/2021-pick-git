import React from "react";
import ReactDOM from "react-dom";
import { QueryClientProvider, QueryClient } from "react-query";
import { ThemeProvider } from "styled-components";

import App from "./App";
import { theme, GlobalStyle } from "./App.style";
import { UserContextProvider } from "./contexts/UserContext";

const queryClient = new QueryClient();

ReactDOM.render(
  <ThemeProvider theme={theme}>
    <QueryClientProvider client={queryClient}>
      <UserContextProvider>
        <GlobalStyle />
        <App />
      </UserContextProvider>
    </QueryClientProvider>
  </ThemeProvider>,
  document.querySelector("#root")
);
