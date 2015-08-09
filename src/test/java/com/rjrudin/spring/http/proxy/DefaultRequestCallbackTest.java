package com.rjrudin.spring.http.proxy;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;

public class DefaultRequestCallbackTest extends Assert {

    private DefaultRequestCallback sut;
    private MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    private MockClientHttpRequest clientRequest = new MockClientHttpRequest();

    @Test
    public void copyHeadersAndBody() throws IOException {
        servletRequest.setServletPath("/test");
        servletRequest.setQueryString("hello=world");
        servletRequest.setContent("<helloWorld/>".getBytes());
        servletRequest.addHeader("Content-type", MediaType.APPLICATION_XML.toString());
        servletRequest.addHeader("Custom-Header", "This should not be copied over");

        sut = new DefaultRequestCallback(servletRequest, "Content-type");
        sut.doWithRequest(clientRequest);

        assertEquals("The body should have been copied from the servlet request", "<helloWorld/>",
                clientRequest.getBodyAsString());
        assertEquals("The Content-type header should have been set", MediaType.APPLICATION_XML, clientRequest
                .getHeaders().getContentType());
        assertEquals("Only the Content-type header should have been set", 1, clientRequest.getHeaders().size());
    }
}
