package Api.Day03;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

public class RestAssuredDemo01 {
    @Test
    //get请求（查询参数）
    public void getDemo01(){
        given().
                //queryParam：查询参数
                queryParam("mobilephone","13323234545").
                queryParam("pwd","123456").
        when().
                get("http://www.httpbin.org/get").
        then().
                log().body();   //.all():打印全部
        }

        @Test
        //post请求（form表单参数类型）
        public void postDemo01(){
            given().
                    formParam("mobilephone","13323234545").
                    formParam("pwd","123456").
            when().
                    post("http://www.httpbin.org/post").
            then().
                    log().body();
        }

        @Test
        //json参数类型
        public void postDemo02(){
        String jsonData = "{\"mobile_phone\":\"18821956614\",\"pwd\":\"123456789\"}";
        given().
                body(jsonData).
                contentType("application/json").
        when().
                post("http://www.httpbin.org/post").
        then().log().body();
        }

        @Test
        //xml参数类型
        public void postDemo03(){
        String xmlData = "<?xml version=\"1.0\" encoding = \"utf-8 \"?>\n"+"<suite>\n"+"<class>测试xml</class>\n" + "</suite>";
        given().
                body(xmlData).
                contentType("application/xml").
                when().
                post("http://www.httpbion.org/post").
                then().log().body();
        }

        @Test
        //多参数表单（上传文件）
        public void postDemo04(){
        given().
                multiPart(new File("G:\\maven\\multiPart.txt")).
        when().
                post("http://www.httpbin.org/post").
        then().log().body();
        }

        @Test
        public void getResponseHeader(){
        Response res =
        given().
        when().
                post("http://www.httpbin.org/post").
        then().log().body().extract().response();
            System.out.println("接口响应时间:" + res.time());  //1、获取接口响应时间
            System.out.println(res.getHeader("content-Type"));  //2、获取请求头参数
        }


        @Test
        //上一个接口的响应结果用作下一个接口的参数
        public void LoginRecharge(){
        String jsonData1 = "{\"mobile_phone\":\"18821956614\",\"pwd\":\"123456789\"}";
            Response res1 =
        given().
                body(jsonData1).
                header("Content-Type","application/json").
                header("X-Lemonban-Media-Type","lemonban.v2").
        when().
                post("http://api.lemonban.com/futureloan/member/login").
        then().
                extract().response();

            int memberId = res1.jsonPath().get("data.id");
            System.out.println(memberId);
            String token = res1.jsonPath().get("data.token_info.token");
            System.out.println(token);


        String jsonData2 = "{\"member_id\":" + memberId + ",\"amount\":\"10000\"}";
        Response res2 =
        given().
                body(jsonData2).
                header("Content-Type","application/json").
                header("X-Lemonban-Media-Type","lemonban.v2").
                header("Authorization","Bearer "+token).
        when().
                post("http://api.lemonban.com/futureloan/member/recharge").
        then().
                log().body().extract().response();
            System.out.println("剩余金额：" + res2.jsonPath().get("data.leave_amount"));
            System.out.println("接口响应时间：" + res2.time());
    }

    @Test
    //Gpath响应体数据提取--json
    public void GpathDemoJson(){
            Response resJson=
        given().
        when().
                get("http://www.httpbin.org/json").
        then().log().all().extract().response();
            //直接索引第二个title值
        System.out.println("第二个title值："+ resJson.jsonPath().get("slideshow.slides.title[1]"));
        //将title设置为一个list集合
        List<String> list = resJson.jsonPath().getList("slideshow.slides.title");
        System.out.println(list.get(0));
        System.out.println(list.get(1));
    }

    @Test
    //Gpath响应体数据提取--html
    public void GpathDemoHtml(){
        Response resHtml =
                given().
                when().
                        get("http://www.baidu.com").
                then().log().all().extract().response();
        System.out.println((char[]) resHtml.htmlPath().get("html.head.meta[0].@content"));
        System.out.println((char[]) resHtml.htmlPath().get("html.head.link.@href"));
        System.out.println((char[]) resHtml.htmlPath().get("html.head.title"));
    }

    @Test
    //Gpath响应体数据提取--xml
    public void GpathDemoxml(){
        Response resXml=
        given().
        when().
                get("http://www.httpbin.org/xml").
        then().log().all().extract().response();
        System.out.println((char[]) resXml.xmlPath().get("slideshow.slide[1].title"));
        System.out.println((char[]) resXml.xmlPath().get("slideshow.slide[1].@type"));
    }
}
