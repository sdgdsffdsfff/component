package com.hehua.framework.web.antispam;

/**
 * autor hewenjerry
 */

import org.apache.commons.lang.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 *     对于标准的Servlet接口重新实现一次，只要的目的是过滤请求的参数
 * <p/>
 */
public class StripXssRequestWrapper extends HttpServletRequestWrapper
        implements HttpServletRequest {

    private Map requestMap; // 线程安全

    public StripXssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public String getParameter(String name) {
        checkRequestMap();
        String values[] = (String[]) requestMap.get(name);
        return ArrayUtils.isNotEmpty(values) ? values[0] : null;
    }

    public Map getParameterMap() {
        checkRequestMap();
        return requestMap;
    }

    public Enumeration getParameterNames() {
        return super.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        checkRequestMap();
        return (String[]) requestMap.get(name);
    }

    private void checkRequestMap() {
        if (requestMap == null) {
            requestMap = buildRequestMap();
            // 这样做的目的是防止意外参数变动，程序再次发起请求攻击
        } else if (requestMap.size() != super.getParameterMap().size()) {
            rebuildNewParam();
        }
    }

    private Map buildRequestMap() {
        Map filteredMap = new HashMap();
        Map originalMap = super.getParameterMap();
        if (originalMap != null && !originalMap.isEmpty()) {
            String name;
            String originalValues[];
            for (Iterator iterator = originalMap.entrySet().iterator();
                                iterator.hasNext();
                                filteredMap.put(name, filterXSS(originalValues))) {
                Object o = iterator.next();
                java.util.Map.Entry entry = (java.util.Map.Entry) o;
                name = (String) entry.getKey();
                originalValues = (String[]) entry.getValue();
            }

        }
        return filteredMap;
    }

    private void rebuildNewParam() {
        Map originalMap = super.getParameterMap();
        if (originalMap != null && !originalMap.isEmpty()) {
            Iterator i$ = super.getParameterMap().entrySet().iterator();
            do {
                if (!i$.hasNext())
                    break;
                Object paramPair = i$.next();
                java.util.Map.Entry entry = (java.util.Map.Entry) paramPair;
                String name = (String) entry.getKey();
                String values[] = (String[]) entry.getValue();
                if (!requestMap.containsKey(name))
                    requestMap.put(name, filterXSS(values));
            } while (true);
        }
    }

    private String[] filterXSS(String src[]) {
        if (src == null || src.length == 0)
            return null;
        String filteredValues[] = new String[src.length];
        int index = 0;
        String arr[] = src;
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            String originalValue = arr[i];
            filteredValues[index++] = ParamXssStripper.filter(originalValue);
        }

        return filteredValues;
    }

}
