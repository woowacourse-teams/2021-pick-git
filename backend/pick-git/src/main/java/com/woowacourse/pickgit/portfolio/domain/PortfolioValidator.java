package com.woowacourse.pickgit.portfolio.domain;

import com.woowacourse.pickgit.exception.portfolio.PortfolioConstraintException;
import com.woowacourse.pickgit.portfolio.domain.project.Project;
import com.woowacourse.pickgit.portfolio.domain.section.Section;
import com.woowacourse.pickgit.portfolio.domain.section.item.Item;
import java.util.List;

public class PortfolioValidator {

    public static void username(String username) {
        if (username.length() > 50) {
            throw new PortfolioConstraintException("유저 이름은 50자를 넘을 수 없습니다.");
        }
    }

    public static void introduction(String introduction) {
        if (introduction.length() > 200) {
            throw new PortfolioConstraintException("자기소개는 200자를 넘을 수 없습니다.");
        }
    }

    public static void projectSize(List<Project> projects) {
        if (projects.size() > 10) {
            throw new PortfolioConstraintException("프로젝트의 개수는 10개를 넘을 수 없습니다.");
        }
    }

    public static void projectContent(String content) {
        if (content.length() > 3000) {
            throw new PortfolioConstraintException("프로젝트 본문은 3000자를 넘을 수 없습니다.");
        }
    }

    public static void sectionCategorySize(List<Item> items) {
        if(items.size() > 10 ) {
            throw new PortfolioConstraintException("한 섹션 당 카테고리 개수는 10개를 넘을 수 없습니다.");
        }
    }

    public static void sectionCategory(String name) {
        if(name.length() > 50) {
            throw new PortfolioConstraintException("카테고리 이름은 50자를 넘을 수 없습니다.");
        }
    }

    public static void sectionDescription(String description) {
        if(description.length() > 300) {
            throw new PortfolioConstraintException("섹션은 300자를 넘을 수 없습니다.");
        }
    }

    public static void sectionSize(List<Section> sections) {
        if(sections.size() > 10) {
            throw new PortfolioConstraintException("섹션의 개수는 10개를 넘을 수 없습니다.");
        }
    }
}
