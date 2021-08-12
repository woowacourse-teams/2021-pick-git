import { Link } from "react-router-dom";
import styled from "styled-components";
import { Spinner } from "../@shared/Loader/Loader.style";

export const List = styled.li`
  display: flex;
  justify-content: space-between;
  align-items: center;

  height: 3rem;
  border-bottom: 1px solid ${({ theme }) => theme.color.borderColor};
`;

export const Empty = styled.div`
  width: 100%;
  height: 100vh;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

export const Button = styled.button<{ follow: boolean }>`
  color: ${({ follow, theme }) => (follow ? theme.color.tertiaryColor : theme.color.primaryColor)};
  font-size: 0.8rem;
  font-weight: bold;
`;

export const NameTag = styled(Link)`
  display: flex;
  align-items: center;

  span {
    font-size: 1rem;
    font-weight: bold;
    line-height: 0.9;
    display: inline-block;
    margin-left: 1rem;
  }
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
