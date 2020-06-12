package com.ubs.Managers;

import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.KeyStore;
import java.util.zip.GZIPInputStream;


/**
 * @author kedarpi
 */
public class RestAPIManager {

    private static RestAPIManager instance = null;

    private RestAPIManager() {

    }

    public static RestAPIManager instance() {
        if (instance == null)
            instance = new RestAPIManager();
        return instance;
    }
    public int getStatusCode(String url, Header[] headers) throws Exception {
        try (CloseableHttpClient httpclient = getHttpClient()) {
            HttpGet httpget = new HttpGet(url);
            System.out.println("Executing request " + httpget.getRequestLine());

            for (Header head : headers) {
                httpget.addHeader(head);
            }

            HttpResponse response = httpclient.execute(httpget);
            return response.getStatusLine().getStatusCode();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public String getRequest(String url, Header[] headers) throws Exception{

        try (CloseableHttpClient httpclient = getHttpClient()) {
            HttpGet httpget = new HttpGet(url);
            System.out.println("Executing request " + httpget.getRequestLine());

            for (Header head: headers) {
                httpget.addHeader(head);
            }
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody.toString());
            return responseBody.toString();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getRequest(String url, Header[] headers, String certpath) throws Exception{

        try (CloseableHttpClient httpclient = getHttpClientWithCertificate(certpath)) {
            HttpGet httpget = new HttpGet(url);
            for (Header head: headers) {
                httpget.addHeader(head);
            }
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            return responseBody;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String postRequest(String url, Header[] headers, JSONObject payload) throws Exception{

       return postRequest(url, headers, payload.toString());
    }

    public String postRequest(String url, Header[] headers, String payload) throws Exception{

        CloseableHttpClient httpClient = getHttpClient();
        return postCreator(url,headers,payload,httpClient);
    }

    public String postRequest(String url, Header[] headers, String payload, String certPath) throws Exception{

        CloseableHttpClient httpClient = getHttpClientWithCertificate(certPath);
        return postCreator(url,headers,payload,httpClient);
    }

    private String postCreator(String url, Header[] headers, String payload, CloseableHttpClient httpClient) {
       try {
           HttpPost httpPost = new HttpPost(url);
           //add header
           for (Header head : headers) {
               httpPost.addHeader(head);
           }
           //add payload
           StringEntity params = new StringEntity(payload);
           httpPost.setEntity(params);
           CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
           int status = httpResponse.getStatusLine().getStatusCode();
           LoggerManager.getInstance().log("status code for service : " + status);
           return EntityUtils.toString(httpResponse.getEntity());
       }catch (Exception e){
           e.printStackTrace();
       }
       return null;
    }

    public String postMultiPartRequest(String url, Header[] headers, MultipartEntityBuilder builder){

        try {
            CloseableHttpClient httpClient = getHttpClient();
            HttpPost httpPost = new HttpPost(url);
            for (Header head: headers) {
                httpPost.addHeader(head);
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            LoggerManager.getInstance().log("status code for service : "+status);
            return EntityUtils.toString(response.getEntity());

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String putRequest(String url, Header[] headers, JSONObject payload) throws Exception{

        //CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getHttpClient();
        HttpPut httpPut = new HttpPut(url);
        //add header
        for (Header head: headers) {
            httpPut.addHeader(head);
        }
        //add payload
        StringEntity params = new StringEntity(payload.toString());
        httpPut.setEntity(params);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPut);
        return EntityUtils.toString(httpResponse.getEntity());
    }

    private final CloseableHttpClient getHttpClient() throws Exception {

        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory> create()
                        .register("https", sslsf)
                        .register("http", new PlainConnectionSocketFactory())
                        .build();
        BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(connectionManager).build();
        return httpClient;
    }

    private final CloseableHttpClient getHttpClientWithCertificate(String certPath) throws Exception {

        FileInputStream inputStream = null;
        CloseableHttpClient httpClient;
        try {
            inputStream = new FileInputStream(new File(certPath));
            KeyStore keystore = KeyStore.getInstance("JKS");
            char[] partnerId2charArray = "123456".toCharArray();
            keystore.load(inputStream, partnerId2charArray);

            SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(new File(certPath), partnerId2charArray, partnerId2charArray).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", sslsf)
                            .build();
            BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(connectionManager).build();
        }
        catch (IOException e) {
            throw new RuntimeException("certificate not loading", e);
        }
        return httpClient;
    }

    public String getGzipRequest(String url, Header[] headers, String certpath) throws Exception{

        try (CloseableHttpClient httpclient = getHttpClientWithCertificate(certpath)) {
            HttpGet httpget = new HttpGet(url);
            System.out.println("Executing request " + httpget.getRequestLine());

            for (Header head: headers) {
                httpget.addHeader(head);
            }
            HttpResponse response = httpclient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200)
                throw new Exception("Service failing" + ":" + statusCode);
            InputStream io = response.getEntity().getContent();
            GZIPInputStream gzip = new GZIPInputStream(io);
            BufferedReader br =new BufferedReader(new InputStreamReader(gzip));
            String entitlementResponseStr = IOUtils.toString(br);
            return entitlementResponseStr;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getGzipRequest(String url, Header[] headers) throws Exception{

        try (CloseableHttpClient httpclient = getHttpClient()) {
            HttpGet httpget = new HttpGet(url);

            for (Header head: headers) {
                httpget.addHeader(head);
            }
            HttpResponse response = httpclient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200)
                throw new Exception("Response failing " + ":" + statusCode);
            InputStream io = response.getEntity().getContent();
            GZIPInputStream gzip = new GZIPInputStream(io);
            BufferedReader br =new BufferedReader(new InputStreamReader(gzip));
            String entitlementResponseStr = IOUtils.toString(br);
            return entitlementResponseStr;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
