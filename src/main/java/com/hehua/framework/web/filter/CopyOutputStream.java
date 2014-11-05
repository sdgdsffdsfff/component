/**
 * 
 */
package com.hehua.framework.web.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author zhihua
 *
 */
public class CopyOutputStream extends ServletOutputStream {

    private OutputStream delegeted;

    private ByteArrayOutputStream copy = new ByteArrayOutputStream();

    /**
     * @param delegeted
     */
    public CopyOutputStream(OutputStream delegeted) {
        super();
        this.delegeted = delegeted;
    }

    @Override
    public void write(int b) throws IOException {
        delegeted.write(b);
        copy.write(b);
    }

    public String getCopy() {
        try {
            return IOUtils.toString(copy.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
