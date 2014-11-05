/**
 * 
 */
package com.hehua.framework.web.model;

/**
 * @author zhihua
 *
 */
public class SuccessMessage {

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * @param data
     */
    public SuccessMessage(Object data) {
        super();
        this.data = data;
    }

}
