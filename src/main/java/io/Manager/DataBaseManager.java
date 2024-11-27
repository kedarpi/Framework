package io.Manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author t618320
 */
public class DataBaseManager {

    private static DataBaseManager dbObj;
    Connection connection = null;

    private DataBaseManager(){

    }

    public static DataBaseManager getInstance(){

        if(dbObj ==null){
            dbObj = new DataBaseManager();
        }
        return dbObj;
    }

    public void closeDBConnection() throws SQLException {

        if(connection !=null)
            connection.close();
            connection = null;
    }

    public Statement getStatementObj(String connectionName)throws Exception{

        Statement statement = null;
        Connection conn = DataBaseManager.getInstance().getDBConnection(connectionName);
        if(conn!=null)
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
        return statement;
    }

    public String getSingleColumnResult(String connectName, String query) throws Exception{

        try {
            Statement stmt = DataBaseManager.getInstance().getStatementObj(connectName);
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(rs.getRow());
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        }
        finally{
            closeDBConnection();
        }
    }

    public boolean updateTable(String connectName, String query) throws Exception{

        try {
            Connection conn = DataBaseManager.getInstance().getDBConnection(connectName);
            PreparedStatement stmt = conn.prepareStatement(query);
            int result = stmt.executeUpdate(query);
            if(result==0)
                return true;
        }
        finally{
            closeDBConnection();
        }
        return false;
    }

  /*  public void execSQL(String sqlPath, String driveClass,String url, String user, String passwd) {
        SQLExec sqlExec = new SQLExec();
        sqlExec.setDriver(driveClass);
        sqlExec.setUrl(url);
        sqlExec.setUserid(user);
        sqlExec.setPassword(passwd);
        sqlExec.setSrc(new File(sqlPath));
        sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(
                SQLExec.OnError.class, "abort")));
        sqlExec.setPrint(false);
        sqlExec.setProject(new Project());
        sqlExec.execute();
    }
*/
    public Object[][] getQueryResult(String connectionName, String sql) throws Exception{

        try {
            Statement stmt = DataBaseManager.getInstance().getStatementObj(connectionName);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            int j = 0;
            rs.last();
            int rowsNumber = rs.getRow();
            rs.beforeFirst();
            Object[][] data = new Object[rowsNumber][columnsNumber];
            while (rs.next()) {
                for (int i = 0; i < columnsNumber; i++) {
                    data[j][i] = rs.getString(i + 1);
                }
                j++;
            }
            return data;
        } finally {
            closeDBConnection();
        }
    }

    public List<String> getSingleColumnQueryResult(String connectionName, String sql) throws Exception{

        try {
            Statement stmt = DataBaseManager.getInstance().getStatementObj(connectionName);
            List<String> outer = new ArrayList<String>();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                outer.add(rs.getString(1));
            }
            return outer;
        }finally {
            closeDBConnection();
        }
    }

    public Connection getDBConnection(String connectionName) throws Exception{
        FileReader fileread = null;
        try {
            fileread = new FileReader("./Resources/DBDetails/db.json");
            JsonNode jsonNode = new ObjectMapper().readTree(fileread).get(connectionName);
            if (jsonNode.isEmpty()) {
                System.out.println("Connection Name is not in JSON, please keep details there.");
            } else {
                String url = jsonNode.get("url").textValue();
                JsonNode db = jsonNode.get("databasetype");
                if (connection == null) {
                    if(db!=null)
                        Class.forName(getDriver(db.textValue()));
                    connection = DriverManager.getConnection(url, jsonNode.get("username").toString().trim(), jsonNode.get("password").toString().trim());
                    System.out.println("DB connection established !!!");
                }
            }
        }catch (Exception e){
            System.out.println("Error in DB connection, please check is db.json having correct details");
            e.printStackTrace();
        } finally{
            fileread.close();
        }
        return connection;
    }

    private String getDriver(String type) {
        if (type.equalsIgnoreCase("oracle"))
            return "oracle.jdbc.driver.OracleDriver";
        else if (type.equalsIgnoreCase("hive"))
            return "org.apache.hive.jdbc.HiveDriver";
        else if (type.equalsIgnoreCase("greenplum") || type.equalsIgnoreCase("postgre"))
            return "org.postgresql.Driver";
        else if (type.equalsIgnoreCase("sybase"))
            return "com.sybase.jdbc.SybDriver";
        else
            return null;
    }
}
