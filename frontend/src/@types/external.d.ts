declare module "https://developers.kakao.com/sdk/js/kakao.min.js";

declare namespace Kakao {
  function init(apiKey: string): void;
  const Link: Object & {
    sendDefault(templateObj: Object): void;
  };
}
