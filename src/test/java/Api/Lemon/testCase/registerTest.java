package Api.Lemon.testCase;

import Api.Lemon.common.BaseTest;
import Api.Lemon.data.Environment;
import Api.Lemon.pojo.ExcelPojo;
import Api.Lemon.util.JDBCUtils;
import Api.Lemon.util.PhoneRandomUtil;
import com.alibaba.fastjson.JSONObject;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class registerTest extends BaseTest {
    @BeforeTest
    public void setup(){
        String phone1 = PhoneRandomUtil.getUnregisterPhone();
        String phone2 = PhoneRandomUtil.getUnregisterPhone();
        String phone3 = PhoneRandomUtil.getUnregisterPhone();
        String phone4 = PhoneRandomUtil.getUnregisterPhone();
        String phone5 = PhoneRandomUtil.getUnregisterPhone();
        Environment.envData.put("phone1", phone1);
        Environment.envData.put("phone2", phone2);
        Environment.envData.put("phone3", phone3);
        Environment.envData.put("phone4", phone4);
        Environment.envData.put("phone5", phone5);
    }

    @Test(dataProvider = "getRegisterData")
    public void register(ExcelPojo excelPojo){
        excelPojo = paramsReplace(excelPojo);
        Response res = request(excelPojo,"registerTest");
        asserResponse(excelPojo,res);
        assertSQL(excelPojo);
/*        String dbAssert = excelPojo.getDbAssert();
        if (dbAssert !=null) {
            Map<String, Object> dbAssertMap = JSONObject.parseObject(dbAssert, Map.class);
            //Set<String> keys = dbAssertMap.keySet();//获取到【数据库校验】的key值
            for (String key : dbAssertMap.keySet()) {//根据SQL语句循环遍历（可能有多条sql断言）
                Integer expectedValue = (Integer) dbAssertMap.get(key);//通过key取值,并用Integer类型来接收
                Long expectedValue2 = expectedValue.longValue(); //把Integer类型转成Long类型
                Object actualValue = JDBCUtils.querySingleData(key); //通过sql执行得到结果值
                Assert.assertEquals(actualValue, expectedValue2);
            }
        }*/

    }
    @DataProvider
    public Object[] getRegisterData(){
        List<ExcelPojo> list = readSpecifyExcelData(1, 0);
        return list.toArray(); //将集合转为一个一维数组
    }
}
