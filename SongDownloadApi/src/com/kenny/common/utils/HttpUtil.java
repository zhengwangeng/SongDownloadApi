package com.kenny.common.utils;

import java.io.*;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import java.util.*;
import java.util.Map.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.*;
import org.apache.commons.lang.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.config.*;
import org.apache.http.conn.socket.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.*;
import org.apache.http.util.*;

import com.kenny.vo.output.ResponseEntity;

/**
 * http,https的请求
 * @author Kenny zheng
 * @date 2014-03-6 下午2:10:19
 */
public class HttpUtil {
    
	/** httpClient对象	*/
    private static CloseableHttpClient httpClient;
    
    /** 默认编码：UTF-8	*/
    private static final String DEFAULT_CHARSET = "UTF-8";
    
    static {
    	//解决jdk1.7 javax.net.ssl.SSLProtocolException: handshake alert:  unrecognized_name 
    	//这个问题是关系到SNI扩展名的JSSE客户端1.7。 解决的办法是禁用SNI发送记录
//    	System.setProperty ("jsse.enableSNIExtension", "false");
    	TrustManager[] tm = { new AnyX509TrustManager() };  
        SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("SSL");
//			sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
    			.register("http", PlainConnectionSocketFactory.INSTANCE)
    			.register("https", new SSLConnectionSocketFactory(sslContext))
    			.build();
    	
    	PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        
        cm.setMaxTotal(100);
        
        RequestConfig defaultRequestConfig = RequestConfig.custom().
//        		setSocketTimeout(3000).setConnectTimeout(3000)	//设置超时
                setCookieSpec(CookieSpecs.IGNORE_COOKIES)	//忽略cookie
        		//这个配置比较坑爹，百度地图api需要启用，否则会有wran信息
        		//模拟微信登录需要禁用该配置
                .build();
        
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
    }
    
