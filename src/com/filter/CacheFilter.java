package com.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ki264 on 2017/6/25.
 */
@WebFilter(
        filterName = "CacheFilter",
        urlPatterns = {"*.jsp", "*.html", "*.do"},
        initParams = {
                @WebInitParam(name = "cacheTime", value = "1000000")
        })
public class CacheFilter implements Filter {

    private ServletContext servletContext;  //Servlet上下文

    private File temporalDir;   //快取檔案夾，使用Tomcat工作目錄

    private long cacheTime = Long.MAX_VALUE;    //快取記憶體時間，設定於Filter的初始化參數中

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;  //request 物件
        HttpServletResponse response = (HttpServletResponse) resp;  //response 物件

        if ("POST".equals(request.getMethod())) {   //如果為 POST，則不經過快取記憶體
            chain.doFilter(req, resp);
            return;
        }

        String uri = request.getRequestURI();   //取得request的URI
        if (uri == null) {
            uri = "";
        }
        uri = uri.replace(request.getContextPath() + "/", "");
        uri = uri.trim().length() == 0 ? "index.jsp" : uri;
        uri = request.getQueryString() == null ? uri : (uri + "?" + request.getQueryString());

        //對應的快取檔案
        File cacheFile = new File(temporalDir, URLEncoder.encode(uri, "utf-8"));
        System.out.println(cacheFile);

        // 如果快取記憶體檔案不存在，或已經超出快取記憶體時間，則請求 Servlet
        if (!cacheFile.exists() || cacheFile.length() == 0 || cacheFile.lastModified() < System.currentTimeMillis() - cacheTime) {

            //自訂的 response ，用於快取記憶體輸出內容
            CacheResponseWrapper cacheResponseWrapper = new CacheResponseWrapper(response);

            chain.doFilter(request, cacheResponseWrapper);

            //將快取記憶體的內容快取檔案
            char[] content = cacheResponseWrapper.getCacheWriter().toCharArray();

            temporalDir.mkdirs();   //建立資料夾
            cacheFile.createNewFile();  //建立新的快取檔案

            //輸出到快取檔案中
            Writer writer = new OutputStreamWriter(new FileOutputStream(cacheFile), "utf-8");
            writer.write(content);
            writer.close();
        }

        //設定請求的 contentType
        String mimeType = servletContext.getMimeType(request.getRequestURI());
        response.setContentType(mimeType);

        //讀取快取檔案的內容，寫入用戶端瀏覽器
        Reader reader = new InputStreamReader(new FileInputStream(cacheFile), "utf-8");
        StringBuffer stringBuffer = new StringBuffer();
        char[] cacheBuffer = new char[1024];
        int length;
        while ((length = reader.read(cacheBuffer)) > -1) {
            stringBuffer.append(cacheBuffer, 0, length);
        }
        reader.close();

        //輸出到用戶端
        response.getWriter().write(stringBuffer.toString());


    }

    public void init(FilterConfig config) throws ServletException {
        temporalDir = (File) config.getServletContext().getAttribute("javax.servlet.context.tempdir");  //Tomcat 工作資料夾
        servletContext = config.getServletContext();    //取得Servlet上下文
        cacheTime = new Long(config.getInitParameter("cacheTime")); //快取記憶體時間
    }

}
