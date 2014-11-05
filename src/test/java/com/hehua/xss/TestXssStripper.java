package com.hehua.xss;

import com.hehua.framework.web.antispam.ParamXssStripper;
import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by hesheng on 14-10-1.
 */
public class TestXssStripper extends TestCase {

    public void testJSTagByScript() {
        String strScript = "<script type=\"text/javascript\">hello world<div>alert(\"java\")</div></script>";
        System.out.println(ParamXssStripper.filter(strScript));
    }

    public void testJSTagByRecursionScript() {
        String strScript = "<script type=\"text/javascript\">hello world<div><script>alert(\"java\")</script></div></script>";
        System.out.println(ParamXssStripper.filter(strScript));
    }

    public void testJSTagBySrcAttribute() {
        String strScript = "<img src=\"javascript:alert(xss)\" />";
        System.out.println(ParamXssStripper.filter(strScript));

        String str = "<p><img alt=\"贝亲婴儿皂_01.jpg\" src=\"http://img.hehuababy.com/2eebff54-51c7-4295-9b5b-7f82e33419e0.jpeg\" title=\"2eebff54-51c7-4295-9b5b-7f82e33419e0\"/><img alt=\"贝亲婴儿皂_02.jpg\" src=\"http://img.hehuababy.com/49109f5c-3f13-4428-b0a7-f397a50cd8db.jpeg\" title=\"49109f5c-3f13-4428-b0a7-f397a50cd8db\"/><img alt=\"贝亲婴儿皂_03.jpg\" src=\"http://img.hehuababy.com/6941f7c5-27a6-4b89-893f-61728d6cb3a2.jpeg\" title=\"6941f7c5-27a6-4b89-893f-61728d6cb3a2\"/><img alt=\"贝亲婴儿皂_04.jpg\" src=\"http://img.hehuababy.com/e8cb69d2-a825-41df-b6c9-726234c1e872.jpeg\" title=\"e8cb69d2-a825-41df-b6c9-726234c1e872\"/><img alt=\"贝亲婴儿皂_05.jpg\" src=\"http://img.hehuababy.com/d9cc4cd2-af22-43bf-b08e-9dbbfada5f6b.jpeg\" title=\"d9cc4cd2-af22-43bf-b08e-9dbbfada5f6b\"/><img alt=\"尾图.jpg\" src=\"http://img.hehuababy.com/6b724919-15e6-4e00-b1bc-a729aa4c0a5e.jpeg\" title=\"6b724919-15e6-4e00-b1bc-a729aa4c0a5e\"/></p>";
        String filterStr = ParamXssStripper.filter(str);
        System.out.println(filterStr);
        System.out.println(StringUtils.equals(str, filterStr));
    }

    public void testJSTagByAttrbuteScript() {
        String strScript = "<button onclick=\"alert(**)\">ok</button>";
        System.out.println(ParamXssStripper.filter(strScript));
    }


}
