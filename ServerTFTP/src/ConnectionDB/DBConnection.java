package ConnectionDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mario
 */
public class DBConnection {
    String URL = "jdbc:mysql://163.178.107.10/";
    String NAMEDB = "if5000_proyecto1_b76090_b75934";
    String USER = "laboratorios";
    String PASSWORD = "KmZpo.2796";
    String TYPE = "?useSSL=false";
    String DRIVER = "com.mysql.jdbc.Driver";
    public Connection connection = null;
    
    public Connection conect(){
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL+NAMEDB+TYPE,USER,PASSWORD);
            if(connection != null){
                System.out.println("ConnectionDB.DBConnection.conect()");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("ConnectionDB.DBConecction.conect() "+ex);
//            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            System.out.println("ConnectionDB.DBConecction.conect() "+ex);
//            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            return connection;
        }
    }
    
    public Connection getConnection(){
        return connection;
    }
    
    public void disConnect() throws SQLException{
        this.connection.close();
    }
}
