package Api.Day04;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.sun.org.apache.xpath.internal.objects.XObject;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
//数据驱动：通过数据驱动用例的执行
public class DataDrivenDemo {

    @Test(dataProvider = "getLoginDatas02") //直接将getLoginDatas方法下的数据注入到该测试注解
    public void Login(ExcelPojo excelPojo) {
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

        String InputParams = excelPojo.getInputParams(); //获取入参
        String url = excelPojo.getUrl();  //获取url
        String requestHeader = excelPojo.getRequestHeader();  //获取请求头
        Map requestHeaderMap = (Map) JSON.parse(requestHeader); //把请求头转换成map，通过map来接收
        String getExpected = excelPojo.getExpected();   //获取期望返回结果
        Map<String ,Object> getExpectedMap = (Map) JSON.parse(getExpected);  //把期望结果转成map，通过map来接收

        Response res =
                given().
                        //config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                        body(InputParams).
                        headers(requestHeaderMap).
                when().
                        post(url).
                then().
                        log().all().extract().response();

        for (String key : getExpectedMap.keySet()) { //将excel里的期望结果key进行for in增强循环遍历
            System.out.println(key);

            Object actualValue = res.jsonPath().get(key);  //获取接口返回的实际结果value

            Object ExpectedValue = getExpectedMap.get(key);  //获取期望结果的value

            Assert.assertEquals(actualValue, ExpectedValue);  //断言
        }

    }

//    @DataProvider  //若使用此注解标明，返回值必须是Object数组
//    public Object[][] getLoginDatas() {
//        Object[][] datas = {{"18821956619", "12345678"},
//                {"1882195661", "12345678"},
//                {"188219566199", "12345678"}};
//        return datas;
//    }

    @DataProvider
    public Object[] getLoginDatas02() {
        File file = new File("D:\\A-zyy个人资料--------\\Java 接口自动化\\api_testcases_futureloan_practice.xls");

        ImportParams importParams = new ImportParams();  //导入的参数对象（需要读取到Excel下的哪个sheet）
        importParams.setStartSheetIndex(1);  //索引第二个sheet用例
        //importParams.setStartRows(3);
        //importParams.setReadRows(2);
        List<ExcelPojo> list = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);  //读取、导入Excel
        return list.toArray();  //把集合转换为一维数组
    }



    public static void main(String[] args) {
        //DataDrivenDemo dataDrivenDemo = new DataDrivenDemo();
        //获取用例路径
        File file = new File("D:\\A-zyy个人资料--------\\Java 接口自动化\\api_testcases_futureloan_practice.xls");

        ImportParams importParams = new ImportParams();  //导入的参数对象（需要读取到Excel下的哪个sheet）
        importParams.setStartSheetIndex(1);  //索引第二个sheet用例

        List<Object> list2 = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);  //读取、导入Excel
        for (Object object : list2) {

            //dataDrivenDemo.Login(object);
            System.out.println(object);
        }
    }
}
