package com.newthread.shiquan.dao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by lanqx on 14-3-23.
 */
public class MyRequest {
    private final static String TAG = "MyRequest";
    //    public final static int REQUEST_SUCCESS = 1;
    public final static int REQUEST_NO_NET = 0;
    public final static int REQUEST_NET_TIMEOUT = -1;
    public final static int SOCKET_NET_TIMEOUT = -2;
    private static final int THREAD_TIMEOUT = 1 * 1000;// 设置请求超时10秒钟
    private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时5秒钟
    private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟

    private final static String CHARSET = HTTP.UTF_8;

    private HttpClient httpClient;
    private List<NameValuePair> params;

    public String getCookieStr() {
        return cookieStr;
    }

    private String cookieStr;
    private boolean isSSL = false;

    private MyRequest() {
        init();
    }

    private void init() {
        if (this.httpClient == null) {
            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);
            // 超时设置
            /* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, THREAD_TIMEOUT);
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);

            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            this.httpClient = new DefaultHttpClient(conMgr, params);
            this.params = new ArrayList<NameValuePair>();
        }
    }

    private static class RequestContainer {

        private static MyRequest instance = new MyRequest();
    }

    public static MyRequest getInstance() {
        return RequestContainer.instance;
    }

    public void post(final String url, final List<NameValuePair> params,
                     final IRequestListener iRequestListener) {

        final Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                resultHandler(message.what, message.obj, iRequestListener);
//                iRequestListener.onComplete(message.what, message.obj);
            }
        };

        new Thread() {
            public void run() {
                try {
                    HttpPost httpPost = new HttpPost(url);
                    HttpEntity entity = new UrlEncodedFormEntity(params,
                            CHARSET);
                    /*
                     * 070 将POST数据放入HTTP请求071
					 */
                    httpPost.setEntity(entity);
                    /*
                     * 074 发出实际的HTTP POST请求075
					 */
//					HttpParams httpParameters = new BasicHttpParams();
//					HttpConnectionParams.setConnectionTimeout(httpParameters,
//							REQUEST_TIMEOUT); // 设置连接超时
//					HttpConnectionParams.setSoTimeout(httpParameters,
//							SO_TIMEOUT); // 设置请求超时
//					httpPost.setParams(httpParameters);
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    int statusCode = httpResponse.getStatusLine()
                            .getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        byte[] data = inputStreamToByte(httpResponse
                                .getEntity().getContent());
                        Message message = handler.obtainMessage(statusCode, data);
                        handler.sendMessage(message);
                    } else {
                        Message message = handler.obtainMessage(statusCode, null);
                        handler.sendMessage(message);
                        if (httpResponse != null) {
                            httpResponse.getEntity().getContent().close();
                        }
                    }
                } catch (ClientProtocolException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                } catch (HttpHostConnectException e) {
                    Message message = handler.obtainMessage(REQUEST_NO_NET, null);
                    handler.sendMessage(message);
                } catch (ConnectTimeoutException e) {
                    Message message = handler.obtainMessage(REQUEST_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (SocketTimeoutException e) {
                    Message message = handler.obtainMessage(SOCKET_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void postByJson(final String url, final JSONObject jsonObject,
                           final IRequestListener iRequestListener) {

        final Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                resultHandler(message.what, message.obj, iRequestListener);
//                iRequestListener.onComplete(message.what, message.obj);
            }
        };

        new Thread() {
            public void run() {
                try {
                    HttpPost httpPost = new HttpPost(url);
                    // 绑定到请求 Entry
                    Log.v(TAG, "postByJson:" + 1);
                    StringEntity entity = new StringEntity(
                            jsonObject.toString(), CHARSET);
                    Log.v(TAG, "postByJson:" + 2);
                    /*
                     * 070 将POST数据放入HTTP请求071
					 */
                    httpPost.setEntity(entity);
                    /*
					 * 074 发出实际的HTTP POST请求075
					 */
//					HttpParams httpParameters = new BasicHttpParams();
//					HttpConnectionParams.setConnectionTimeout(httpParameters,
//							REQUEST_TIMEOUT); // 设置连接超时
//					HttpConnectionParams.setSoTimeout(httpParameters,
//							SO_TIMEOUT); // 设置请求超时
//					httpPost.setParams(httpParameters);
                    Log.v(TAG, "postByJson:" + 3);
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    Log.v(TAG, "postByJson:" + 4);
                    int statusCode = httpResponse.getStatusLine()
                            .getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        byte[] data = inputStreamToByte(httpResponse
                                .getEntity().getContent());
                        // 得到应答的字符串，这也是一个 JSON 格式保存的数据
                        // String retSrc = EntityUtils.toString(httpResponse
                        // .getEntity());
                        // // 生成 JSON 对象
                        // JSONObject result = new JSONObject(retSrc);
                        Message message = handler.obtainMessage(statusCode, data);
                        handler.sendMessage(message);
                    } else {
                        Message message = handler.obtainMessage(statusCode, null);
                        handler.sendMessage(message);
                    }
                    if (httpResponse != null) {
                        httpResponse.getEntity().getContent().close();
                    }
                } catch (ClientProtocolException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                } catch (ConnectTimeoutException e) {
                    Message message = handler.obtainMessage(REQUEST_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (HttpHostConnectException e) {
                    Message message = handler.obtainMessage(REQUEST_NO_NET, null);
                    handler.sendMessage(message);
                } catch (SocketTimeoutException e) {
                    Message message = handler.obtainMessage(SOCKET_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void postMultipart(final String url, final List<NameValuePair> params, final File[] files, final IRequestListener iRequestListener) {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                resultHandler(message.what, message.obj, iRequestListener);
//                iRequestListener.onComplete(message.what, message.obj);
            }
        };

        new Thread() {
            public void run() {
                try {
                    HttpPost httppost = new HttpPost(url);

                    MultipartEntity mpEntity = new MultipartEntity(); //文件传输
                    for (int i = 0; i < files.length; i++) {
                        ContentBody cbFile = new FileBody(files[i]);
                        mpEntity.addPart("file" + i, cbFile); // <input type="file" name="userfile" />  对应的
                    }
                    for (NameValuePair name : params) {
                        StringBody body = new StringBody(name.getValue());
                        mpEntity.addPart(name.getName(), body);
                    }
                    httppost.setEntity(mpEntity);
                    HttpResponse httpResponse = httpClient.execute(httppost);

                    int statusCode = httpResponse.getStatusLine()
                            .getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        byte[] data = inputStreamToByte(httpResponse
                                .getEntity().getContent());
                        Message message = handler.obtainMessage(statusCode, data);
                        handler.sendMessage(message);
                    } else {
                        Message message = handler.obtainMessage(statusCode, null);
                        handler.sendMessage(message);
                        if (httpResponse != null) {
                            httpResponse.getEntity().getContent().close();
                        }
                    }
                } catch (ClientProtocolException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                } catch (HttpHostConnectException e) {
                    Message message = handler.obtainMessage(REQUEST_NO_NET, null);
                    handler.sendMessage(message);
                } catch (ConnectTimeoutException e) {
                    Message message = handler.obtainMessage(REQUEST_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (SocketTimeoutException e) {
                    Message message = handler.obtainMessage(SOCKET_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void postFile(final String url, final File file, final IRequestListener iRequestListener) {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                resultHandler(message.what, message.obj, iRequestListener);
//                iRequestListener.onComplete(message.what, message.obj);
            }
        };

        new Thread() {
            public void run() {

                try {
                    HttpPost httppost = new HttpPost(url);

                    MultipartEntity mpEntity = new MultipartEntity(); //文件传输
                    ContentBody cbFile = new FileBody(file);
                    mpEntity.addPart("file", cbFile); // <input type="file" name="userfile" />  对应的


                    httppost.setEntity(mpEntity);
                    HttpResponse httpResponse = httpClient.execute(httppost);

                    int statusCode = httpResponse.getStatusLine()
                            .getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        byte[] data = inputStreamToByte(httpResponse
                                .getEntity().getContent());
                        Message message = handler.obtainMessage(statusCode, data);
                        handler.sendMessage(message);
                    } else {
                        Message message = handler.obtainMessage(statusCode, null);
                        handler.sendMessage(message);
                    }
                    if (httpResponse != null) {
                        httpResponse.getEntity().getContent().close();
                    }
                } catch (ClientProtocolException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                } catch (HttpHostConnectException e) {
                    Message message = handler.obtainMessage(REQUEST_NO_NET, null);
                    handler.sendMessage(message);
                } catch (ConnectTimeoutException e) {
                    Message message = handler.obtainMessage(REQUEST_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (SocketTimeoutException e) {
                    Message message = handler.obtainMessage(SOCKET_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void get(final String url, final IRequestListener iRequestListener) {

        final Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                resultHandler(message.what, message.obj, iRequestListener);
//                iRequestListener.onComplete(message.what, message.obj);
            }
        };

        new Thread() {
            public void run() {
                super.run();
                try {
                    HttpGet httpGet = new HttpGet(url);
//					HttpParams httpParameters = new BasicHttpParams();
//					HttpConnectionParams.setConnectionTimeout(httpParameters,
//							REQUEST_TIMEOUT); // 设置连接超时
//					HttpConnectionParams.setSoTimeout(httpParameters,
//							SO_TIMEOUT); // 设置请求超时
//					httpGet.setParams(httpParameters);

                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    int statusCode = httpResponse.getStatusLine()
                            .getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        byte[] data = inputStreamToByte(httpResponse
                                .getEntity().getContent());

                        Message message = handler.obtainMessage(statusCode, data);
                        handler.sendMessage(message);
                    } else {
                        Message message = handler.obtainMessage(statusCode, null);
                        handler.sendMessage(message);
                        if (httpResponse != null) {
                            httpResponse.getEntity().getContent().close();
                        }
                    }
                } catch (ClientProtocolException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                } catch (HttpHostConnectException e) {
                    Message message = handler.obtainMessage(REQUEST_NO_NET, null);
                    handler.sendMessage(message);
                } catch (ConnectTimeoutException e) {
                    Message message = handler.obtainMessage(REQUEST_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (SocketTimeoutException e) {
                    Message message = handler.obtainMessage(SOCKET_NET_TIMEOUT, null);
                    handler.sendMessage(message);
                    Log.v(TAG, e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void resultHandler(int statusCode, Object obj, IRequestListener iRequestListener) {
        String msg = "";
        if (statusCode == HttpStatus.SC_OK) {
            msg = "成功";
        } else {
            if (statusCode == REQUEST_NET_TIMEOUT) {
                msg = "网络连接超时";
            } else if (statusCode == SOCKET_NET_TIMEOUT) {
                msg = "服务器响应超时";
            } else if (statusCode == REQUEST_NO_NET) {
                msg = "无网络";
            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                msg = "内部服务器错误 ";
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                msg = "没有找到服务器";
            } else {
                msg = "未知错误:" + statusCode;
            }
        }

        Log.v(TAG, msg + ":" + statusCode);
        iRequestListener.onComplete(statusCode, msg, obj);
    }

    public void setSSLSocket() {
        if (isSSL) {
            return;
        }
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            this.httpClient = new DefaultHttpClient(ccm, params);
            isSSL = true;
        } catch (Exception e) {
            this.httpClient = new DefaultHttpClient();
        }
    }

    // 把数据流转换成字节流
    private byte[] inputStreamToByte(InputStream inStream) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return outStream.toByteArray();
    }

    private String setCookie(Header[] headers) {
        cookieStr = "";
        for (Header header : headers) {
            String headerStr = header.getValue();
            if (headerStr.contains("; ")) {
                cookieStr = cookieStr
                        + headerStr.substring(0, headerStr.indexOf("; ") + 2);
            }
        }
        Log.v("000000", cookieStr);
        return cookieStr;
    }

    public List<NameValuePair> getParams() {
        return params;
    }

    public void setParams(List<NameValuePair> params) {
        this.params = params;
    }

    public interface IRequestListener {
        public void onComplete(int result, String msg, Object obj);
    }

    public class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {

                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
