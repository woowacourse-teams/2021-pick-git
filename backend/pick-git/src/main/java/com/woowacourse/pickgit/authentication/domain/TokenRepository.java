package com.woowacourse.pickgit.authentication.domain;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

@Profile("!test")
public interface TokenRepository extends CrudRepository<Token, String> {
}
