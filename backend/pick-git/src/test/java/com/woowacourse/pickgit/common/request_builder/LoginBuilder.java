package com.woowacourse.pickgit.common.request_builder;

import static io.restassured.RestAssured.given;

import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import io.restassured.specification.RequestSpecification;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;

public class LoginBuilder<T extends Parameters> {

    private RequestSpecification spec;
    private final Class<T> parameterType;
    private final HttpMethod httpMethod;
    private final String url;
    private final Object[] params;

    public LoginBuilder(
        Class<T> parameterType,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        spec = given().log().all().contentType(ContentType.APPLICATION_JSON.getMimeType());
        this.parameterType = parameterType;
        this.httpMethod = httpMethod;
        this.url = url;
        this.params = params;
    }

    public T withUser() {
        return withUser(createRandomString());
    }

    private String createRandomString() {
        String seed = String.valueOf(LocalDateTime.now().getNano());
        return DigestUtils.md5DigestAsHex(seed.getBytes());
    }

    public T withUser(String name) {
        return setOauth2ToSpec(requestLogin(name));
    }

    private T setOauth2ToSpec(String token) {
        spec = given().log().all().auth().oauth2(token);

        return getParameterBuilder();
    }

    private String requestLogin(String code) {
        return given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code={code}", code)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(TokenDto.class)
            .getToken();
    }

    public T withGuest() {
        spec = given().log().all();
        return getParameterBuilder();
    }

    private T getParameterBuilder() {
        try {
            Constructor<T> declaredConstructor = parameterType
                .getDeclaredConstructor(
                    RequestSpecification.class,
                    HttpMethod.class,
                    String.class,
                    Object[].class
                );

            return declaredConstructor.newInstance(spec, httpMethod, url, params);
        } catch (NoSuchMethodException |
            InstantiationException |
            IllegalAccessException |
            InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
