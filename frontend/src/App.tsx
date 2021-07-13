import { BrowserRouter, Route, Switch } from "react-router-dom";

import { PAGE_URL } from "./constants/urls";
import LoginPage from "./pages/LoginPage/LoginPage";

const App = () => {
  return (
    <BrowserRouter>
      <Switch>
        <Route path={PAGE_URL.LOGIN}>
          <LoginPage />
        </Route>
      </Switch>
    </BrowserRouter>
  );
};

export default App;
