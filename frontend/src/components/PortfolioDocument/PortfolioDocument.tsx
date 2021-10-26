import { Document, Page, View, Text, Image } from "@react-pdf/renderer";

import { Portfolio, ProfileData } from "../../@types";
import styles from "./PortfolioDocument.style";

interface PortfolioDocumentProps {
  profile?: ProfileData | null;
  portfolio?: Portfolio;
}

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

const PortfolioDocument = ({ profile, portfolio }: PortfolioDocumentProps) => {
  return (
    <Document>
      <Page style={styles.global.page}>
        <View style={styles.profile.profileContainer}>
          <View style={styles.profile.basic}>
            {portfolio?.intro.isProfileShown ? (
              <View style={styles.profile.imageWrapper}>
                <Image style={styles.profile.image} src={profile?.imageUrl ?? ""} />
              </View>
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

        {portfolio?.projects.length ? <Text style={styles.global.sectionTitle}>Project</Text> : <View />}
        {portfolio?.projects.map((project, i) => {
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
                    {project.tags.map((tag, i) => (
                      <Tag key={tag} text={tag} />
                    ))}
                  </View>
                </View>
                <View style={styles.project.thumbnailWrapper}>
                  <Image style={styles.project.thumbnail} src={project.imageUrl ?? ""} />
                </View>
              </View>
            </View>
          );
        })}

        {portfolio?.sections.length ? (
          <Text style={styles.global.sectionTitle} wrap={false}>
            Details
          </Text>
        ) : (
          <View />
        )}
        {portfolio?.sections.map((section, i) => (
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
                      <Text key={description.id ?? "" + i} style={styles.section.description}>
                        {description.value}
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
