package com.woowacourse.pickgit.common.network;

import com.woowacourse.pickgit.post.domain.util.RestClient;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateClient implements RestClient {

    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 3000;
    private static final int MAX_CONN_TOTAL = 100;
    private static final int MAX_CONN_PER_ROUTE = 50;

    public final RestTemplate restTemplate = createRestTemplate();

    private static RestTemplate createRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory();

        factory.setReadTimeout(READ_TIMEOUT);
        factory.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);

        CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(MAX_CONN_TOTAL)
            .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
            .build();

        factory.setHttpClient(httpClient);

        return new RestTemplate(factory);
    }

    @Override
    public <T> T getForObject(
        String url,
        Class<T> responseType,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate.getForObject(url, responseType, uriVariables);
    }

    @Override
    public <T> T getForObject(
        String url,
        Class<T> responseType,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate.getForObject(url, responseType, uriVariables);
    }

    @Override
    public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
        return this.restTemplate.getForObject(url, responseType);
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(
        String url,
        Class<T> responseType,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate.getForEntity(url, responseType, uriVariables);
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(
        String url,
        Class<T> responseType,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate.getForEntity(url, responseType, uriVariables);
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(
        URI url,
        Class<T> responseType
    ) throws RestClientException {
        return this.restTemplate.getForEntity(url, responseType);
    }

    @Override
    public HttpHeaders headForHeaders(
        String url,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate.headForHeaders(url, uriVariables);
    }

    @Override
    public HttpHeaders headForHeaders(
        String url,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate.headForHeaders(url, uriVariables);
    }

    @Override
    public HttpHeaders headForHeaders(URI url) throws RestClientException {
        return this.restTemplate.headForHeaders(url);
    }

    @Override
    public URI postForLocation(
        String url,
        Object request,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate.postForLocation(url, request, uriVariables);
    }

    @Override
    public URI postForLocation(
        String url,
        Object request,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate.postForLocation(url, request, uriVariables);
    }

    @Override
    public URI postForLocation(URI url, Object request) throws RestClientException {
        return this.restTemplate.postForLocation(url, request);
    }

    @Override
    public <T> T postForObject(
        String url,
        Object request,
        Class<T> responseType,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate.postForObject(url, request, responseType, uriVariables);
    }

    @Override
    public <T> T postForObject(
        String url,
        Object request,
        Class<T> responseType,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate.postForObject(url, request, responseType, uriVariables);
    }

    @Override
    public <T> T postForObject(
        URI url,
        Object request,
        Class<T> responseType
    ) throws RestClientException {
        return this.restTemplate.postForObject(url, request, responseType);
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(
        String url,
        Object request,
        Class<T> responseType,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate.postForEntity(url, request, responseType, uriVariables);
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(
        String url,
        Object request,
        Class<T> responseType,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate.postForEntity(url, request, responseType, uriVariables);
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(
        URI url,
        Object request,
        Class<T> responseType
    ) throws RestClientException {
        return this.restTemplate.postForEntity(url, request, responseType);
    }

    @Override
    public void put(String url, Object request, Object... uriVariables) throws RestClientException {
        this.restTemplate.put(url, request, uriVariables);
    }

    @Override
    public void put(
        String url,
        Object request,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        this.restTemplate.put(url, request);
    }

    @Override
    public void put(URI url, Object request) throws RestClientException {
        this.restTemplate.put(url, request);
    }

    @Override
    public <T> T patchForObject(
        String url,
        Object request,
        Class<T> responseType,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate.patchForObject(url, request, responseType, uriVariables);
    }

    @Override
    public <T> T patchForObject(
        String url,
        Object request,
        Class<T> responseType,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate.patchForObject(url, request, responseType, uriVariables);
    }

    @Override
    public <T> T patchForObject(
        URI url,
        Object request,
        Class<T> responseType
    ) throws RestClientException {
        return this.restTemplate.patchForObject(url, request, responseType);
    }

    @Override
    public void delete(String url, Object... uriVariables) throws RestClientException {
        this.restTemplate.delete(url, uriVariables);
    }

    @Override
    public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        this.restTemplate.delete(url, uriVariables);
    }

    @Override
    public void delete(URI url) throws RestClientException {
        this.restTemplate.delete(url);
    }

    @Override
    public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables)
        throws RestClientException {
        return this.restTemplate.optionsForAllow(url, uriVariables);
    }

    @Override
    public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables)
        throws RestClientException {
        return this.restTemplate.optionsForAllow(url, uriVariables);
    }

    @Override
    public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
        return this.restTemplate.optionsForAllow(url);
    }

    @Override
    public <T> ResponseEntity<T> exchange(
        String url,
        HttpMethod method,
        HttpEntity<?> requestEntity,
        Class<T> responseType,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    @Override
    public <T> ResponseEntity<T> exchange(
        String url,
        HttpMethod method,
        HttpEntity<?> requestEntity,
        Class<T> responseType,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    @Override
    public <T> ResponseEntity<T> exchange(
        URI url,
        HttpMethod method,
        HttpEntity<?> requestEntity,
        Class<T> responseType
    ) throws RestClientException {
        return this.restTemplate.exchange(url, method, requestEntity, responseType);
    }

    @Override
    public <T> ResponseEntity<T> exchange(
        String url,
        HttpMethod method,
        HttpEntity<?> requestEntity,
        ParameterizedTypeReference<T> responseType,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    @Override
    public <T> ResponseEntity<T> exchange(
        String url,
        HttpMethod method,
        HttpEntity<?> requestEntity,
        ParameterizedTypeReference<T> responseType,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    @Override
    public <T> ResponseEntity<T> exchange(
        URI url,
        HttpMethod method,
        HttpEntity<?> requestEntity,
        ParameterizedTypeReference<T> responseType
    ) throws RestClientException {
        return this.restTemplate.exchange(url, method, requestEntity, responseType);
    }

    @Override
    public <T> ResponseEntity<T> exchange(
        RequestEntity<?> requestEntity,
        Class<T> responseType
    ) throws RestClientException {
        return this.restTemplate.exchange(requestEntity, responseType);
    }

    @Override
    public <T> ResponseEntity<T> exchange(
        RequestEntity<?> requestEntity,
        ParameterizedTypeReference<T> responseType
    ) throws RestClientException {
        return this.restTemplate.exchange(requestEntity, responseType);
    }

    @Override
    public <T> T execute(
        String url,
        HttpMethod method,
        RequestCallback requestCallback,
        ResponseExtractor<T> responseExtractor,
        Object... uriVariables
    ) throws RestClientException {
        return this.restTemplate
            .execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> T execute(
        String url,
        HttpMethod method,
        RequestCallback requestCallback,
        ResponseExtractor<T> responseExtractor,
        Map<String, ?> uriVariables
    ) throws RestClientException {
        return this.restTemplate
            .execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public <T> T execute(
        URI url,
        HttpMethod method,
        RequestCallback requestCallback,
        ResponseExtractor<T> responseExtractor
    ) throws RestClientException {
        return this.restTemplate.execute(url, method, requestCallback, responseExtractor);
    }
}
