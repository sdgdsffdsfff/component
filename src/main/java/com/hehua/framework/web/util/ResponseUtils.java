/**
 * 
 */
package com.hehua.framework.web.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.hehua.commons.model.CommonMetaCode;
import com.hehua.commons.model.ResultView;
import com.hehua.framework.web.model.ErrorMessage;
import com.hehua.framework.web.model.SuccessMessage;

public class ResponseUtils {

    private static final Log logger = LogFactory.getLog(ResponseUtils.class);

    public static void output(HttpServletResponse response, ResultView<?> resultView) {
        if (resultView.getMeta().getCode() == CommonMetaCode.Success) {
            output(response, JSON.toJSONString(new SuccessMessage(resultView.getData())));
        } else {
            output(response, JSON.toJSONString(new ErrorMessage(resultView.getMeta())));
        }
    }

    /**
     * 输出指定字符串
     * 
     * @param response
     * @param text
     */
    public static void output(HttpServletResponse response, String text) {
        try {
            if (StringUtils.startsWith(text, "{") && StringUtils.endsWith(text, "}")) {
                logger.trace("set content type to json:" + text);
                response.setContentType("application/json; charset=utf-8");
            } else {
                response.setContentType("text/html;charset=utf-8");
            }
            PrintWriter pw = response.getWriter();
            // add by w.vela，如果使用mockResponse的话，这里会返回null，为了测试方便，这里做一个patch
            if (pw == null) {
                return;
            }
            pw.print(text);
            pw.flush();
            pw.close();
        } catch (Exception e) {
            logger.error("output(HttpServletResponse, String)", e); //$NON-NLS-1$
        }
    }

}
