import React from "react";
import ReactDOM from "react-dom";
import { ThemeProvider } from "styled-components";

import App from "./App";
import { theme, GlobalStyle } from "./App.style";
import { UserContextProvider } from "./contexts/UserContext";

ReactDOM.render(
  <ThemeProvider theme={theme}>
    <UserContextProvider>
      <GlobalStyle />
      <App />
    </UserContextProvider>
  </ThemeProvider>,
  document.querySelector("#root")
);
