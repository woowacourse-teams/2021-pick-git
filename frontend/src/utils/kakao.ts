Kakao.init(process.env.KAKAO_API_KEY as string);

export const sendKakaoShareLink = (template: Object) => {
  Kakao.Link.sendDefault(template);
};
