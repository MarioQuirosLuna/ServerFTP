package Data;

import ConnectionDB.DBConnection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author mario
 */
public class UserData {
    public UserData(){
    
    }
    public void addNewUser(String userName, String userPassword) throws SQLException{
        DBConnection dbConnection = new DBConnection();
        Statement execution = dbConnection.conect().createStatement();
        String query="";
        System.out.println("Domain.User.addNewUser() "+query);
        
        if(execution.executeUpdate(query) > 0){
            System.out.println("Domain.User.addNewUser()");
        }
    }
}