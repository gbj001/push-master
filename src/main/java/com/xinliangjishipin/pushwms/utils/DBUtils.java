package com.xinliangjishipin.pushwms.utils;

import com.xinliangjishipin.pushwms.entity.User;

import java.sql.*;

public class DBUtils {
    public static Connection getConnection(){
        Connection conn=null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");//找到oracle驱动器所在的类
            String url="jdbc:oracle:thin:@218.241.158.246:8152:NCGC"; //URL地址
            String username="xlj_0328";
            String password="xlj_0328";
            conn= DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }
    public static void close(PreparedStatement pstmt){
        if(pstmt !=null){
            try {
                pstmt.close();
            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs){
        if(rs !=null){
            try {
                rs.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]){

        String sql = "select * from CS_USER_INFO where \"user_name\"='test1'";
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;//定义存放查询结果的结果集
        User user=null;
        try{
            conn=DBUtils.getConnection();
            pstmt=conn.prepareStatement(sql);
            rs=pstmt.executeQuery();//运行查询操作
            if(rs.next()){
                user=new User();

            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            //按顺序进行关闭
            DBUtils.close(rs);
            DBUtils.close(pstmt);
            //DBUtils.close(conn);

        }
    }
}
