package com.hehua.framework.web.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class LogableRequest extends HttpServletRequestWrapper {

    protected static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    protected static final String FORM_CHARSET = "UTF-8";

    private static final String METHOD_POST = "POST";

    private byte[] body;

    private long id;

    /**
     * @param servletRequest
     */
    public LogableRequest(long id, HttpServletRequest servletRequest) {
        super(servletRequest);
        try {
            body = IOUtils.toByteArray(getBody());
        } catch (IOException e) {
            e.printStackTrace();
            body = new byte[0];
        }
        this.id = id;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new DelegatingServletInputStream(new ByteArrayInputStream(body));
    }

    public InputStream getBody() throws IOException {
        if (isFormPost(this)) {
            return getBodyFromServletRequestParameters((HttpServletRequest) this.getRequest());
        } else {
            return this.getRequest().getInputStream();
        }
    }

    private boolean isFormPost(HttpServletRequest request) {
        return (request.getContentType() != null
                && request.getContentType().contains(FORM_CONTENT_TYPE) && METHOD_POST
                    .equalsIgnoreCase(request.getMethod()));
    }

    private InputStream getBodyFromServletRequestParameters(HttpServletRequest request)
            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(bos, FORM_CHARSET);

        Map<String, String[]> form = request.getParameterMap();
        for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
            String name = nameIterator.next();
            List<String> values = Arrays.asList(form.get(name));
            for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext();) {
                String value = valueIterator.next();
                writer.write(URLEncoder.encode(name, FORM_CHARSET));
                if (value != null) {
                    writer.write('=');
                    writer.write(URLEncoder.encode(value, FORM_CHARSET));
                    if (valueIterator.hasNext()) {
                        writer.write('&');
                    }
                }
            }
            if (nameIterator.hasNext()) {
                writer.append('&');
            }
        }
        writer.flush();

        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("##" + id + "## ").append("Request URL: ")
                .append(getRequestURL().toString());
        if (StringUtils.isNotBlank(getQueryString())) {
            stringBuilder.append("?").append(getQueryString());
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("##" + id + "## ").append("Request Method: ").append(getMethod())
                .append("\r\n");

        Enumeration<String> headerNames = getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> values = getHeaders(headerName);
            while (values.hasMoreElements()) {
                String value = values.nextElement();

                stringBuilder.append("##" + id + "## ").append("Request Header: ")
                        .append(headerName).append(" = ").append(value).append("\r\n");
            }
        }

        String string;
        try {
            string = IOUtils.toString(getBody());
        } catch (IOException e) {
            string = "";
        }

        stringBuilder.append("##" + id + "## ").append("Request Body: ").append(string)
                .append("\r\n");
        return stringBuilder.toString();

    }

    class DelegatingServletInputStream extends ServletInputStream {

        private final InputStream sourceStream;

        /**
         * Create a DelegatingServletInputStream for the given source
         * stream.
         * 
         * @param sourceStream the source stream (never <code>null</code>)
         */
        public DelegatingServletInputStream(InputStream sourceStream) {
            this.sourceStream = sourceStream;
        }

        /**
         * Return the underlying source stream (never <code>null</code>).
         */
        public final InputStream getSourceStream() {
            return this.sourceStream;
        }

        @Override
        public int read() throws IOException {
            return this.sourceStream.read();
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.sourceStream.close();
        }

    }

}
