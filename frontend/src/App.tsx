import { BrowserRouter, Route, Switch } from "react-router-dom";
import { ThemeProvider } from "styled-components";

import { theme, GlobalStyle } from "./App.style";
import { PAGE_URL } from "./constants/urls";
import LoginPage from "./pages/LoginPage/LoginPage";

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <BrowserRouter>
        <Switch>
          <Route path={PAGE_URL.LOGIN}>
            <LoginPage />
          </Route>
        </Switch>
      </BrowserRouter>
    </ThemeProvider>
  );
};

export default App;
