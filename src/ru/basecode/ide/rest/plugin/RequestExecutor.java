package ru.basecode.ide.rest.plugin;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import ru.basecode.ide.rest.plugin.http.Response;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author danblack
 */
public class RequestExecutor {

    private static final Key<RequestExecutor> EXECUTOR_KEY = new Key<>("ExecutorKey");
    private State state = State.WAITING;

    enum State {
        WAITING,
        RUNNING,
        STOPPING
    }

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final HttpClient httpClient = createHttpClient();

    public static RequestExecutor getInstance(UserDataHolder dataHolder) {
        RequestExecutor executor = dataHolder.getUserData(EXECUTOR_KEY);
        if (executor == null) {
            executor = new RequestExecutor();
            dataHolder.putUserData(EXECUTOR_KEY, executor);
        }
        return executor;
    }

    private HttpRequestBase httpRequest;

    public void stop() {
        httpRequest.abort();
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

    public Response execute(Request request) throws IOException {
        state = State.RUNNING;
        try {
            httpRequest = Requests.createHttpRequest(request);
            HttpResponse response = httpClient.execute(httpRequest);
            return new Response(
                    response.getStatusLine().toString(),
                    getHeaders(response),
                    getContentType(response),
                    EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET));
        } finally {
            state = State.WAITING;
        }
    }

    private String getContentType(HttpResponse response) {
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

    private List<String> getHeaders(HttpResponse response) {
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
}
