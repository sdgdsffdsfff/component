package com.hehua.framework.web.antispam.param;

import com.hehua.framework.web.annotation.XssParamFilter;
import com.hehua.framework.web.antispam.ParamXssStripper;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

/**
 * Created by hesheng on 14-10-3.
 */
public class XssMethodHandler implements HandlerMethodArgumentResolver {
    private static final Logger logger = LoggerFactory.getLogger(XssMethodHandler.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation annotation : annotations) {
            if (XssParamFilter.class.isInstance(annotation))
                return true;
        }
        return false;

    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Class<?> type = parameter.getParameterType();
        String paramName = parameter.getParameterName();
        if (String.class.isAssignableFrom(type)) {
            String paramValue = request.getParameter(paramName);
            return ParamXssStripper.filter(paramValue);
        } else if (isStringCollection(parameter)) {
            String[] values = request.getParameterValues(paramName);
            if (ArrayUtils.isNotEmpty(values)) {

                String[] filterArray = new String[values.length];
                for (int i = 0; i <= values.length; i++) {
                    filterArray[i] = ParamXssStripper.filter(values[i]);
                }
                return filterArray;
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("paramName type is not String or Array[String] by class:" + type);
        }

        return null;
    }

    private boolean isStringCollection(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        if (Collection.class.equals(paramType) || List.class.isAssignableFrom(paramType)){
            Class<?> valueType = GenericCollectionTypeResolver.getCollectionParameterType(parameter);
            if (valueType != null && valueType.equals(String.class)) {
                return true;
            }
        }
        return false;
    }
}
