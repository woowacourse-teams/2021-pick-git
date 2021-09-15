package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.authentication.domain.Token;
import com.woowacourse.pickgit.authentication.domain.TokenRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class MockTokenRepository implements TokenRepository {

    private final Map<String, Token> values;

    public MockTokenRepository() {
        this(new ConcurrentHashMap<>());
    }

    public MockTokenRepository(Map<String, Token> values) {
        this.values = values;
    }

    @Override
    public <S extends Token> S save(S entity) {
        values.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Token> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Token> findById(String s) {
        return Optional.ofNullable(values.get(s));
    }

    @Override
    public boolean existsById(String s) {
        return values.containsKey(s);
    }

    @Override
    public Iterable<Token> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<Token> findAllById(Iterable<String> strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        return values.size();
    }

    @Override
    public void deleteById(String s) {
        values.remove(s);
    }

    @Override
    public void delete(Token entity) {
        values.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends Token> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }
}
