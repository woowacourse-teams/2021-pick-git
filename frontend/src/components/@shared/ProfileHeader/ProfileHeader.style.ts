import styled from "styled-components";
import Button from "../Button/Button";
import { Spinner } from "../Loader/Loader.style";

export const Container = styled.div`
  display: flex;
  justify-content: space-between;
`;

export const Indicators = styled.div`
  display: flex;
  justify-content: space-between;

  width: 13.25rem;
  margin-bottom: 0.8125rem;
`;

export const ButtonLoader = styled(Button)`
  opacity: 0.6;
  position: relative;
`;

export const ButtonSpinner = styled(Spinner)`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
`;
