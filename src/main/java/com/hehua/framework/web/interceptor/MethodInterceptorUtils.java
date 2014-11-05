/**
 * 
 */
package com.hehua.framework.web.interceptor;

import java.lang.annotation.Annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

public class MethodInterceptorUtils {

    private static final Log logger = LogFactory.getLog(MethodInterceptorUtils.class);

    public static <A extends Annotation> A getAnnotaion(Object handler, Class<A> annotationClass) {
        if (!(handler instanceof HandlerMethod)) {
            return null;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        A result = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotationClass);
        if (result == null) {
            result = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), annotationClass);
        }
        return result;
    }
}
