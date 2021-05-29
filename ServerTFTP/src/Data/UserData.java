package Data;

import ConnectionDB.DBConnection;
import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mario
 */
public class UserData {
    public UserData(){
    
    }
    public boolean addNewUser(String userName, String userPassword){
        try {
            DBConnection dbConnection = new DBConnection();
            Statement execution = dbConnection.conect().createStatement();
            
            String query="CALL checkInUser('"+userName+"','"+userPassword+"');";
            
            if(execution.executeUpdate(query) > 0){
                File folder = new File("Carpetas\\"+userName);
                folder.mkdir();
                execution.close();
                dbConnection.disConnect();
                return true;
            }else{
                System.out.println("Data.UserData.addNewUser() "+ "No se registro");
            }
            execution.close();
            dbConnection.disConnect();
            return false;
        } catch (SQLException ex) {
            System.out.println("Data.UserData.addNewUser() "+ex);
//            Logger.getLogger(UserData.class.getName()).log(Level.SEVERE, null, ex); 
            return false;
        }
    }
    
    public boolean checkUser(String userName, String userPassword){
        String nameTemp="";
        String passWordTemp="";
        
        try {
            DBConnection dBConnection = new DBConnection();
            Statement execution = dBConnection.conect().createStatement();
            
            String query = "CALL logInUser('"+userName+"','"+userPassword+"');";
            ResultSet result = execution.executeQuery(query);
            
            while(result.next()){
                nameTemp = result.getString("user_name");
                passWordTemp = result.getString("user_password");
                
                System.out.format("%s,%s\n", nameTemp, passWordTemp);
            }
            execution.close();
            dBConnection.disConnect();
        } catch (SQLException ex) {
            System.out.println("Data.UserData.checkUser() "+ex);
//            Logger.getLogger(UserData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return userPassword.equalsIgnoreCase(passWordTemp) && !"".equals(passWordTemp);
        
    }
}