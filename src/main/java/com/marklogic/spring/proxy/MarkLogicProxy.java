package com.marklogic.spring.proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class MarkLogicProxy extends LoggingObject {

    private RestTemplate restTemplate;
    private String host;
    private int port;

    public MarkLogicProxy(RestTemplate restTemplate, String host, int port) {
        this.restTemplate = restTemplate;
        this.host = host;
        this.port = port;
    }

    /**
     * Proxy a request without copying any headers.
     * 
     * @param httpRequest
     * @param httpResponse
     */
    public void proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        proxy(httpRequest, httpResponse, new DefaultRequestCallback(httpRequest), new DefaultResponseExtractor(
                httpResponse));
    }

    /**
     * Proxy a request and copy the given headers on both the request and the response.
     * 
     * @param httpRequest
     * @param httpResponse
     * @param headerNamesToCopy
     */
    public void proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String... headerNamesToCopy) {
        proxy(httpRequest, httpResponse, new DefaultRequestCallback(httpRequest, headerNamesToCopy),
                new DefaultResponseExtractor(httpResponse, headerNamesToCopy));
    }

    /**
     * Specify your own request callback and response extractor. This gives you the most flexibility, but does the least
     * for you.
     * 
     * @param httpRequest
     * @param httpResponse
     * @param requestCallback
     * @param responseExtractor
     * @return
     */
    public <T> T proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
        String url = buildUrl(httpRequest);
        HttpMethod method = determineMethod(httpRequest);

        if (logger.isInfoEnabled()) {
            logger.info(format("Proxying to URL: %s", url));
        }

        return restTemplate.execute(url, method, requestCallback, responseExtractor);
    }

    protected HttpMethod determineMethod(HttpServletRequest request) {
        return HttpMethod.valueOf(request.getMethod());
    }

    protected String buildUrl(HttpServletRequest request) {
        String url = format("http://%s:%d%s", host, port, request.getServletPath());
        String qs = request.getQueryString();
        if (qs != null) {
            url += "?" + qs;
        }
        return url;
    }
}
