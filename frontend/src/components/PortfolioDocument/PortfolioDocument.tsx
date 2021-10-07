import ReactPDF, { StyleSheet, Document, Page, View, Text, Image, Font } from "@react-pdf/renderer";
import axios from "axios";
import { useEffect, useRef, useState } from "react";

import { Portfolio, ProfileData } from "../../@types";
import { encodeImageToBase64ByURL } from "../../utils/encode";
import fontNanumGothic from "../../assets/font/NanumGothic.ttf";
import fontNanumGothicBold from "../../assets/font/NanumGothicBold.ttf";
import { theme } from "../../App.style";

interface PortfolioDocumentProps {
  profile: ProfileData | null;
  portfolio: Portfolio;
}

Font.register({
  family: "NanumGothic",
  format: "truetype",
  fonts: [
    { src: fontNanumGothic, fontWeight: "normal" },
    { src: fontNanumGothicBold, fontWeight: "bold" },
  ],
});

const styles = {
  global: StyleSheet.create({
    page: {
      fontFamily: "NanumGothic",
      padding: "20 0",
      color: theme.color.textColor,
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
    thumbnail: {
      width: 100,
      height: 100,
      backgroundColor: "#9f9f9f",
      overflow: "hidden",
    },
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

const PROJECT_TYPE = {
  team: "팀 프로젝트",
  personal: "개인 프로젝트",
} as const;

const ContactItem = ({ item }: { item?: string }) => {
  return item ? (
    <View>
      <Text>{item}</Text>
    </View>
  ) : (
    <View />
  );
};

const DateView = ({ date }: { date: string }) => {
  const [year, month, day] = date.split("-");

  return (
    <Text>
      {year}. {month}. {day}
    </Text>
  );
};

const DurationView = ({ startDate, endDate }: { startDate: string; endDate: string }) => {
  return (
    <View style={styles.durationView.container}>
      <View style={styles.durationView.date}>{startDate ? <DateView date={startDate} /> : <View />}</View>
      <Text>~</Text>
      <View style={styles.durationView.date}>{endDate ? <DateView date={endDate} /> : <View />}</View>
    </View>
  );
};

const Tag = ({ text }: { text: string }) => {
  return (
    <Text style={styles.project.tag} wrap={false}>
      {text}
    </Text>
  );
};

const getImage = async (url: string) => {
  const $img = document.createElement("img");

  return new Promise((resolve, reject) => {
    $img.src = url;
    $img.onload = () => {
      const canvas = document.createElement("canvas");

      // if (image.naturalWidth < image.naturalHeight) {
      //   canvas.width = size;
      //   canvas.height = (size * image.naturalHeight) / image.naturalWidth;
      // } else {
      //   canvas.height = size;
      //   canvas.width = (size * image.naturalWidth) / image.naturalHeight;
      // }

      canvas.getContext("2d")?.drawImage($img, 0, 0);
      resolve(canvas.toDataURL("image/png"));
    };
    $img.onerror = reject;
  });
};

const PortfolioDocument = ({ profile, portfolio }: PortfolioDocumentProps) => {
  // const [profileImageData, setProfileImageData] = useState("");

  // useEffect(() => {
  //   (async () => {
  //     const data = await encodeImageToBase64ByURL(profile?.imageUrl ?? "", 150);

  //     setProfileImageData(data);
  //   })();
  // }, []);

  return (
    <Document>
      <Page style={styles.global.page}>
        <View style={styles.profile.profileContainer}>
          <View style={styles.profile.basic}>
            {portfolio.intro.isProfileShown ? (
              <View style={styles.profile.imageWrapper}>{/* <Image src={profile?.imageUrl} /> */}</View>
            ) : (
              <View style={{ width: 0, height: 0 }} />
            )}
            <View>
              <Text style={styles.profile.name}>{portfolio?.intro?.name ?? ""}</Text>
              <View style={styles.profile.contacts}>
                <ContactItem item={profile?.company} />
                <ContactItem item={profile?.location} />
                <ContactItem item={profile?.githubUrl} />
                <ContactItem item={profile?.website} />
                <ContactItem item={profile?.twitter} />
              </View>
            </View>
          </View>
          <Text style={styles.profile.description}>{portfolio?.intro?.description ?? ""}</Text>
        </View>

        {portfolio.projects.length ? <Text style={styles.global.sectionTitle}>Project</Text> : <View />}
        {portfolio.projects.map((project, i) => {
          return (
            <View key={project.name + i} style={{ marginBottom: 20 }} wrap={false}>
              <View style={styles.project.header}>
                <DurationView startDate={project.startDate} endDate={project.endDate} />
                <View style={styles.project.titleWrapper}>
                  <Text style={styles.project.title}>{project.name}</Text>
                  <Text style={styles.project.type}>{PROJECT_TYPE[project.type]}</Text>
                </View>
              </View>
              <View style={styles.project.body}>
                <View style={styles.project.content}>
                  <Text style={{ marginRight: 10 }}>{project.content}</Text>
                  <View style={styles.project.tagList} wrap={false}>
                    {project.tags.map((tag) => (
                      <Tag key={tag} text={tag} />
                    ))}
                  </View>
                </View>
                <View style={styles.project.thumbnail} wrap={false}></View>
              </View>
            </View>
          );
        })}

        {portfolio.sections.length ? (
          <Text style={styles.global.sectionTitle} wrap={false}>
            Details
          </Text>
        ) : (
          <View />
        )}
        {portfolio.sections.map((section, i) => (
          <View key={section.name + i}>
            <View style={styles.section.header} wrap={false}>
              <View style={styles.section.title}>
                <Text>{section.name}</Text>
              </View>
            </View>
            <View style={styles.section.itemList}>
              <View style={styles.section.verticalLiner} />
              {section.items.map((item, i) => (
                <View key={item.category + i} style={styles.section.item} wrap={false}>
                  <Text style={styles.section.category}>{item.category}</Text>
                  <View style={styles.section.descriptionList}>
                    {item.descriptions.map((description, i) => (
                      <Text key={description + i} style={styles.section.description}>
                        {description}
                      </Text>
                    ))}
                  </View>
                </View>
              ))}
            </View>
          </View>
        ))}
      </Page>
    </Document>
  );
};

export default PortfolioDocument;

// full size: 596
