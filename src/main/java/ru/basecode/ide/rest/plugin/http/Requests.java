package ru.basecode.ide.rest.plugin.http;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.function.Function;

import static ru.basecode.ide.rest.plugin.http.HttpRequest.Method.DELETE;
import static ru.basecode.ide.rest.plugin.http.HttpRequest.Method.GET;
import static ru.basecode.ide.rest.plugin.http.HttpRequest.Method.HEAD;
import static ru.basecode.ide.rest.plugin.http.HttpRequest.Method.OPTIONS;
import static ru.basecode.ide.rest.plugin.http.HttpRequest.Method.PATCH;
import static ru.basecode.ide.rest.plugin.http.HttpRequest.Method.POST;
import static ru.basecode.ide.rest.plugin.http.HttpRequest.Method.PUT;

/**
 * @author danblack
 */
public class Requests {

  private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

  public static HttpRequestBase createHttpRequest(HttpRequest request)
      throws UnsupportedEncodingException {
    if (request.getMethod() == GET) {
      HttpGet httpGet = new HttpGet(request.getUrl());
      fillHeaders(request, httpGet);
      return httpGet;
    }

    if (request.getMethod() == POST) {
      return create(HttpPost::new, request);
    }

    if (request.getMethod() == PUT) {
      return create(HttpPut::new, request);
    }

    if (request.getMethod() == PATCH) {
      return create(HttpPatch::new, request);
    }

    if (request.getMethod() == OPTIONS) {
      final HttpOptions httpOptiopns = new HttpOptions(request.getUrl());
      fillHeaders(request, httpOptiopns);
      return httpOptiopns;
    }

    if (request.getMethod() == HEAD) {
      final HttpHead httpHead = new HttpHead(request.getUrl());
      fillHeaders(request, httpHead);
      return httpHead;
    }

    if (request.getMethod() == DELETE) {
      HttpDelete httpDelete = new HttpDelete(request.getUrl());
      fillHeaders(request, httpDelete);
      return httpDelete;
    }

    throw new IllegalStateException();
  }

  public static HttpEntityEnclosingRequestBase create(
      Function<String, HttpEntityEnclosingRequestBase> constructor, HttpRequest request)
      throws UnsupportedEncodingException {
    HttpEntityEnclosingRequestBase httpRequest = constructor.apply(request.getUrl());
    fillHeaders(request, httpRequest);
    fillBody(request, httpRequest);
    return httpRequest;
  }

  private static void fillBody(HttpRequest request, HttpEntityEnclosingRequestBase httpRequest) {
    String body = request.getBody();
    if (body != null) {
      httpRequest.setEntity(new StringEntity(body, DEFAULT_CHARSET));
      for (HttpRequest.Header header : request.getHeaders()) {
        if ("Content-Type".equals(header.getName())) {
          if (!header.getValue().contains("charset")) {
            // TODO: Not sure what the author wanted to do
          }
        }
      }
    }
  }

  private static void fillHeaders(HttpRequest request, HttpRequestBase httpRequest) {
    for (HttpRequest.Header header : request.getHeaders()) {
      httpRequest.addHeader(header.getName(), header.getValue());
    }
  }
}
