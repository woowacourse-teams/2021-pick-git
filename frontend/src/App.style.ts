import { createGlobalStyle } from "styled-components";
import normalize from "styled-normalize";
import backgroundImagePNG from "./assets/images/app-background.png";

export const theme = {
  color: {
    appBackgroundColor: "#ffffff",
    primaryColor: "#fc3465",
    darkerSecondaryColor: "#ffdede",
    secondaryColor: "#fff0f0",
    tertiaryColor: "#6d6d6d",
    textColor: "#3d3d3d",
    lighterTextColor: "#959595",
    postBackgroundColor: "#f2f2f2",
    borderColor: "#cfcfcf",
    darkBorderColor: "#9f9f9f",
    tagItemColor: "#ffc1c1",
    separatorColor: "#a4a4a45b",
    yellow: "#ffe812",
    white: "#ffffff",
  },
};

export const GlobalStyle = createGlobalStyle`
  ${normalize}

  html,
  body,
  #root {
    margin: 0;
    padding: 0;
    height: 100%;
    font-size: 16px;
    font-family: 'Noto Sans KR', sans-serif;
  }

  body {
    background-image: url(${backgroundImagePNG});
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
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
    margin: 0;
  }

  input[type='number']::-webkit-outer-spin-button,
  input[type='number']::-webkit-inner-spin-button {
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
  }

  textarea {
    resize: none;
  }

  a {
    font-weight: bold;
    font-size: 2rem;
    display: block;
    margin: 20px;
    all: unset;
    color: #3f464d;
  }
  
  a:link {
    text-decoration: none;
  }
  a:visited {
    text-decoration: none;
  }
  a:active {
    text-decoration: none;
  }
  a:hover {
    text-decoration: none;
    cursor: pointer;
  }

  a, button {
    -webkit-tap-highlight-color: transparent;
  }

  * {
    box-sizing: border-box;
  }
`;
