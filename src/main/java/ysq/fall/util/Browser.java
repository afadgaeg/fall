package ysq.fall.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

/**
 *
 * @author Administrator
 */
public class Browser {

    private String referer;
    private CookieStore cookieStore;
    private RequestConfig requestConfig;

    public Browser() {
        cookieStore = new BasicCookieStore();
        requestConfig = RequestConfig.custom().setSocketTimeout(60 * 1000)
                .setConnectTimeout(60 * 1000).setConnectionRequestTimeout(60 * 1000).build();
    }

    private ArrayList<Header> defaultHeaderList() {
//        String ip = "60.172.229.61";
        ArrayList<Header> defaultHeaderList = new ArrayList<>();
//        defaultHeaderList.add(new BasicHeader("X-FORWARDED-FOR", ip));
//        defaultHeaderList.add(new BasicHeader("CLIENT-IP", ip));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 5.2; rv:52.0) Gecko/20100101 Firefox/52.0"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"));
        defaultHeaderList.add(new BasicHeader("DNT", "1"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.CACHE_CONTROL, "max-age=0"));
        if (referer != null) {
            defaultHeaderList.add(new BasicHeader(HttpHeaders.REFERER, referer));
        }
        return defaultHeaderList;
    }

    public CloseableHttpClient getHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true).build();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

        HttpClientBuilder httpClientBuilder = HttpClients.custom();//HttpClientBuilder.create()

        return httpClientBuilder
                .setSSLSocketFactory(sslsf)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultHeaders(defaultHeaderList()).build();
    }

    public CloseableHttpClient getUnCompressionHttpClient() {
        return HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .setDefaultHeaders(defaultHeaderList())
                .disableContentCompression().build();
    }

    public HttpResponse doGet(HttpClient client, String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        referer = url;
        return response;
    }

    public HttpResponse doPost(HttpClient client, String url, List<NameValuePair> postParameters, String charset) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        if (!postParameters.isEmpty()) {
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters, charset));
        }
        HttpResponse httpResponse = client.execute(httpPost);
        return httpResponse;
    }

    //----------------------------------------------
    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

}
