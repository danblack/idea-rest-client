package ru.basecode.ide.rest.plugin.http;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author danblack
 */
@Value
@Builder
public class HttpRequest {

  Method method;
  String url;
  List<Header> headers;
  String body;

  public HttpRequest(Method method, String url, List<Header> headers, String body) {
    this.method = method;
    this.url = url;
    this.headers = headers;
    this.body = body;
  }

  @Override
  public String toString() {
    return "Request{" + "method=" + method + ", url='" + url + '\'' + ", headers=" + headers
        + ", body='" + body + '\'' + '}';
  }

  public static enum Method {
    GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD
  }


  @Value
  @Builder
  public static class Header {
    String name;
    String value;

    public Header(String name, String value) {
      this.name = name;
      this.value = value;
    }
  }


  @Value
  @Builder
  public static class Params {
    int timeout;

    public Params(int timeout) {
      this.timeout = timeout;
    }
  }
}
