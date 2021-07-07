import { createGlobalStyle } from "styled-components";

export const theme = {
  color: {
    appBackgroundColor: "#ffffff",
    primaryColor: "#fc3465",
    secondaryColor: "#fff0f0",
    teriaryColor: "#6d6d6d",
    textColor: "#3d3d3d",
    tagItemColor: "#ffc1c1",
    white: "#ffffff",
  },
};

export const GlobalStyle = createGlobalStyle`
  html,
  body,
  ul {
    margin: 0;
    height: 100%;
    font-size: 16px;
    padding: 0;
    font-family: 'Noto Sans KR', sans-serif;
  }
   
  svg {
    display: block;
  }

  button {
    border: none;
    background-color: transparent;
    outline: none;
    padding: 0px;
    cursor: pointer;
  }

  ul {
    list-style: none;
    padding: 0;
  }

  input[type='number']::-webkit-outer-spin-button,
  input[type='number']::-webkit-inner-spin-button {
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
  }

  a {
    font-weight: bold;
    font-size: 2rem;
    display: block;
    margin: 20px;
  }
  a {
    all: unset;
  }
  a:link {
    text-decoration: none;
    color: #3f464d;
  }
  a:visited {
    text-decoration: none;
    color: #3f464d;
  }
  a:active {
    text-decoration: none;
    color: #3f464d;
  }
  a:hover {
    text-decoration: none;
    color: tomato;
    cursor: pointer;
  }

  * {
    box-sizing: border-box;
  }
`;
