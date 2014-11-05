package com.hehua.framework.web.antispam.csrf;

import com.hehua.framework.web.util.AesCryptUtil;
import com.hehua.framework.web.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * CSRF prevention handler interceptor using Double Submitted Cookies to prevent potential CSRF attacks.
 * <pre>
 * Only applied to POST request created within the profile page by FORM submitting.
 * Limitations:
 * 1.Client browser MUST support cookies.
 * 2.Token should be unique. Or at lease secure.
 * 3.CSRF token name should NOT be changed.
 * 4.Need manually add {@code <input type="hidden" name="CSRFToken" value="${CSRFToken!}"/>} into the html form.
 * 5.Need CSRFToken timeout support.
 * </pre>
 * <pre>
 * User: George
 * Date: 13-1-4
 * Time: 上午10:47
 * </pre>
 */
public abstract class CSRFTokenManager<T> {

    private static final Logger LOG = LoggerFactory.getLogger(CSRFTokenManager.class);
    private static final String HTTP_METHOD = "POST";
    private static final String AJAX_REQUEST_HEADER_NAME = "X-Requested-With";
    private static final String DEFAULT_PASSWORD = "/L*#mv$9023^-T0@";
    private static final String DEFAULT_DELIMITER = "_";
    private static final long DEFAULT_TIMEOUT_IN_MILLISECONDS = 24 * 3600 * 1000;
    public static final String EMPTY_STRING = "";
    /**
     * DO NOT change this. Because this name used in many ftl templates.
     * Be careful when you need to change this.
     */
    public static final String CSRF_TOKEN_NAME = "CSRFToken";
    /**
     * 5-16(inclusive)  characters
     */
    private String password = DEFAULT_PASSWORD;
    private boolean ignoreAjax = false;
    // If the value less than or equals to zero, we think the token never timeout.
    private long timeoutInSeconds = DEFAULT_TIMEOUT_IN_MILLISECONDS;
    // All http method MUST be in uppercase.
    private Set<String> httpMethods = new HashSet<String>();

    // Add the default http method POST
    {
        httpMethods.add(HTTP_METHOD);
    }

    /**
     * Put it into the model for view(ftl) rendering.
     *
     * @param csrfToken    CSRF Token
     * @param modelAndView page model
     * @return append是否成功
     */
    public abstract boolean appendCSRFToken(String csrfToken, T modelAndView);

    public abstract int getLoginUserId(HttpServletRequest request);


    public long getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(long timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public boolean isIgnoreAjax() {
        return ignoreAjax;
    }

    public void setIgnoreAjax(boolean ignoreAjax) {
        this.ignoreAjax = ignoreAjax;
    }

    public Set<String> getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(Set<String> httpMethods) {
        if (null == httpMethods) {
            throw new NullPointerException("httpMethods parameter cannot set to null");
        }
        if (null == this.httpMethods) {
            this.httpMethods = new HashSet<String>(httpMethods.size());
        }
        for (String method : httpMethods) {
            this.httpMethods.add(method.toUpperCase());
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void appendCSRFToken(HttpServletRequest request,
                                HttpServletResponse response,
                                T modelAndView) {
        if (null != modelAndView) {
            // Generate new CSRF token per request.
            appendCSRFToken(generateCSRFToken(request), modelAndView);
        }
    }

    public String generateCSRFToken(HttpServletRequest request) {
        int uid = getLoginUserId(request);
        return generateCSRFToken(uid == 0 ? 0 : uid, password);
    }

    public boolean supportCSRFRequestMethod(String requestMethod) {
        return httpMethods.contains(requestMethod.toUpperCase());
    }

    public boolean verifyCSRFToken(HttpServletRequest request) {
        if (isIgnoreAjax()) {
            // Ignore AJAX request.
            String requestWith = request.getHeader(AJAX_REQUEST_HEADER_NAME);
            if (StringUtils.isNotEmpty(requestWith)) {
                LOG.warn("This is an AJAX request {}, let it go.", request.getRequestURI());
                return true;
            }
        }

        // Only verify CSRF token for the incoming POST request for now.
        if (!supportCSRFRequestMethod(request.getMethod())) {
            LOG.info("====>>>>{} is not a supported CSRF request method.", request.getMethod());
            return true;
        }
        // This is a POST request, need to verify the CSRF token.
        // Get CSRF token from the FORM.
        String formToken = getCSRFTokenInForm(request);
//        int uid = LoginAuthParser.getLoginUidByMis(request);
        int uid = getLoginUserId(request); //获取登录的uid，CSRF只对授权用户进行攻击
        return verifyCSRFToken(uid, formToken, timeoutInSeconds);
    }

    public static String generateCSRFToken(int uid) {
        return generateCSRFToken(uid, DEFAULT_PASSWORD);
    }

    public static boolean verifyCSRFToken(int uid, String formToken) {
        return verifyCSRFToken(uid, formToken, DEFAULT_TIMEOUT_IN_MILLISECONDS);
    }

    private String getCSRFTokenInForm(HttpServletRequest request) {
        return request.getParameter(CSRF_TOKEN_NAME);
    }

    private static String generateCSRFToken(int uid, String password) {
        if (uid <= 0) {
            return EMPTY_STRING;
        }
        return AesCryptUtil.encrypt(uid + DEFAULT_DELIMITER + System.currentTimeMillis(), password);
    }

    private static boolean verifyCSRFToken(int uid, String formToken, long tokenTimeoutInMilliSeconds) {
        if (uid <= 0) {   //未登录不需要验证token
            return true;
        }

        if (StringUtils.isEmpty(formToken)) {//如果用户登录了但是没有token，验证失败
            return false;
        }

        String plainToken = AesCryptUtil.decrypt(formToken, DEFAULT_PASSWORD);
        if (StringUtils.isEmpty(plainToken)) {
            LOG.warn("===>>>>Decrypt form token {} failed for uid {}.", formToken, uid);
            return false;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Plain token is %s of form token %s for uid %d", plainToken, formToken, uid));
        }

        String uidInToken = plainToken.substring(0, plainToken.indexOf(DEFAULT_DELIMITER));
        String createTimeOfToken = plainToken.substring(plainToken.indexOf(DEFAULT_DELIMITER) + 1);

        if (!StringUtil.isLong(uidInToken) || !StringUtil.isLong(createTimeOfToken)) {
            LOG.warn(String.format("===>>>>It's illegal plain token %s of form token %s for uid %d",plainToken,formToken,uid));
            return false;
        }

        long createTimeInMilliSeconds = Long.parseLong(createTimeOfToken);
        long currentTimeMillis = System.currentTimeMillis();
        return uidInToken.equals(String.valueOf(uid))
                && ((currentTimeMillis > createTimeInMilliSeconds)
                && ((currentTimeMillis - createTimeInMilliSeconds) <= tokenTimeoutInMilliSeconds));
    }
}
