package com.woowacourse.pickgit.common.request_builder;

import static io.restassured.RestAssured.given;

import com.woowacourse.pickgit.common.request_builder.post.Parameters;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import lombok.extern.java.Log;

public class LoginBuilder<T extends ServiceBuilder> {

    private RequestSpecification spec;
    private final Class<T> builderType;

    public LoginBuilder(Class<T> builderType) {
        spec = given().log().all();
        this.builderType = builderType;
    }

    public LoginBuilder<T> withUser(String token) {
        spec = given().log().all().auth().oauth2(token);
        return this;
    }

    public LoginBuilder<T> guest() {
        spec = given().log().all();
        return this;
    }

    public Parameters.Builder withInitParams() {
        return Parameters.builderWithInitParams(getServiceBuilder());
    }

    public Parameters.Builder withEmptyParams() {
        return Parameters.builder(getServiceBuilder());
    }

    private T getServiceBuilder() {
        try {
            Constructor<T> declaredConstructor = builderType
                .getDeclaredConstructor(RequestSpecification.class);

            return declaredConstructor.newInstance(spec);
        } catch (NoSuchMethodException |
            InstantiationException |
            IllegalAccessException |
            InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
