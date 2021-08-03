package com.woowacourse.pickgit.common.request_builder.parameters;

import static com.woowacourse.pickgit.common.PickgitHeaders.IMAGES;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSenderOptions;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpMethod;

public abstract class Parameters {

    private final RequestSpecification spec;
    private final HttpMethod httpMethod;
    private final String url;
    private final Object[] params;

    private final Map<String, Object> formParams;
    private final List<File> multiparts;

    public Parameters(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        this.spec = spec;
        this.httpMethod = httpMethod;
        this.url = url;
        this.params = params;

        this.formParams = new HashMap<>();
        this.multiparts = new ArrayList<>();
    }

    protected void setBody(File data) {
        spec.body(data);
    }

    protected void setBody(String data) {
        spec.body(data);
    }

    protected void setBody(byte[] data) {
        spec.body(data);
    }

    protected void setParam(String key, Object value) {
        formParams.put(key, value);
    }

    protected void setMultiparts(List<File> files) {
        this.multiparts.clear();
        this.multiparts.addAll(files);
    }

    protected void setMultiparts(File... files) {
        setMultiparts(List.of(files));
    }

    public ExtractableResponse<Response> extract() {
        var specWithParams = spec.formParams(formParams);

        for (File file : multiparts) {
            specWithParams = specWithParams.multiPart(IMAGES, file);
        }

        return Methods.extract(specWithParams, httpMethod, url, params);
    }

    private enum Methods {
        GET(RequestSenderOptions::get),
        POST(RequestSpecification::post),
        PUT(RequestSpecification::put),
        DELETE(RequestSpecification::delete);

        private final HttpMethodSetter spec;

        Methods(HttpMethodSetter operator) {
            this.spec = operator;
        }

        private static ExtractableResponse<Response> extract(
            RequestSpecification spec,
            HttpMethod httpMethod,
            String url,
            Object... params
        ) {
            Methods methods = List.of(values()).stream()
                .filter(value -> httpMethod.matches(value.name()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);

            return methods.spec.set(spec, url, params)
                .then().log().all()
                .extract();
        }

    }

    private interface HttpMethodSetter {
        Response set(RequestSpecification spec, String url, Object... params);
    }
}
