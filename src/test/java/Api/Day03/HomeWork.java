package Api.Day03;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class HomeWork {
    int v2MemberId;
    int v1MemberId;
    int AddProjct;
    String v1Token;
    String v2Token;

//    @Test(priority = 1)  //注册
//    public void registerDemo() {
//        String register = "{\"mobile_phone\":\"18821956610\",\"pwd\":\"123456789\",\"type\":0}";
//        Response response =
//        given().
//                body(register).
//                header("Content-Type", "application/json").
//                header("X-Lemonban-Media-Type", "lemonban.v2").
//                when().
//                post("http://api.lemonban.com/futureloan/member/register").
//                then().
//                log().body().extract().response();
//    }

    @Test(priority = 2)  //项目人 -登陆--新增项目
    public void logonDemo() {
        String register = "{\"mobile_phone\":\"18821956610\",\"pwd\":\"123456789\"}";
        Response res =
                given().
                        body(register).
                        header("Content-Type", "application/json").
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                        when().
                        post("http://api.lemonban.com/futureloan/member/login").
                        then().
                        log().body().extract().response();
        v1MemberId = res.jsonPath().get("data.id");  //v1MemberId 即为全局变量
        v1Token = res.jsonPath().get("data.token_info.token");

        int memberId = res.jsonPath().get("data.id");//变量前面加上类型，即为局部变量

        System.out.println("接口响应时间" + res.time());
        System.out.println("" +
                "" +
                " ");


        //新增项目
        String add =
                "{\"member_id\":" + v1MemberId + ",\n" +
                        "\"title\":\"新增项目1\",\n" +
                        "\"amount\":\"50000\",\n" +
                        "\"loan_rate\":\"10\",\n" +
                        "\"loan_term\":\"30\",\n" +
                        "\t\"loan_date_type\":2,\n" +
                        "\t\"bidding_days\":3\n" +
                        "}";
        Response res2 =
                given().
                        body(add).
                        header("Content-Type", "application/json").
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                        header("Authorization", "Bearer " + v1Token).
                        when().
                        post("http://api.lemonban.com/futureloan/loan/add").
                        then().
                        log().body().extract().response();
        AddProjct = res2.jsonPath().get("data.id");
        System.out.println("接口响应时间" + res2.time());
    }

    @Test(priority = 3)  //管理员--审核项目--投资
    public void AuditInvest() {
        String register = "{\"mobile_phone\":\"18821956614\",\"pwd\":\"123456789\"}";
        Response res =
                given().
                        body(register).
                        header("Content-Type", "application/json").
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                        when().
                        post("http://api.lemonban.com/futureloan/member/login").
                        then().
                        log().body().extract().response();
        System.out.println("接口响应时间" + res.time());

        //将响应结果的参数提取出来，用作下一个接口的参数
        v2Token = res.jsonPath().get("data.token_info.token");
        v2MemberId = res.jsonPath().get("data.id");
        System.out.println("" +
                "" +
                " ");


        //充值
        String recharge = "{\"member_id\":" + v2MemberId + ",\"amount\":\"10000\"}";
        Response res2 =
                given().
                        body(recharge).
                        header("Content-Type", "application/json").
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                        header("Authorization", "Bearer " + v2Token).
                        when().
                        post("http://api.lemonban.com/futureloan/member/recharge").
                        then().
                        log().body().extract().response();
        System.out.println("剩余金额：" + res2.jsonPath().get("data.leave_amount"));
        System.out.println("接口响应时间" + res2.time());


        //审核项目
        String audit = "{\"loan_id\":37771,\"approved_or_not\":true}";
                given().
                        body(audit).
                        header("Content-Type", "application/json").
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                        header("Authorization", "Bearer " + v2Token).
                when().
                        patch("http://api.lemonban.com/futureloan/loan/audit").
                then().
                        log().body().extract().response();


        //投资项目
        String invest = "{\"member_id\":" + v2MemberId + ",\"loan_id\":\"37771\",\"amount\":\"500\"}";
    Response res3 =
            given().
                    body(invest).
                    header("Content-Type", "application/json").
                    header("X-Lemonban-Media-Type", "lemonban.v2").
                    header("Authorization", "Bearer " + v2Token).
                    when().
                    post("http://api.lemonban.com/futureloan/member/invest").
                    then().
                    log().body().extract().response();
  }
}
