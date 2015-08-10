package com.rjrudin.spring.http.proxy;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;

public class DefaultResponseExtractorTest extends Assert {

    private DefaultResponseExtractor sut;
    private MockHttpServletResponse servletResponse = new MockHttpServletResponse();
    private MockClientHttpResponse clientResponse;

    @Test
    public void bodyAndMultipleHeaders() throws IOException {
        clientResponse = new MockClientHttpResponse("<helloWorld/>".getBytes(), HttpStatus.OK);
        clientResponse.getHeaders().setContentType(MediaType.APPLICATION_XML);
        clientResponse.getHeaders().add("Custom-Header-1", "This should be copied");
        clientResponse.getHeaders().add("Custom-Header-2", "This should not be copied");

        sut = new DefaultResponseExtractor(servletResponse, "Content-type", "Custom-Header-1", "Missing-Header");
        sut.extractData(clientResponse);

        assertEquals("<helloWorld/>", servletResponse.getContentAsString());
        assertEquals(MediaType.APPLICATION_XML.toString(), servletResponse.getHeader("Content-type"));
        assertEquals("This should be copied", servletResponse.getHeader("Custom-Header-1"));
        assertEquals(2, servletResponse.getHeaderNames().size());
        assertEquals(200, servletResponse.getStatus());
    }

    @Test
    public void noBodyOrHeaders() throws IOException {
        clientResponse = new MockClientHttpResponse(new byte[] {}, HttpStatus.NO_CONTENT);

        sut = new DefaultResponseExtractor(servletResponse);
        sut.extractData(clientResponse);

        assertEquals("", servletResponse.getContentAsString());
        assertEquals(0, servletResponse.getHeaderNames().size());
    }

}
