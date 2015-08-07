package com.marklogic.spring.proxy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class MarkLogicProxy {

    // These would normally be in another Spring bean
    private String host = "localhost";
    private int port = 8320;
    private String username = "admin";
    private String password = "admin";

    private final static Logger logger = LoggerFactory.getLogger(MarkLogicProxy.class);

    public void proxy(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        RestTemplate t = newRestTemplate(host, port, username, password);

        String url = buildUrl(httpRequest);
        if (logger.isInfoEnabled()) {
            logger.info("Proxying to URL: " + url);
        }

        t.execute(url, determineMethod(httpRequest), new RequestCallback() {
            @Override
            public void doWithRequest(ClientHttpRequest request) throws IOException {
                FileCopyUtils.copy(httpRequest.getInputStream(), request.getBody());
            }
        }, new ResponseExtractor<Void>() {
            @Override
            public Void extractData(ClientHttpResponse response) throws IOException {
                FileCopyUtils.copy(response.getBody(), httpResponse.getOutputStream());
                return null;
            }
        });
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

    public static RestTemplate newRestTemplate(String host, int port, String username, String password) {
        BasicCredentialsProvider prov = new BasicCredentialsProvider();
        prov.setCredentials(new AuthScope(host, port, AuthScope.ANY_REALM), new UsernamePasswordCredentials(username,
                password));
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(prov).build();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
    }
}
