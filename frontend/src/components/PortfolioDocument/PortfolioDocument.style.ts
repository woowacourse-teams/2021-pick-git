import { StyleSheet, Font } from "@react-pdf/renderer";

import { theme } from "../../App.style";
import fontNanumBarunGothic from "../../assets/font/NanumBarunGothic.ttf";
import fontNanumBarunGothicBold from "../../assets/font/NanumBarunGothicBold.ttf";

Font.register({
  family: "NanumBarunGothic",
  format: "truetype",
  fonts: [
    { src: fontNanumBarunGothic, fontWeight: "normal" },
    { src: fontNanumBarunGothicBold, fontWeight: "bold" },
  ],
});

const styles = {
  global: StyleSheet.create({
    page: {
      fontFamily: "NanumBarunGothic",
      padding: "20 0",
      color: theme.color.textColor,
      lineHeight: 1.1,
    },
    sectionTitle: {
      width: 100,
      marginTop: 25,
      padding: "5 7",
      textAlign: "right",
      fontSize: 15,
      fontWeight: "bold",
      color: theme.color.white,
      backgroundColor: theme.color.tagItemColor,
      borderTopRightRadius: 12,
      borderBottomRightRadius: 12,
    },
  }),
  profile: StyleSheet.create({
    profileContainer: {
      width: "100vw",
      padding: "0 20",
    },
    imageWrapper: {
      width: 80,
      height: 80,
      marginRight: "25",
      borderRadius: 75,
      backgroundColor: "#9f9f9f",
      overflow: "hidden",
    },
    image: {
      minWidth: 80,
      minHeight: 80,
    },
    basic: {
      width: "100%",
      display: "flex",
      flexDirection: "row",
      marginBottom: 20,
    },
    name: {
      fontSize: 15,
      fontWeight: "bold",
      marginTop: 5,
      marginBottom: 10,
    },
    description: {
      fontSize: 10,
    },
    contacts: {
      fontSize: 10,
      lineHeight: 1.4,
    },
  }),
  project: StyleSheet.create({
    header: {
      display: "flex",
      flexDirection: "column",
      alignItems: "flex-end",
    },
    body: {
      width: "100vw",
      padding: "0 20",
      display: "flex",
      flexDirection: "row",
    },
    titleWrapper: {
      width: "100%",
      padding: "0 20 10 20",
      display: "flex",
      flexDirection: "row",
      justifyContent: "space-between",
    },
    type: {
      fontSize: 12,
      fontWeight: "bold",
    },
    title: {
      fontSize: 12,
      fontWeight: "bold",
    },
    content: {
      width: 446,
      fontSize: 10,
    },
    thumbnailWrapper: {
      width: 100,
      height: 100,
      overflow: "hidden",
      display: "flex",
      flexDirection: "row",
      justifyContent: "center",
      alignItems: "center",
    },
    thumbnail: {},
    tagList: {
      width: "100%",
      marginTop: 20,
      display: "flex",
      flexDirection: "row",
      flexWrap: "wrap",
    },
    tag: {
      padding: "4 8",
      marginRight: 5,
      fontSize: 8,
      color: theme.color.white,
      backgroundColor: theme.color.tagItemColor,
      borderRadius: 10,
    },
  }),
  durationView: StyleSheet.create({
    container: {
      width: 230,
      display: "flex",
      flexDirection: "row",
      justifyContent: "space-between",
      fontSize: 10,
      fontWeight: "bold",
      padding: "10 30",
      marginBottom: 10,
      borderBottom: `2 solid ${theme.color.primaryColor}`,
    },
    date: {
      width: 70,
      display: "flex",
      flexDirection: "row",
      justifyContent: "center",
    },
  }),
  section: StyleSheet.create({
    header: {
      display: "flex",
      flexDirection: "column",
      alignItems: "flex-end",
    },
    title: {
      width: 230,
      display: "flex",
      flexDirection: "row",
      justifyContent: "flex-end",
      fontSize: 10,
      fontWeight: "bold",
      padding: "10 30",
      marginBottom: 10,
      borderBottom: `2 solid ${theme.color.primaryColor}`,
    },
    itemList: {
      position: "relative",
      paddingTop: 10,
    },
    verticalLiner: {
      position: "absolute",
      top: 0,
      left: 0,
      bottom: 0,
      width: 150,
      borderRight: `2 solid ${theme.color.secondaryColor}`,
    },
    item: {
      width: "100vw",
      padding: "0 20",
      display: "flex",
      flexDirection: "row",
    },
    category: {
      width: 150,
      paddingRight: 30,
      fontSize: 15,
      fontWeight: "bold",
    },
    descriptionList: {
      width: 446,
      paddingLeft: 30,
      paddingBottom: 20,
    },
    description: {
      fontSize: 12,
      marginBottom: 15,
      color: theme.color.lighterTextColor,
    },
  }),
};

export default styles;
