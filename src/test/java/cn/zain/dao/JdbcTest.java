package cn.zain.dao;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class JdbcTest {
    private static final Logger logger = LoggerFactory.getLogger(JdbcTest.class);

    @Test
    public void name() throws Exception {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://192.168.21.104:3306/ssm?useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull";
        String username = "root";
        String password = "passwd4root~Q";
        String sql = "select id,remark,create_time from sys_user ";
        Connection conn = null;
        PreparedStatement pre=null;
        ResultSet res=null;
        logger.info("执行查询");
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = DriverManager.getConnection(url, username, password);
            pre = conn.prepareStatement(sql);
            res = pre.executeQuery();
            logger.info("res");
            ResultSetMetaData rData = res.getMetaData();
            while (res.next()) {
                Map<Object, Object> obj = new HashMap<Object, Object>();
                for (int i = 1; i <= rData.getColumnCount(); i++) {
                    final String columnName = rData.getColumnName(i);//列名
                    final String columnTypeName = rData.getColumnTypeName(i);
                    final String columnClassName = rData.getColumnClassName(i);
                    obj.put(columnName,res.getObject(i));
                    logger.info("map..");
                }
            }
        }catch (Exception e){
            logger.error("",e);
        }
    }
}
