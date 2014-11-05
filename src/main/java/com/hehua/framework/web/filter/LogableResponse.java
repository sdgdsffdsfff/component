package com.hehua.framework.web.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class LogableResponse extends HttpServletResponseWrapper {

    private final long id;

    private List<Cookie> cookies = new ArrayList<>();

    private CopyOutputStream outputStream;

    private CopyPrintWriter writer;

    /**
     * @param response
     */
    public LogableResponse(long id, HttpServletResponse response) {
        super(response);
        this.id = id;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new CopyPrintWriter(super.getWriter());
        }
        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new CopyOutputStream(super.getOutputStream());
        }
        return outputStream;
    }

    @Override
    public void addCookie(Cookie cookie) {
        super.addCookie(cookie);
        cookies.add(cookie);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (!cookies.isEmpty()) {

            stringBuilder.append("##" + id + "## ").append("Response Cookies: ");

            for (int i = 0; i < cookies.size(); i++) {
                Cookie cookie = cookies.get(i);
                if (i > 0) {
                    stringBuilder.append(",");
                }

                stringBuilder.append(cookie.getName()).append(":").append(cookie.getValue())
                        .append("|").append(cookie.getDomain()).append("|")
                        .append(cookie.getMaxAge());
            }
            stringBuilder.append("\r\n");
        }

        String string = "";
        if (writer != null) {
            string = writer.getCopy();
        }

        if (outputStream != null) {
            string = outputStream.getCopy();
        }

        stringBuilder.append("##" + id + "## ").append("Response Body: ").append(string)
                .append("\r\n");
        return stringBuilder.toString();
    }

}
