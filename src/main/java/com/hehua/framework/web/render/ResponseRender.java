/**
 * 
 */
package com.hehua.framework.web.render;

import com.alibaba.fastjson.JSONObject;
import com.hehua.commons.exception.BusinessException;
import com.hehua.commons.model.CommonMetaCode;
import com.hehua.commons.model.MetaCode;

/**
 * @author zhihua
 *
 */
public class ResponseRender {

    public static JSONObject renderResponse(Object result) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", result);
        return jsonObject;
    }

    public static JSONObject renderError(Exception exception) {
        JSONObject result = new JSONObject();
        MetaCode errorCode = CommonMetaCode.Error;
        if (exception instanceof BusinessException) {
            errorCode = ((BusinessException) exception).getCode();
        }

        JSONObject errorJson = new JSONObject();
        errorJson.put("code", errorCode.getCode());
        errorJson.put("message", errorCode.getMessage());

        result.put("error", errorJson);
        return result;
    }

}
