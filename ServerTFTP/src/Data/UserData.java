package Data;

import ConnectionDB.DBConnection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
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
    
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    private FileOutputStream fileOutputStream = null;
    private FileInputStream fileInputStream = null;
    private BufferedOutputStream bufferedOutputStream = null;
    private BufferedInputStream bufferedInputStream = null;
    
    private String nameDocument = "";
    private int totalSize = 0;
    private byte[] buffer = null;
    
    private File document = null;
    
    public UserData(Socket socket) throws IOException{
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
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
            System.err.println("Data.UserData.addNewUser() "+ex.toString());
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
            System.err.println("Data.UserData.checkUser() "+ex.toString());
//            Logger.getLogger(UserData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return userPassword.equalsIgnoreCase(passWordTemp) && !"".equals(passWordTemp);
        
    }

    public boolean downloadData(Socket socket, String userName) {
        
        while(true){
            try {
                this.dataInputStream = new DataInputStream(socket.getInputStream());
                
                this.nameDocument = this.dataInputStream.readUTF();
                
                this.totalSize = this.dataInputStream.readInt();
                this.buffer = new byte[this.totalSize];
                
                System.out.println("Data.UserData.downloadData() - Name: "+this.nameDocument+"totalSize: "+this.totalSize);
                
                this.fileOutputStream = new FileOutputStream("Carpetas\\"+userName+"\\"+this.nameDocument);
                this.bufferedOutputStream = new BufferedOutputStream(this.fileOutputStream);
                this.bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                
                for (int i = 0; i < this.buffer.length; i++) {
                    this.buffer[i] = (byte)this.bufferedInputStream.read();
                }
                
                this.bufferedOutputStream.write(this.buffer);
                this.bufferedOutputStream.flush();
                this.fileOutputStream.flush();
                this.fileOutputStream.close();
                System.out.println("Data.UserData.downloadData() Recibido: "+this.nameDocument);
                return true;
            } catch (IOException ex) {
                System.err.println("Data.UserData.downloadData() "+ex.toString());
                return false;
//                Logger.getLogger(UserData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean uploadData(String nameDocument, String userName){
        try {
            this.document = new File("Carpetas\\"+userName+"\\"+nameDocument);
            this.totalSize = (int) this.document.length();
            this.dataOutputStream.writeUTF(this.document.getName());
            this.dataOutputStream.writeInt(this.totalSize);
            
            this.fileInputStream = new FileInputStream("Carpetas\\"+userName+"\\"+nameDocument);
            this.bufferedInputStream = new BufferedInputStream(this.fileInputStream);
            
            this.buffer = new byte[this.totalSize];
            
            this.bufferedInputStream.read(this.buffer);
            
            for (int i = 0; i < this.buffer.length; i++) {
                this.bufferedOutputStream.write(buffer[i]);
            }
            this.bufferedInputStream.close();
            this.bufferedOutputStream.flush();
            this.dataOutputStream.flush();
            System.out.println("Data.UserData.upload() Enviado: "+nameDocument);
            return true;
        } catch (IOException ex) {
            System.err.println("Data.UserData.upload() "+ex.toString());
            return false;
//            Logger.getLogger(UserData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}