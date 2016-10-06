package ru.basecode.ide.rest.plugin;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.function.Function;

/**
 * @author danblack
 */
public class Requests {

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    public static HttpRequestBase createHttpRequest(Request request) throws UnsupportedEncodingException {
        if (request.getMethod() == Request.Method.GET) {
            HttpGet httpGet = new HttpGet(request.getUrl());
            fillHeaders(request, httpGet);
            return httpGet;
        }

        if (request.getMethod() == Request.Method.POST) {
            return create(HttpPost::new, request);
        }

        if (request.getMethod() == Request.Method.PUT) {
            return create(HttpPut::new, request);
        }

        if (request.getMethod() == Request.Method.PATCH) {
            return create(HttpPatch::new, request);
        }

        if (request.getMethod() == Request.Method.DELETE) {
            HttpDelete httpDelete = new HttpDelete(request.getUrl());
            fillHeaders(request, httpDelete);
            return httpDelete;
        }

        throw new IllegalStateException();
    }

    public static HttpEntityEnclosingRequestBase create(Function<String, HttpEntityEnclosingRequestBase> constructor,
                                                 Request request)
            throws UnsupportedEncodingException {
        HttpEntityEnclosingRequestBase httpRequest = constructor.apply(request.getUrl());
        fillHeaders(request, httpRequest);
        fillBody(request, httpRequest);
        return httpRequest;
    }

    private static void fillBody(Request request, HttpEntityEnclosingRequestBase httpRequest)
            throws UnsupportedEncodingException {
        String body = request.getBody();
        if (body != null) {
            httpRequest.setEntity(new StringEntity(body, DEFAULT_CHARSET));
            for (Request.Header header : request.getHeaders()) {
                if ("Content-Type".equals(header.getName())) {
                    if (!header.getValue().contains("charset")) {

                    }
                }
            }
        }
    }

    private static void fillHeaders(Request request, HttpRequestBase httpRequest) {
        for (Request.Header header : request.getHeaders()) {
            httpRequest.addHeader(header.getName(), header.getValue());
        }
    }
}
