/**
 * 
 */
package com.hehua.framework.web.model;

import com.hehua.commons.model.Meta;

/**
 * @author zhihua
 *
 */
public class ErrorMessage {

    private Error error;

    public ErrorMessage(Meta meta) {
        this(meta.getCode().getCode(), meta.getCode().getMessage());
    }

    public ErrorMessage(int code, String message) {
        this.error = new Error(code, message);
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public static class Error {

        private int code;

        private String message;

        /**
         * @param code
         * @param message
         */
        public Error(int code, String message) {
            super();
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
