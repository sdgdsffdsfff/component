package com.hehua.framework.web.interceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hehua.framework.web.model.UserAgent;

public class UserAgentInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String USER_AGENT_KEY = "gUserAgent";

    private final Pattern WEBKIT = Pattern.compile("AppleWebKit/([\\d.]+)");

    private final Pattern CHROME = Pattern.compile("Chrome/([\\d.]+)");

    private final Pattern SAFARI = Pattern.compile("/([\\d.]+) Safari");

    private final Pattern WEBKIT_MOBILE = Pattern
            .compile("(NokiaN[^/]*|Android\\s\\d\\.\\d|webOS\\/\\d\\.\\d)"); // TODO 验证这个正则表达式

    private float parseVersion(String num) {
        if (num != null) {
            num = num.replaceAll("[a-zA-Z]", ""); // 处理7.0b这样的版本号，去掉b
        }
        int firstDot = StringUtils.indexOf(num, ".");
        if ((firstDot != -1) && (firstDot != (num.length() - 1))) {
            String first = StringUtils.substring(num, 0, firstDot + 1);
            String last = StringUtils.substring(num, firstDot + 1);
            num = first + StringUtils.replace(last, ".", "");
        }

        return NumberUtils.toFloat(num);
    }

    private void processWebkitSeries(UserAgent userAgent, String ua) {
        // WebKit
        Matcher m = WEBKIT.matcher(ua);
        if (m.find()) {
            userAgent.setWebkitCore(true);
            userAgent.setCoreVersion(parseVersion(m.group(1)));

            // chrome?
            m = CHROME.matcher(ua);
            if (m.find()) {
                userAgent.setChrome(true);
                userAgent.setVersion(parseVersion(m.group(1)));
            }

            // sarari?
            if (!userAgent.isChrome()) {
                m = SAFARI.matcher(ua);
                if (m.find()) {
                    userAgent.setSafari(true);
                    userAgent.setVersion(parseVersion(m.group(1)));
                }
            }

            // webkit mobile?
            m = WEBKIT_MOBILE.matcher(ua);
            if (m.find()) {
                userAgent.setMobile(m.group(1).toLowerCase());
            } else {
                if (StringUtils.contains(ua, "Mobile")) {
                    userAgent.setMobile("apple");
                }
            }

            // Android, iPhone, iPad?
            processMobile(userAgent, ua);

        }
    }

    private void processMobile(UserAgent userAgent, String ua) {
        if (StringUtils.containsIgnoreCase(ua, "Android")) {
            userAgent.setAndroid(true);
        } else if (StringUtils.containsIgnoreCase(ua, "iPad")) {
            userAgent.setIpad(true);
        } else if (StringUtils.containsIgnoreCase(ua, "iPhone")) {
            userAgent.setIphone(true);
        }
    }

    private Pattern PRESTO = Pattern.compile("Presto/([\\d.]*)");

    private Pattern OPERA = Pattern.compile("Opera/([\\d.]*)");

    private Pattern OPERA2 = Pattern.compile("Opera/.* Version/([\\d.]*)");

    private Pattern OPERA_MINI = Pattern.compile("(Opera Mini[^;]*)");

    private Pattern OPERA_MOBILE = Pattern.compile("(Opera Mobi[^;]*)");

    private void processPrestoSeries(UserAgent userAgent, String ua) {
        // Presto
        Matcher m = PRESTO.matcher(ua);
        if (m.find()) {
            String v = m.group(1);
            userAgent.setPrestoCore(true);
            userAgent.setCoreVersion(parseVersion(v));

            m = OPERA.matcher(ua);
            if (m.find()) {
                userAgent.setOpera(true);
                userAgent.setVersion(parseVersion(m.group(1)));

                m = OPERA2.matcher(ua);
                if (m.find()) {
                    userAgent.setVersion(parseVersion(m.group(1)));
                }

                m = OPERA_MINI.matcher(ua);
                if (m.find()) {
                    userAgent.setMobile(m.group(1).toLowerCase());
                }
                m = OPERA_MOBILE.matcher(ua);
                if (m.find()) {
                    userAgent.setMobile(m.group(1).toLowerCase());
                }
            }
        }
    }

    private Pattern MSIE = Pattern.compile("MSIE\\s([^;]*)");

    private Pattern TRIDENT = Pattern.compile("Trident/([\\d.]*)");

    private void processIe(UserAgent userAgent, String ua) {
        Matcher m = MSIE.matcher(ua);
        if (m.find()) {
            userAgent.setTridentCore(true);
            userAgent.setCoreVersion(0.1f);
            userAgent.setIe(true);
            userAgent.setVersion(parseVersion(m.group(1)));
            //windows phone
            if (StringUtils.containsIgnoreCase(ua, "Windows Phone")) {
                userAgent.setWinPhone(true);
            }
            m = TRIDENT.matcher(ua);
            if (m.find()) {
                userAgent.setCoreVersion(parseVersion(m.group(1)));
            }
        }
    }

    private Pattern GECKO_VERSION = Pattern.compile("rv:([\\d.]*)");

    private Pattern FIREFOX = Pattern.compile("Firefox/([\\d.]*)");

    private void processGecko(UserAgent userAgent, String ua) {
        if (StringUtils.contains(ua, "Gecko")) {
            userAgent.setGeckoCore(true);

            Matcher m = GECKO_VERSION.matcher(ua);
            if (m.find()) {
                userAgent.setCoreVersion(parseVersion(m.group(1)));
            }
            m = FIREFOX.matcher(ua);
            if (m.find()) {
                userAgent.setFirefox(true);
                userAgent.setVersion(parseVersion(m.group(1)));
            }
        }

    }

    public void processOthers(UserAgent userAgent, String ua) {
        if (StringUtils.contains(ua, "MicroMessenger")) {
            userAgent.setMicroMessenger(true);
        }
        if (StringUtils.contains(ua, "Renren Share Slurp 1.0")) {
            userAgent.setRenrenShareSlurp(true);
        }
    }

    private void setUserAgent(HttpServletRequest request, HttpServletResponse response) {
        String ua = request.getHeader("User-Agent");
        UserAgent userAgent = parseUserAgent(ua);
        request.setAttribute(USER_AGENT_KEY, userAgent);
    }

    private UserAgent parseUserAgent(String ua) {
        UserAgent userAgent = new UserAgent();
        if (StringUtils.isNotBlank(ua)) {
            processWebkitSeries(userAgent, ua);
            if (!userAgent.isWebkitCore()) {
                processPrestoSeries(userAgent, ua);

                if (!userAgent.isPrestoCore()) {
                    processIe(userAgent, ua);

                    if (!userAgent.isTridentCore()) {
                        processGecko(userAgent, ua);
                    }
                }
            }
            // os
            if (StringUtils.contains(ua, "Macintosh")) {
                userAgent.setMacOs(true);
            } else if (StringUtils.contains(ua, "Windows")) {
                userAgent.setWindowsOs(true);
            } else if (StringUtils.contains(ua, "Linux")) {
                userAgent.setLinuxOs(true);
            }

            processWebpSupport(userAgent, ua);
            processOthers(userAgent, ua);
        }
        return userAgent;
    }

    /**
     * @param userAgent
     * @param ua
     */
    private void processWebpSupport(UserAgent userAgent, String ua) {
        if (userAgent.isChrome() && (userAgent.getVersion() >= 23.0)) {
            userAgent.setSupportWebp(true);
        } else if (userAgent.isOpera() && (userAgent.getVersion() >= 12.0)) {
            userAgent.setSupportWebp(true);
        }
    }

    public static void main(String[] args) {
        String[] uaList = { //
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/18.6.872.0 Safari/535.2 UNTRUSTED/1.0 3gpp-gba UNTRUSTED/1.0", //
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.12 Safari/535.11", //
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.36 Safari/535.7", //
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.186 Safari/535.1", //
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.814.0 Safari/535.1", //
                "Opera/9.80 (Windows NT 6.1; U; es-ES) Presto/2.9.181 Version/12.00", //
                "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; de) Presto/2.9.168 Version/11.52", //
                "Opera/9.80 (Windows NT 5.1; U;) Presto/2.7.62 Version/11.01", //
                "Opera/9.80 (Windows NT 5.1; U; cs) Presto/2.7.62 Version/11.01", //
                "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.13) Gecko/20101213 Opera/9.80 (Windows NT 6.1; U; zh-tw) Presto/2.7.62 Version/11.01", //
                "Mozilla/5.0 (Windows NT 6.1; U; nl; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6 Opera 11.01", //
                "Mozilla/5.0 (Windows NT 6.1; U; de; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6 Opera 11.01", //
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; de) Opera 11.01", //
                "Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0", //
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", //
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)", //
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)", //
                "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)", //
                "Mozilla/1.22 (compatible; MSIE 10.0; Windows 3.1)", //
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)", //
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; MS-RTC LM 8; .NET4.0C; .NET4.0E; Zune 4.7)", //
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; MS-RTC LM 8)", //
                "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.40607)", //
                "Mozilla/4.79 [en] (compatible; MSIE 7.0; Windows NT 5.0; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 1.1.4322; .NET CLR 3.0.04506.30; .NET CLR 3.0.04506.648)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.1; DigExt)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.1)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.0; YComp 5.0.2.6)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.0; YComp 5.0.0.0) (Compatible; ; ; Trident/4.0)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.0; YComp 5.0.0.0)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.0; .NET CLR 1.1.4322)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.0)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 4.0; .NET CLR 1.0.2914)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 4.0)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows 98; YComp 5.0.0.0)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows 98; Win 9x 4.90)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows 98)", //
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.1)", //
                "Mozilla/4.0 (compatible; U; MSIE 6.0; Windows NT 5.1) (Compatible; ; ; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)", //
                "Mozilla/4.0 (compatible; U; MSIE 6.0; Windows NT 5.1)", //
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.3; Tablet PC 2.0)", //
                "Nokia2760/2.0 (06.82) Profile/MIDP-2.1 Configuration/CLDC-1.1", //
                "Mozilla/5.0 (Linux; U; Android 0.5; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3", //
                "Mozilla/5.0 (Linux; U; Android 2.1; en-us; Nexus One Build/ERD62) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17", //
                "Mozilla/5.0 (Linux; U; Android 2.1-update1; de-de; HTC Desire 1.19.161.5 Build/ERE27) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17", //
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/534.53.11 (KHTML, like Gecko) Version/5.1.3 Safari/534.53.10", //
                "Opera/9.80 (Macintosh; Intel Mac OS X 10.7.3; U; en) Presto/2.9.168 Version/11.52", //
                "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13", //
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)",
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows Phone 8.0; Trident/6.0; IEMobile/10.0; ARM; Touch; NOKIA; Nokia 920T)"

        };
        UserAgentInterceptor f = new UserAgentInterceptor();
        for (String ua : uaList) {
            UserAgent u = f.parseUserAgent(ua);
            System.out.println(u);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        try {
            setUserAgent(request, response);
        } catch (Exception e) {
            logger.warn("process user agent fail", e);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {

        request.removeAttribute(USER_AGENT_KEY);
    }

}
