package com.filter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by ki264 on 2017/6/25.
 */
public class CacheResponseWrapper extends HttpServletResponseWrapper {

    private CharArrayWriter cacheWriter = new CharArrayWriter();

    public CacheResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(cacheWriter);
    }

    @Override
    public void flushBuffer() throws IOException {
        cacheWriter.flush();
    }

    public void finishResponse() throws IOException {
        cacheWriter.close();
    }


    public CharArrayWriter getCacheWriter() {
        return cacheWriter;
    }

    public void setCacheWriter(CharArrayWriter cacheWriter) {
        this.cacheWriter = cacheWriter;
    }
}
