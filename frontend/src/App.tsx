import { BrowserRouter, Route, Switch } from "react-router-dom";
import { ThemeProvider } from "styled-components";

import { theme, GlobalStyle } from "./App.style";

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <BrowserRouter>
        <h1>Hello</h1>
      </BrowserRouter>
    </ThemeProvider>
  );
};

export default App;