    /**
     * 发送get请求.</br>
     * @param @param uri
     * @param @return
     * @return String    返回请求uri的响应字符串
     * @throws
     */
    public static ResponseEntity get(String uri){
    	ResponseEntity baseOutPut = new ResponseEntity();
    	HttpGet httpGet = new HttpGet(uri);
    	CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
	        try {
				HttpEntity httpEntity = httpResponse.getEntity();
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				baseOutPut.setCode(statusCode);
				if(httpEntity != null){
					baseOutPut.setBody(EntityUtils.toString(httpEntity,DEFAULT_CHARSET));
					return baseOutPut;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (httpResponse != null) {
				    // 关闭连接
					httpResponse.close();
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(httpGet!=null){
				//释放链接
				httpGet.releaseConnection();
			}
		}
        return null;
    }
    
    /**
     * 发送get请求.</br>
     * @param @param uri
     * @param @param params 参数
     * @param @return
     * @return String    返回请求uri的响应字符串
     * @throws
     */
    public static ResponseEntity get(String uri, Map<String, String> params) {
        return get(initGetParams(uri, params));
    }
    
    /**
     * 发送get请求..</br>
     * @param @param uri
     * @param @param header 头信息设置参数map
     * @param @return
     * @return String    返回请求uri的响应字符串 
     * @throws
     */
    public static ResponseEntity getByHeader(String uri, Map<String, String> header){
    	ResponseEntity baseOutPut = new ResponseEntity();
    	HttpGet httpGet = new HttpGet(uri);
    	CloseableHttpResponse httpResponse = null;
    	for (Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey();
	        String value = entry.getValue();
	        httpGet.setHeader(key, value);
		}
    	try {
			httpResponse = httpClient.execute(httpGet);
	        try {
				HttpEntity httpEntity = httpResponse.getEntity();//获取body
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				baseOutPut.setCode(statusCode);
				if(httpEntity != null){
					baseOutPut.setBody(EntityUtils.toString(httpEntity,DEFAULT_CHARSET));
					return baseOutPut;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (httpResponse != null) {
				    // 关闭连接
					httpResponse.close();
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(httpGet!=null){
				//释放链接
				httpGet.releaseConnection();
			}
		}
        return null;
    	
    }
    
    
    
    /**
     * 发送get请求.</br>
     * @param @param uri
     * @param @return
     * @return String    返回请求uri的响应HttpResponse
     * @throws
     */
    public static CloseableHttpResponse getResponse(String uri){
    	HttpGet httpGet = new HttpGet(uri);
    	CloseableHttpResponse httpResponse = null;
    	
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(httpGet!=null){
				//释放链接
				httpGet.releaseConnection();
			}
		}
        return httpResponse;
    }
    
    /**
     * 发送Post请求.</br>
     * @param @param uri
     * @param @param params	参数
     * @param @return
     * @return String    返回请求uri的响应字符串
     * @throws
     */
    public static ResponseEntity post(String uri, String params){
    	ResponseEntity baseOutPut = new ResponseEntity();
    	HttpPost httpPost = new HttpPost(uri);
    	CloseableHttpResponse httpResponse = null;
		try {
			httpPost.setEntity(new StringEntity(params,DEFAULT_CHARSET));
			httpResponse = httpClient.execute(httpPost);
	        try {
				HttpEntity httpEntity = httpResponse.getEntity();
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				baseOutPut.setCode(statusCode);
				if(httpEntity != null){
					baseOutPut.setBody(EntityUtils.toString(httpEntity,DEFAULT_CHARSET));
					return baseOutPut;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (httpResponse != null) {
				    // 关闭连接
					httpResponse.close();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(httpPost!=null){
				httpPost.releaseConnection();
			}
		}
        return null;
    }
    
    /**
     * 发送Post请求..</br>
     * @param @param uri
     * @param @param header 头信息设置参数map
     * @param @param nameValuePairs 请求nvp
     * @param @return
     * @return String    返回请求uri的响应字符串 
     * @throws
     */
    public static ResponseEntity post(String uri, Map<String, String> header,String jsonBodyParams ){
    	ResponseEntity baseOutPut = new ResponseEntity();
    	HttpPost httpPost = new HttpPost(uri);
    	CloseableHttpResponse httpResponse = null;
    	
		for (Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey();
	        String value = entry.getValue();
	        httpPost.setHeader(key, value);
		}
		try {
			httpPost.setEntity(new StringEntity(jsonBodyParams,DEFAULT_CHARSET));
			httpResponse = httpClient.execute(httpPost);
	        try {
				HttpEntity httpEntity = httpResponse.getEntity();
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				baseOutPut.setCode(statusCode);
				if(httpEntity != null){
					baseOutPut.setBody(EntityUtils.toString(httpEntity,DEFAULT_CHARSET));
					return baseOutPut;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (httpResponse != null) {
				    // 关闭连接
					httpResponse.close();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(httpPost!=null){
				httpPost.releaseConnection();
			}
		}
        return null;
    }
    
    /**
     * 发送Post请求..</br>
     * @param @param uri
     * @param @param header 头信息设置参数map
     * @param @param nameValuePairs 请求nvp
     * @param @return
     * @return String    返回请求uri的响应字符串 
     * @throws
     */
    public static ResponseEntity post(String uri, Map<String, String> header,List<NameValuePair> nameValuePairs){
    	ResponseEntity baseOutPut = new ResponseEntity();
    	HttpPost httpPost = new HttpPost(uri);
    	CloseableHttpResponse httpResponse = null;
    	
		for (Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey();
	        String value = entry.getValue();
	        httpPost.setHeader(key, value);
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,DEFAULT_CHARSET));
			httpResponse = httpClient.execute(httpPost);
	        try {
				HttpEntity httpEntity = httpResponse.getEntity();
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				baseOutPut.setCode(statusCode);
				if(httpEntity != null){
					baseOutPut.setBody(EntityUtils.toString(httpEntity,DEFAULT_CHARSET));
					return baseOutPut;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (httpResponse != null) {
				    // 关闭连接
					httpResponse.close();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(httpPost!=null){
				httpPost.releaseConnection();
			}
		}
        return null;
    }
    
    /**
     * 发送get请求..</br>
     * @param @param uri
     * @param @param header 头信息设置参数map
     * @param @return
     * @return String    返回请求uri的响应字符串 
     * @throws
     */
    public static ResponseEntity post(String uri, Map<String, String> header){
    	ResponseEntity baseOutPut = new ResponseEntity();
    	HttpPost httpPost = new HttpPost(uri);
    	CloseableHttpResponse httpResponse = null;
    	for (Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey();
	        String value = entry.getValue();
	        httpPost.setHeader(key, value);
		}
    	try {
			httpResponse = httpClient.execute(httpPost);
	        try {
				HttpEntity httpEntity = httpResponse.getEntity();
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				baseOutPut.setCode(statusCode);
				if(httpEntity != null){
					baseOutPut.setBody(EntityUtils.toString(httpEntity,DEFAULT_CHARSET));
					return baseOutPut;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (httpResponse != null) {
				    // 关闭连接
					httpResponse.close();
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(httpPost!=null){
				//释放链接
				httpPost.releaseConnection();
			}
		}
        return null;
    	
    }
    
    /**
     * 初始化get方式的请求参数.</br>
     * @param @param url
     * @param @param params
     * @param @return
     * @return String    返回url参数拼接字符串
     * @throws
     */
    public static String initGetParams(String url, Map<String, String> params){
        if (null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            sb.append("?");
        } else {
            sb.append("&");
        }
        boolean first = true;
        for (Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=");
            if (StringUtils.isNotEmpty(value)) {
            	sb.append(value);
            }
        }
        return sb.toString();
    }
    
    /**
     * 发送Delete请求..</br>
     * @param @param uri
     * @param @param header 头信息设置参数map
     * @param @param nameValuePairs 请求nvp
     * @param @return
     * @return String    返回请求uri的响应字符串 
     * @throws
     */
    public static ResponseEntity delete(String uri, Map<String, String> header){
    	ResponseEntity baseOutPut = new ResponseEntity();
    	HttpDelete httpDelete = new HttpDelete(uri);
    	CloseableHttpResponse httpResponse = null;
    	for (Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey();
	        String value = entry.getValue();
	        httpDelete.setHeader(key, value);
		}
		try {
			httpResponse = httpClient.execute(httpDelete);
	        try {
				HttpEntity httpEntity = httpResponse.getEntity();
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				baseOutPut.setCode(statusCode);
				if(httpEntity != null){
					baseOutPut.setBody(EntityUtils.toString(httpEntity,DEFAULT_CHARSET));
					return baseOutPut;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (httpResponse != null) {
				    // 关闭连接
					httpResponse.close();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(httpDelete!=null){
				httpDelete.releaseConnection();
			}
		}
        return null;
    }
    
    /**
     * 发送Put请求..</br>
     * @param @param uri
     * @param @param header 头信息设置参数map
     * @param @param nameValuePairs 请求nvp
     * @param @return
     * @return String    返回请求uri的响应字符串 
     * @throws
     */
    public static ResponseEntity put(String uri, Map<String, String> header,String jsonBodyParams){
    	ResponseEntity baseOutPut = new ResponseEntity();
    	HttpPut httpPut = new HttpPut(uri);
    	CloseableHttpResponse httpResponse = null;
    	
		for (Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey();
	        String value = entry.getValue();
	        httpPut.setHeader(key, value);
		}
		try {
			httpPut.setEntity(new StringEntity(jsonBodyParams,DEFAULT_CHARSET));
			httpResponse = httpClient.execute(httpPut);
	        try {
				HttpEntity httpEntity = httpResponse.getEntity();
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				baseOutPut.setCode(statusCode);
				if(httpEntity != null){
					baseOutPut.setBody(EntityUtils.toString(httpEntity,DEFAULT_CHARSET));
					return baseOutPut;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (httpResponse != null) {
				    // 关闭连接
					httpResponse.close();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(httpPut!=null){
				httpPut.releaseConnection();
			}
		}
        return null;
    }
    
    /**
     * 下载资源.</br>
     * @param @param uri 文件下载的url
     * @param @param baseFilePath 保存的父路径
     * @param @param singer 演唱者
     * @param @return
     * @param @throws IOException
     * @return boolean    返回类型
     * @throws
     */
    public static boolean download(String uri,String baseFilePath,String singer,String saveFileName) throws IOException{
    	HttpGet httpGet = new HttpGet(uri);
    	CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
	        try {
				HttpEntity httpEntity = httpResponse.getEntity();
				if(httpEntity != null){
					BufferedInputStream bis = new BufferedInputStream(
							httpEntity.getContent());
					
					Header header = httpEntity.getContentType();
					byte [] gif = IOUtils.toByteArray(bis);
				 	FileUtils.writeByteArrayToFile(new File(baseFilePath+File.separator +singer,saveFileName),gif);
				 	IOUtils.closeQuietly(bis);
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			} finally{
				if (httpResponse != null) {
				    // 关闭连接
					httpResponse.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(httpGet!=null){
				httpGet.releaseConnection();
			}
		}
        return false;
    }
    
}
