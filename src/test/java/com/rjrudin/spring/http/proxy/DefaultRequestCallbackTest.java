package com.rjrudin.spring.http.proxy;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;

public class DefaultRequestCallbackTest extends Assert {

    private DefaultRequestCallback sut;
    private MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    private MockClientHttpRequest clientRequest = new MockClientHttpRequest();

    @Test
    public void bodyAndMultipleHeaders() throws IOException {
        servletRequest.setContent("<helloWorld/>".getBytes());
        servletRequest.addHeader("Content-type", MediaType.APPLICATION_XML.toString());
        servletRequest.addHeader("Custom-Header-1", "This should be copied over");
        servletRequest.addHeader("Custom-Header-2", "This should not be copied over");

        sut = new DefaultRequestCallback(servletRequest, "Content-type", "Custom-Header-1");
        sut.doWithRequest(clientRequest);

        assertEquals("The body should have been copied from the servlet request", "<helloWorld/>",
                clientRequest.getBodyAsString());

        HttpHeaders headers = clientRequest.getHeaders();
        assertEquals("The Content-type header should have been set", MediaType.APPLICATION_XML,
                headers.getContentType());
        assertEquals("The custom header should be set", "This should be copied over", headers.get("Custom-Header-1")
                .get(0));
        assertEquals(2, clientRequest.getHeaders().size());
    }

    @Test
    public void emptyBodyAndNoHeaders() throws IOException {
        sut = new DefaultRequestCallback(servletRequest);
        sut.doWithRequest(clientRequest);

        assertEquals("The body as a string should be empty (but not null, the body always exists", "",
                clientRequest.getBodyAsString());
        assertEquals(0, clientRequest.getHeaders().size());
    }
}
