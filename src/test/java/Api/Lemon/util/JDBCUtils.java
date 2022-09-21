package Api.Lemon.util;

import Api.Lemon.data.Constants;
//import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JDBCUtils {
    public static Connection getConnection(){
        //定义数据库连接
        //Oracle:jdbc:oracle:thin:@localhost:1521:DBName
        //SqlServer:jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=DBName
        //MySql:jdbc:mysql://localhost:3306/DBName
    String url="jdbc:mysql://"+ Constants.DB_BASE_URL + Constants.DB_NAME +"?useUnicode=true&characterEncoding=utf-8";
    String user=Constants.DB_USER_NAME;
    String password=Constants.DB_PWD;
    //定义数据库连接对象
    Connection conn =null;
    try {
        conn = DriverManager.getConnection(url,user,password);
    }catch (Exception throwables){
        throwables.printStackTrace();
      }
    return conn;
    }

/*    public static void main(String[] args) throws SQLException {
        //1、建立数据库连接
        Connection connection =getConnection();
        //2、实例化数据库操作对象
        QueryRunner queryRunner = new QueryRunner();
        //3、对数据库进行更新操作
        //String insertSql="INSERT INTO `futureloan`.`member` VALUES (994999, 'xixi2', 'E315CBFBD45F8597EB5AB8C0F6FC900F', '15030456547', 1, 10000.00, '2022-08-25 14:41:33')";
        String updateSql="UPDATE `futureloan`.`member` SET reg_name=\"大大熙熙\" where id=\"994999\"";
        //String deletrSql="DELETE FROM  `futureloan`.`member`WHERE id =994999";
        queryRunner.update(connection,updateSql);

        //4、对数据库进行查询操作
        //MapHandler:将第一条结果封装到Map<String,Object>中，key是字段名，value是字段值
        String MapHandlerSql = "SELECT * from `futureloan`.`member`WHERE id =994999";
        Map<String,Object> result1 = queryRunner.query(connection, MapHandlerSql, new MapHandler());
        System.out.println(result1);
        //MapListHandler:将每条结果封装到Map<String,Object>中，再将这些Map封装到List集合中
        String MapListHandlerSql = "SELECT * from `futureloan`.`member`WHERE id <10";
        List<Map<String,Object>> result2 = queryRunner.query(connection, MapListHandlerSql, new MapListHandler());
        System.out.println(result2);
        //ScalarHandler:用于单个数据储存
        String ScalarHandlerSql = "SELECT count(*) FROM `futureloan`.`member` WHERE id<20";
        Long result3 = queryRunner.query(connection, ScalarHandlerSql, new ScalarHandler<Long>());
        System.out.println(result3);
    }*/ //数据库增删改查操作测试

    /**
     * 关闭数据库连接
     * @param connection 数据库连接对象
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close(); //关闭数据库连接
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sql的更新操作（包括 新增、修改、删除）
     * @param sql 要执行的sql语句
     */
    public static void update(String sql){
        Connection connection=getConnection();
        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.update(connection, sql);
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }finally { //不管有没有出现异常，finally块中代码都会执行；
            closeConnection(connection); //关闭数据库链接
        }

    }

    /**
     * 查询所有的结果集
     * @param sql 要执行的sql语句
     * @return 返回的结果集
     */
    public static List<Map<String,Object>> queryAll(String sql){
        Connection connection=getConnection();
        QueryRunner queryRunner = new QueryRunner();
        List<Map<String,Object>> result =null;
        try {
            result = queryRunner.query(connection, sql, new MapListHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally { //不管有没有出现异常，finally块中代码都会执行；
            closeConnection(connection); //关闭数据库链接
        }
        return result;
    }

    /**
     * 查询结果集中的第一条
     * @param sql 要执行的sql语句
     * @return 返回的结果集
     */
    public static Map<String,Object> queryOne(String sql){
        Connection connection=getConnection();
        QueryRunner queryRunner = new QueryRunner();
        Map<String,Object> result =null;
        try {
            result = queryRunner.query(connection, sql, new MapHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally { //不管有没有出现异常，finally块中代码都会执行；
            closeConnection(connection); //关闭数据库链接
        }
        return result;
    }

    /**
     * 查询单条的数据
     * @param sql 要执行的sql语句
     * @return 返回的结果集
     */
    public static Object querySingleData(String sql){
        Connection connection=getConnection();
        QueryRunner queryRunner = new QueryRunner();
        Object result =null;
        try {
            result = queryRunner.query(connection, sql, new ScalarHandler<Object>());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally { //不管有没有出现异常，finally块中代码都会执行；
            closeConnection(connection); //关闭数据库链接
        }
        return result;
    }
}
