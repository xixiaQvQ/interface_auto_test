package Api.Day03;

import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static java.lang.Enum.valueOf;

public class AssertDemo {
    @Test(priority = 1)   //@Test(dependsOnmethods = xx):依赖于xx方法
    public void Assert01(){
        //RestAssured全局配置（github--restassured--使用指南内搜索：Json Config）
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        //baseURI全局配置（配置统一的请求路径，when下面只需输入剩余的接口路径，restassured会自动将其拼接）
        RestAssured. baseURI="http://api.lemonban.com/futureloan";
        String json ="{\"mobile_phone\":\"13301822908\",\"pwd\":\"12345678\"}";
        Response res =
        given().
                //config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                body(json).
                header("Content-Type","application/json").
                header("X-Lemonban-Media-Type","lemonban.v2").
        when().
                post("/member/login").
        then().log().body().extract().response();

        //整数类型断言
        int code = res.jsonPath().get("code");
        Assert.assertEquals(code,0);
        //字符串类型
        String msg = res.jsonPath().get("msg");
        Assert.assertEquals(msg,"OK");
        //小数类型（注：restassured里返回的json小数都是float类型）
        // 小数点丢失精度问题（在github搜索resassured--bigdecimal。given中添加配置，将double和float类型都转换为BigDecimal类型）
        BigDecimal actual = res.jsonPath().get("data.leave_amount");//将实际值转为BigDecimal类型
        BigDecimal expected = BigDecimal.valueOf(20001.01);//将期望值转为BigDecimal类型
        Assert.assertEquals(actual,expected);

        int memberId = res.jsonPath().get("data.id");
        String token = res.jsonPath().get("data.token_info.token");

//——————————————————————————————————————————————————————————————————————————————————————————————————————
        //充值接口
        String jsonData2 = "{\"member_id\":" + memberId + ",\"amount\":\"1\"}";
        Response res2 =
                given().
                        body(jsonData2).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+token).
                        when().
                        post("/member/recharge").
                        then().
                        log().body().extract().response();
        System.out.println("剩余金额：" + res2.jsonPath().get("data.leave_amount"));
        System.out.println("接口响应时间：" + res2.time());
        BigDecimal actual2 = res2.jsonPath().get("data.leave_amount");
        BigDecimal expected2 = BigDecimal.valueOf(20002.01);
        Assert.assertEquals(actual2, expected2);

    }

}
