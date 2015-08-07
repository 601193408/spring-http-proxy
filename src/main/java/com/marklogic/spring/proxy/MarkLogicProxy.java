package com.marklogic.spring.proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class MarkLogicProxy {

    private RestTemplate restTemplate;
    private String host;
    private int port;

    private final static Logger logger = LoggerFactory.getLogger(MarkLogicProxy.class);

    public MarkLogicProxy(RestTemplate restTemplate, String host, int port) {
        this.restTemplate = restTemplate;
        this.host = host;
        this.port = port;
    }

    public void proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        proxy(httpRequest, httpResponse, new DefaultRequestCallback(httpRequest), new DefaultResponseExtractor(
                httpResponse));
    }

    public <T> T proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse, RequestCallback requestCallback,
            ResponseExtractor<T> responseExtractor) {
        String url = buildUrl(httpRequest);
        HttpMethod method = determineMethod(httpRequest);

        if (logger.isInfoEnabled()) {
            logger.info("Proxying to URL: " + url);
        }

        return restTemplate.execute(url, method, requestCallback, responseExtractor);
    }

    protected HttpMethod determineMethod(HttpServletRequest request) {
        return HttpMethod.valueOf(request.getMethod());
    }

    protected String buildUrl(HttpServletRequest request) {
        String url = String.format("http://%s:%d%s", host, port, request.getServletPath());
        String qs = request.getQueryString();
        if (qs != null) {
            url += "?" + qs;
        }
        return url;
    }
}
