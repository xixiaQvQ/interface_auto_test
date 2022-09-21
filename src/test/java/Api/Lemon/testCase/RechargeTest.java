package Api.Lemon.testCase;

import Api.Lemon.common.BaseTest;
import Api.Lemon.data.Constants;
import Api.Lemon.data.Environment;
import Api.Lemon.pojo.ExcelPojo;
import Api.Lemon.util.PhoneRandomUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;

public class RechargeTest extends BaseTest {
     int memberId;
     String token;

    @BeforeClass
    public void setup(){
        //RestAssured全局配置（github--restassured--使用指南内搜索：Json Config）
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        RestAssured.baseURI = Constants.BASE_URI;
        List<ExcelPojo> list = readSpecifyExcelData(3,0,2);
        String phone = PhoneRandomUtil.getUnregisterPhone();
        Environment.envData.put("phone", phone);
        ExcelPojo excelPojo = paramsReplace(list.get(0));

        //注册请求
        Response resRegister = request(excelPojo,"rechargeTest");
        extractToEnvironment(excelPojo,resRegister); //提取接口返回对应字段保存到环境变量
        excelPojo = paramsReplace(list.get(1));//参数替换，替换{{phone}}

        //登陆请求
        Response resLogin = request(excelPojo,"rechargeTest");
        extractToEnvironment(list.get(1),resLogin);

/*        Map<String,Object> extractMap = JSONObject.parseObject(extractStr,Map.class);
        Object memberIdPath = extractMap.get("member_id"); //得到的值就是memberId路径表达式
        memberId = resLogin.jsonPath().get((String) memberIdPath);
        Environment.memberId = memberId;

        Object tokenPath = extractMap.get("token"); //得到的值就是token路径表达式
        token = resLogin.jsonPath().get((String) tokenPath);
        Environment.token = token;*/ //未封装方法：通过【提取返回数据】这列的路径表达式取到值并存到环境变量
    }

    @Test(dataProvider = "getRechargeDatas")
    public void recharge(ExcelPojo excelPojo){

        excelPojo = paramsReplace(excelPojo); //将入参的{{}}值进行替换
        Response res =request(excelPojo,"rechargeTest");
        extractToEnvironment(excelPojo, res);
        asserResponse(excelPojo, res); //响应断言
        assertSQL(excelPojo);  //数据库断言
/*        //Map<String,Object> ExpectedMap = (Map) JSON.parse(excelPojo.getExpected());  //获取期望结果并通过map来接收
        Map<String,Object> ExpectedMap = JSONObject.parseObject(excelPojo.getExpected(),Map.class);
        for (String key :ExpectedMap.keySet()){
            System.out.println(key);
            Object expectedValue = ExpectedMap.get(key); //获取map里期望值的value

            if(key.equals("data.leave_amount")){
              double expectedValue2 = Double.valueOf((String) expectedValue);
              expectedValue=BigDecimal.valueOf(expectedValue2);
            } //将map里的期望值String类型转为BigDecimal

            Object actualValue = res.jsonPath().get(key);  //获取接口返回的实际值value
            Assert.assertEquals(actualValue, expectedValue);  //断言
            } //for循环，对充值金额断言*/
    }

    @DataProvider
    public Object[] getRechargeDatas(){
        List<ExcelPojo> list = readSpecifyExcelData(3, 2);
        return list.toArray();
    }



}
