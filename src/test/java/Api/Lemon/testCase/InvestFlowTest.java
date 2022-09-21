package Api.Lemon.testCase;

import Api.Lemon.common.BaseTest;
import Api.Lemon.data.Constants;
import Api.Lemon.data.Environment;
import Api.Lemon.pojo.ExcelPojo;
import Api.Lemon.util.JDBCUtils;
import Api.Lemon.util.PhoneRandomUtil;
import com.alibaba.fastjson.JSONObject;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.config.JsonConfig.jsonConfig;

public class InvestFlowTest extends BaseTest {
    @BeforeClass
    public void setup() {
        //生成三个角色的随机手机号码（借款人、管理员、投资人）
        String borrower_phone = PhoneRandomUtil.getUnregisterPhone();
        Environment.envData.put("borrower_phone", borrower_phone);
        String admin_phone = PhoneRandomUtil.getUnregisterPhone();
        Environment.envData.put("admin_phone", admin_phone);
        String investor_phone = PhoneRandomUtil.getUnregisterPhone();
        Environment.envData.put("investor_phone", investor_phone);
        //读取用例数据从第一条~第九条
        List<ExcelPojo> list = readSpecifyExcelData(4, 0, 9);
        for (int i = 0; i < list.size(); i++) {
            ExcelPojo excelPojo = list.get(i);
            excelPojo = paramsReplace(excelPojo); //参数替换
            Response res = request(excelPojo,"InvestFlowTest"); //发送请求
            //判断是否要提取响应数据
            if (excelPojo.getExtract() != null) {
                extractToEnvironment(excelPojo, res);
            }
        }
    }

    @Test
    public void invest() {
        List<ExcelPojo> list = readSpecifyExcelData(4, 9);
        ExcelPojo excelPojo = paramsReplace(list.get(0));
        Response res = request(excelPojo,"InvestFlowTest"); //发送请求
        asserResponse(excelPojo, res);  //响应断言
        assertSQL(excelPojo);  //数据库断言


    }
}