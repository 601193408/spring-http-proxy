package com.marklogic.spring.proxy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RequestCallback;

public class DefaultRequestCallback implements RequestCallback {

    private HttpServletRequest httpRequest;

    public DefaultRequestCallback(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public void doWithRequest(ClientHttpRequest request) throws IOException {
        FileCopyUtils.copy(httpRequest.getInputStream(), request.getBody());
    }

}
