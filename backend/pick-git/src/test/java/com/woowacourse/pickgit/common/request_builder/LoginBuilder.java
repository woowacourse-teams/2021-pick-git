package com.woowacourse.pickgit.common.request_builder;

import static io.restassured.RestAssured.given;

import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import io.restassured.specification.RequestSpecification;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
        spec = given().log().all();
        this.parameterType = parameterType;
        this.httpMethod = httpMethod;
        this.url = url;
        this.params = params;
    }

    public T withUser() {
        return withUser(requestLogin());
    }

    public T withUser(String token) {
        spec = given().log().all().auth().oauth2(token);

        return getParameterBuilder();
    }

    public T withGuest() {
        spec = given().log().all();
        return getParameterBuilder();
    }

    private String requestLogin() {
        return given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code=1234")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(TokenDto.class)
            .getToken();
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
