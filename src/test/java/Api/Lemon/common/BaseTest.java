package Api.Lemon.common;

import Api.Lemon.data.Constants;
import Api.Lemon.data.Environment;
import Api.Lemon.pojo.ExcelPojo;
import Api.Lemon.util.JDBCUtils;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import com.sun.xml.internal.stream.StaxErrorReporter;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;

//创建的父类，其他测试类需调用该方法可直接继承，例如：public class RechargeTest extends BaseTest
public class BaseTest {
    @BeforeTest
    public void GlobalSetup() throws FileNotFoundException {
        //返回json为BigDecimal数据类型(RestAssured全局配置（github--restassured--使用指南内搜索：Json Config）)
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        //baseURI全局配置（配置统一的请求路径，when下面只需输入剩余的接口路径，restassured会自动将其拼接）
        RestAssured.baseURI = Constants.BASE_URI;
/*        //全局重定向到本地文件
        File file =new File(System.getProperty("user.dir") + "\\log");
        if (!file.exists()){
            file.mkdir();
        }
        //将测试用例的日志保存到指定文件
        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new ResponseLoggingFilter(fileOutPutStream));
        //System.out.println(System.getProperty("user.dir")); //获取项目根目录*/ //全局重定向到本地文件
    }


    /**
     * 对get、post、patch、put...做二次封装
     *
     * @param excelPojo excel每行数据对应的对象
     * @return 返回接口响应结果
     */
    public Response request(ExcelPojo excelPojo,String moduleName) {
        //如果指定输出到文件的话，那么设置重定向输出到文件
        String logFilePath;
        if (Constants.LOG_TO_FILE) {
            //为每一个请求单独做日志保存
            File file = new File(System.getProperty("user.dir") + "\\log\\" + moduleName);
            if (!file.exists()) {
                file.mkdirs(); //创建log文件夹
            }
            logFilePath = file +"\\test" + excelPojo.getCaseId() + ".log";
            PrintStream fileOutPutStream = null;
            try {
                fileOutPutStream = new PrintStream(new File(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        }

        String url = excelPojo.getUrl(); //接口请求地址
        String method = excelPojo.getMethod(); //请求方法
        String params = excelPojo.getInputParams(); //请求参数
        Map<String, Object> headersMap = JSON.parseObject(excelPojo.getRequestHeader()); //将请求头转成map
        Response res = null;
        if ("get".equalsIgnoreCase(method)) {  //equalsIgnoreCase:忽略大小写
            res = given().headers(headersMap).log().all().when().get(url).then().log().body().extract().response();
        } else if ("post".equalsIgnoreCase(method)) {
            res = given().headers(headersMap).body(params).log().all().when().post(url).then().log().body().extract().response();
        } else if ("patch".equalsIgnoreCase(method)) {
            res = given().headers(headersMap).body(params).log().all().when().patch(url).then().log().body().extract().response();
        }
        //向Allure报表中添加日志
        if (Constants.LOG_TO_FILE) {
            try {
                Allure.addAttachment("接口请求响应信息", new FileInputStream(logFilePath)); //Allure定制添加接口请求响应信息
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }return res;
    }



    /**
     * 对响应结果断言
     * @param excelPojo 用例数据实体类对象
     * @param res 接口响应
     */
    public void asserResponse(ExcelPojo excelPojo,Response res){
        if (excelPojo.getExpected() !=null) {
            Map<String, Object> expectedMap = JSONObject.parseObject(excelPojo.getExpected(), Map.class);
            for (String key : expectedMap.keySet()) {
                //System.out.println(key);
                Object expectedValue = expectedMap.get(key); //获取map里期望值的value
                //System.out.println("expectedValue类型："+expectedValue.getClass());
                Object actualValue = res.jsonPath().get(key);  //获取接口返回的实际值value
                //System.out.println("actualValue类型："+actualValue.getClass());
                Assert.assertEquals(actualValue, expectedValue);  //断言
            }
        }
    }

    /**
     * 数据库断言
     * @param excelPojo
     */
    public void assertSQL(ExcelPojo excelPojo){
    String dbAssert = excelPojo.getDbAssert();
        if (dbAssert != null) {
        Map<String, Object> dbAssertMap = JSONObject.parseObject(dbAssert, Map.class);
        //Set<String> keys = dbAssertMap.keySet();//获取到【数据库校验】的key值
        for (String key : dbAssertMap.keySet()) {  //根据SQL语句循环遍历（可能有多条sql断言）
            Object expectedValue = dbAssertMap.get(key);  //通过key取值
            //System.out.println("expectedValue类型：" +expectedValue.getClass());
            if (expectedValue instanceof BigDecimal){ //如果excel期望值是BigDecimal类型
                Object actualValue = JDBCUtils.querySingleData(key); //通过sql执行得到结果值
                //System.out.println("actualValue类型："+actualValue.getClass());
                Assert.assertEquals(actualValue, expectedValue);
            }else if (expectedValue instanceof Integer){
                //如果期望值是Integer类型，数据库拿到的实际值是Long类型
                Long expectedValue2 = ((Integer) expectedValue).longValue(); //把Integer类型转成Long类型
                Object actualValue = JDBCUtils.querySingleData(key); //通过sql执行得到结果值
                Assert.assertEquals(actualValue, expectedValue2);
            }
          }
        }
    }

    /**
     * 将对应的接口返回字段存到环境变量当中
     * @param excelPojo 用例数据对象
     * @param res 接口返回Response对象
     */
    public void extractToEnvironment(ExcelPojo excelPojo,Response res){
        if (excelPojo.getExtract() !=null){
        Map<String,Object> extractMap = JSONObject.parseObject(excelPojo.getExtract(),Map.class);
        //循环遍历extractMap
        for (String key : extractMap.keySet()){
            Object path = extractMap.get(key);
            //根据【提取返回数据】里的路径表达式，提取实际接口对应返回字段的值
            Object value = res.jsonPath().get(path.toString());
            Environment.envData.put(key,value);  //存到环境变量中
            }
        }
    }

    /**封装：：使用正则表达式匹配器，将值进行替换
     * @param orgStr  原始字符串
     * @return
     */
    public static String regexReplace(String orgStr) {
        //如果原始字符串不为空，就执行正则替换，否则直接返回原始字符串
        if (orgStr != null) {
            Pattern pattern = Pattern.compile("\\{\\{(.*?)}}"); //pattern:正则表达式匹配器
            Matcher matcher = pattern.matcher(orgStr); //matcher:匹配原始字符串，得到匹配对象
            //通过result接收原始字符串，有多个值需替换时，则在前面已经替换过的基础上再去替换其他值
            String result = orgStr; //若只有一个值需替换：String result = "";
            while (matcher.find()) {
                String outerStr = matcher.group(0); //group(0)表示获取到整个匹配到的内容，例如：｛｛token｝｝
                String innerStr = matcher.group(1); //group(1)表示获取｛｛｝｝包裹着的内容，例如：token
                Object replaceStr = Environment.envData.get(innerStr);
                result = result.replace(outerStr, replaceStr + ""); //replace替换，将outerStr的值替换成replaceStr的值
            }//此处则以已替换的基础上进行替换其他值（若只有一个值需替换：result = orgStr.replace(outerStr, replaceStr);） ）
            return result;
        }
        return orgStr; //否则直接返回原始字符串
    }
    /**
     * 参数替换
     * @param excelPojo  用例数据对象
     */
    public ExcelPojo paramsReplace(ExcelPojo excelPojo){
        //正则替换-->请求入参
        String inputParams = regexReplace(excelPojo.getInputParams());
        excelPojo.setInputParams(inputParams);
        //正则替换-->请求头
        String requestHeader = regexReplace(excelPojo.getRequestHeader());
        excelPojo.setRequestHeader(requestHeader);
        //正则替换-->URL
        String url = regexReplace(excelPojo.getUrl());
        excelPojo.setUrl(url);
        //正则替换-->期望返回结果
        String expected = regexReplace(excelPojo.getExpected());
        excelPojo.setExpected(expected);
        //正则替换-->数据库校验
        String dbAssert = regexReplace(excelPojo.getDbAssert());
        excelPojo.setDbAssert(dbAssert);
        return excelPojo;
    }



    /** 将获取excel信息进行封装
     *读取excel指定行的数据
     * @param sheetNum  sheet编号(从1开始)
     * @param startRow  起始行 (默认从0开始)
     * @param readRow  读取行
     */
    public List<ExcelPojo> readSpecifyExcelData(int sheetNum, int startRow, int readRow){
        //将文件的路径放到父类写死，即可在测试类直接使用该路径
        File file = new File(Constants.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1); //获取sheet
        importParams.setStartRows(startRow); //获取起始行
        importParams.setReadRows(readRow); //获取读取行
        return ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
    }

    /** 将获取excel信息进行封装
     *读取excel指定行开始的所有excel数据
     * @param sheetNum  sheet编号(从1开始)
     * @param startRow  起始行 (默认从0开始)
     */
    public List<ExcelPojo> readSpecifyExcelData( int sheetNum, int startRow){
        File file = new File(Constants.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1); //获取sheet
        importParams.setStartRows(startRow); //获取起始行
        return ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
    }

    //读取excel某个sheet下所有数据
    public List<ExcelPojo> readAllExcelData( int sheetNum){
        File file = new File(Constants.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1); //获取sheet
        return ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
    }
}
