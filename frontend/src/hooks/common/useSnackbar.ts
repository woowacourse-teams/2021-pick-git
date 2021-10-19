import { useContext } from "react";

import SnackBarContext from "../../contexts/SnackbarContext";

const useSnackbar = () => {
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  return { pushSnackbarMessage };
};

export default useSnackbar;
