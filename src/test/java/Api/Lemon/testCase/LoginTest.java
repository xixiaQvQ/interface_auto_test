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
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;


public class LoginTest extends BaseTest {

    @BeforeClass
    public void register(){
        String phone = PhoneRandomUtil.getUnregisterPhone(); //获取到随机生成手机号
        Environment.envData.put("phone",phone); //将手机号保存到环境变量Map中
        List<ExcelPojo> list = readSpecifyExcelData(2,0,1);
        ExcelPojo excelPojo = paramsReplace(list.get(0)); //参数替换
        Response res = request(excelPojo,"LoginTest"); //发送请求
        extractToEnvironment(excelPojo,res); //提取注册返回的手机号保存到环境变量
    }

    @Test(dataProvider = "getLoginData")
    public void login (ExcelPojo excelPojo) throws FileNotFoundException {

        excelPojo = paramsReplace(excelPojo); //替换用例数据，并以excelPojo来接受
        Response res = request(excelPojo,"LoginTest");
        asserResponse(excelPojo,res);
    }
    @DataProvider //此注解返回值必须是Object数组
    public Object[] getLoginData (){
        List<ExcelPojo> list = readSpecifyExcelData(2, 1);
        return list.toArray(); //将集合转为一个一维数组
    }
}



