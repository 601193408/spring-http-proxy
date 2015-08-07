package com.marklogic.spring.proxy;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseExtractor;

public class DefaultResponseExtractor implements ResponseExtractor<Void> {

    private HttpServletResponse httpResponse;

    public DefaultResponseExtractor(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public Void extractData(ClientHttpResponse response) throws IOException {
        FileCopyUtils.copy(response.getBody(), httpResponse.getOutputStream());
        return null;
    }

}
