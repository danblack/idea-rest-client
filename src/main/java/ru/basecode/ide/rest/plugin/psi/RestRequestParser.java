package ru.basecode.ide.rest.plugin.psi;

import com.google.common.net.UrlEscapers;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.http.HttpRequest;
import ru.basecode.ide.rest.plugin.http.HttpRequest.Method;
import ru.basecode.ide.rest.plugin.psi.RestEHeader;
import ru.basecode.ide.rest.plugin.psi.RestEMethod;
import ru.basecode.ide.rest.plugin.psi.RestEParam;
import ru.basecode.ide.rest.plugin.psi.RestEUrl;
import ru.basecode.ide.rest.plugin.psi.RestHeaders;
import ru.basecode.ide.rest.plugin.psi.RestParams;
import ru.basecode.ide.rest.plugin.psi.RestRequest;
import ru.basecode.ide.rest.plugin.psi.RestRequestBody;

import java.util.ArrayList;
import java.util.List;
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
public class RestRequestParser {

  private static final Function<String, String> encoder =
      v -> UrlEscapers.urlFormParameterEscaper().escape(v);

  private static Method getMethod(RestRequest request) {
    RestEMethod method = request.getEMethod();
    if (method != null && method.isValid()) {
      switch (method.getText()) {
        case "GET":
          return GET;
        case "PUT":
          return PUT;
        case "POST":
          return POST;
        case "DELETE":
          return DELETE;
        case "PATCH":
          return PATCH;
        case "OPTIONS":
          return OPTIONS;
        case "HEAD":
          return HEAD;
      }
      throw new IllegalStateException();
    }
    return GET;
  }

  public static HttpRequest parse(RestRequest request) {
    Method method = getMethod(request);
    String url = getUrl(request);
    String body = getBody(request);
    List<HttpRequest.Header> headers = getHeaders(request);
    return HttpRequest.builder().method(method).url(url).headers(headers).body(body).build();
  }

  private static List<HttpRequest.Header> getHeaders(RestRequest request) {
    ArrayList<HttpRequest.Header> result = new ArrayList<>();
    RestHeaders headers = request.getHeaders();
    if (headers != null) {
      for (RestEHeader header : headers.getEHeaderList()) {
        String text = header.getText();
        int i = text.indexOf("@");
        if (i >= 0) {
          i = text.indexOf(":");
          if (i >= 0) {
            String name = text.substring(1, i).trim();
            String value = text.substring(i + 1).trim();
            result.add(new HttpRequest.Header(name, value));
          }
        }
      }
    }
    return result;
  }

  private static String getBody(RestRequest request) {
    RestRequestBody body = request.getRequestBody();
    if (body != null && body.isValid()) {
      return body.getText();
    }
    return null;
  }

  @NotNull
  private static String getUrl(RestRequest request) {
    RestEUrl url = request.getEUrl();
    if (url.isValid()) {
      String urlText = url.getText();
      boolean markExists = urlText.contains("?");
      StringBuilder sb = new StringBuilder(urlText);
      RestParams params = request.getParams();
      if (params != null && params.isValid()) {
        if (!markExists) {
          sb.append('?');
        }
        for (RestEParam param : params.getEParamList()) {
          sb.append(param.getText());
        }
      }
      return encode(sb.toString());
    }
    throw new IllegalStateException("");
  }

  static String encode(String url) {
    StringBuilder sb = new StringBuilder(url.length());
    int qpos = url.indexOf("?");
    if (qpos == -1) {
      return url;
    }
    sb.append(url.substring(0, qpos + 1));
    url = url.substring(qpos + 1);
    String delimiter = "";
    for (String paramEntry : url.split("&")) {
      sb.append(delimiter);
      int epos = paramEntry.indexOf("=");
      if (epos == -1) {
        sb.append(UrlEscapers.urlFormParameterEscaper().escape(paramEntry.trim()));
      } else {
        String paramName = paramEntry.substring(0, epos).trim();
        String paramValue = paramEntry.substring(epos + 1).trim();
        sb.append(encoder.apply(paramName)).append("=").append(encoder.apply(paramValue));
      }
      delimiter = "&";
    }
    return sb.toString();
  }
}
