/**
 * 
 */
package com.hehua.framework.web.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.hehua.Hehua;
import com.hehua.commons.model.CommonMetaCode;
import com.hehua.commons.model.Meta;
import com.hehua.commons.model.ResultView;
import com.hehua.framework.web.model.SuccessMessage;

/**
 * @author zhihua
 *
 */
public class HehuaFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    private static final Log logger = LogFactory.getLog(HehuaFastJsonHttpMessageConverter.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        return super.readInternal(clazz, inputMessage);
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {

        if (logger.isDebugEnabled()) {
            logger.debug("writeInternal " + obj);
        }

        if (obj instanceof ResultView) {

            if (logger.isDebugEnabled()) {
                logger.debug("writeInternalResultView " + obj);
            }

            ResultView<?> result = (ResultView<?>) obj;
            if (result.getMeta().getCode() == CommonMetaCode.Success) {
                super.writeInternal(new SuccessMessage(result.getData()), outputMessage);
            } else {

                JSONObject jsonResponse = new JSONObject();

                JSONObject jsonError = new JSONObject();
                Meta meta = result.getMeta();
                int code = meta.getCode().getCode();
                String message = Hehua.getContext().getMessage(code);
                message = StringUtils.defaultString(message, meta.getCode().getMessage());

                jsonError.put("code", code);
                jsonError.put("message", message);

                jsonResponse.put("error", jsonError);

                super.writeInternal(jsonResponse, outputMessage);
            }
        } else {
            super.writeInternal(obj, outputMessage);
        }
    }

}
