package ru.basecode.ide.rest.plugin.http;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author danblack
 */
public class RequestExecutor {

  private static final Key<RequestExecutor> EXECUTOR_KEY = new Key<>("ExecutorKey");

  private static final Charset DEFAULT_CHARSET = UTF_8;
  private final HttpClient httpClient = createHttpClient();
  private State state = State.WAITING;
  private HttpRequestBase httpRequest;

  @Nullable
  public static RequestExecutor getOrDefault(AnActionEvent event) {
    // Its possible for the event that triggered this might not have an editor associated
    final Editor editor = event.getData(CommonDataKeys.EDITOR);
    if (editor != null) {
      return getOrDefault(editor);
    }
    return null;
  }

  @NotNull
  public static RequestExecutor getOrDefault(UserDataHolder dataHolder) {
    RequestExecutor executor = dataHolder.getUserData(EXECUTOR_KEY);
    if (executor == null) {
      executor = new RequestExecutor();
      dataHolder.putUserData(EXECUTOR_KEY, executor);
    }
    return executor;
  }

  public void stop() {
    if (httpRequest != null) {
      httpRequest.abort();
    }
  }

  private CloseableHttpClient createHttpClient() {
    try {
      SSLContextBuilder contextBuilder = SSLContextBuilder.create();
      contextBuilder.loadTrustMaterial(null, (x509Certificates, s) -> true);
      SSLConnectionSocketFactory sslSocketFactory =
          new SSLConnectionSocketFactory(contextBuilder.build(), NoopHostnameVerifier.INSTANCE);
      return HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
    } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
      throw new RuntimeException(e);
    }
  }

  public HttpResponse execute(HttpRequest request) throws IOException {
    //@formatter:off
    state = State.RUNNING;
    try {
      httpRequest = Requests.createHttpRequest(request);
      org.apache.http.HttpResponse response = httpClient.execute(httpRequest);
      if (response.getEntity() != null) {
        return HttpResponse.builder()
            .status(response.getStatusLine().toString())
            .headers(getHeaders(response))
            .contentType(getContentType(response))
            .body(EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET))
            .build();
       } else {
        return HttpResponse.builder()
            .status(response.getStatusLine().toString())
            .headers(getHeaders(response))
            .contentType(getContentType(response))
            .build();
      }
    } finally {
      state = State.WAITING;
    }
    //@formatter:on
  }

  private String getContentType(org.apache.http.HttpResponse response) {
    Header header = response.getFirstHeader("Content-Type");
    if (header != null) {
      String contentType = header.getValue();
      int semicolonIndex = contentType.indexOf(";");
      if (semicolonIndex >= 0) {
        return contentType.substring(0, semicolonIndex);
      } else {
        return contentType;
      }
    }
    return null;
  }

  private List<String> getHeaders(org.apache.http.HttpResponse response) {
    Header[] allHeaders = response.getAllHeaders();
    if (allHeaders != null) {
      ArrayList<String> result = new ArrayList<>();
      for (Header header : allHeaders) {
        result.add(header.getName() + ": " + header.getValue());
      }
      return result;
    }
    return Collections.emptyList();
  }

  public boolean isRunning() {
    return state == State.RUNNING;
  }

  public boolean isWaiting() {
    return state == State.WAITING;
  }

  public State getState() {
    return state;
  }

  public enum State {
    WAITING, RUNNING, STOPPING
  }
}
