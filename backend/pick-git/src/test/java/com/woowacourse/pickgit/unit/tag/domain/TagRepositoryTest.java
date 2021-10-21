package com.woowacourse.pickgit.unit.tag.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.config.JpaTestConfiguration;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@Import(JpaTestConfiguration.class)
@DataJpaTest
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("태그를 저장하고 조회할 수 있다.")
    @Test
    void saveAndFindByName_FindSavedTag_Success() {
        // given
        Tag savedTag = tagRepository.save(new Tag("java"));

        entityManager.flush();
        entityManager.clear();

        // when
        Tag findTag = tagRepository.findByName(savedTag.getName())
            .orElse(null);

        // then
        assertThat(findTag).isNotNull();
        assertThat(savedTag.getId()).isEqualTo(findTag.getId());
        assertThat(savedTag.getName()).isEqualTo(findTag.getName());
    }

    @DisplayName("존재하지 않는 태그 조회시 Optional.empty를 반환한다.")
    @Test
    void findByName_FindNonExistsTag_ReturnOptionalEmpty() {
        // given
        String name = "nonexists tag name";

        // when
        Optional<Tag> findTag = tagRepository.findByName(name);

        // then
        assertThat(findTag).isEqualTo(Optional.empty());
    }

    @DisplayName("이미 존재하는 태그(이름)을 저장하면 예외가 발생한다.")
    @Test
    void save_DuplicateTagName_Fail() {
        // given
        Tag savedTag = tagRepository.save(new Tag("java"));

        entityManager.flush();
        entityManager.clear();

        // when, then
        assertThatThrownBy(() -> tagRepository.save(new Tag(savedTag.getName())))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}
