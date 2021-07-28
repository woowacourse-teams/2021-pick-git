import styled from "styled-components";

export const UserList = styled.li`
  display: flex;
  justify-content: space-between;
  align-items: center;

  height: 3rem;
  border-bottom: 1px solid ${({ theme }) => theme.color.borderColor};
`;

export const Empty = styled.div`
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const Button = styled.button<{ follow: boolean }>`
  color: ${({ follow, theme }) => (follow ? theme.color.tertiaryColor : theme.color.primaryColor)};
  font-size: 0.8rem;
  font-weight: bold;
`;

export const NameTag = styled.div`
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
