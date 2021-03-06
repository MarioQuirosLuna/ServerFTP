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
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mario
 */
public class UserData {
    
    private DataOutputStream dataOutputStream = null;
    private BufferedOutputStream bufferedOutputStream = null;
    
    private String nameDocument = "";
    private int totalSize = 0;
    private byte[] buffer = null;
    
    private File document = null;
    Map<Integer, Integer> map = null;
    
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
            System.out.println("Data.UserData.addNewUser() "+ex.toString());
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
            System.out.println("Data.UserData.checkUser() "+ex.toString());
//            Logger.getLogger(UserData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return userPassword.equalsIgnoreCase(passWordTemp) && !"".equals(passWordTemp);
        
    }

    public boolean downloadData(Socket socket, String userName) {
        
        while(true){
            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                
                this.nameDocument = dataInputStream.readUTF();
                
                this.totalSize = dataInputStream.readInt();
                this.buffer = new byte[this.totalSize];
                
                System.out.println("Data.UserData.downloadData() - Name: "+this.nameDocument+"totalSize: "+this.totalSize);
                
                FileOutputStream fileOutputStream = new FileOutputStream("Carpetas\\"+userName+"\\"+this.nameDocument);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                
                int j = this.buffer.length-1;
                for (int i = 0; i < this.buffer.length; i++) {
                    bufferedInputStream.read();
                    this.buffer[j] = (byte)bufferedInputStream.read();
                    j--;
                }
                               
                bufferedOutputStream.write(this.buffer); 
                bufferedOutputStream.flush();
                fileOutputStream.flush();
                fileOutputStream.close();
                System.out.println("Data.UserData.downloadData() Recibido: "+this.nameDocument);
                return true;
            } catch (IOException ex) {
                System.out.println("Data.UserData.downloadData() "+ex.toString());
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
            System.out.println("Data.UserData.uploadData() Name: "+this.document.getName());
            this.dataOutputStream.writeInt(this.totalSize);
            
            FileInputStream fileInputStream = new FileInputStream("Carpetas\\"+userName+"\\"+nameDocument);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            
            this.buffer = new byte[this.totalSize];
            
            bufferedInputStream.read(this.buffer);
            
            
            this.map = new HashMap<Integer, Integer>();

            for (int i = 0; i < buffer.length; i++) {
                this.map.put(i, (int) buffer[i]);
            }

            int key = buffer.length - 1;
            for (int i = 0; i < buffer.length; i++) {
                this.bufferedOutputStream.write(key);
                this.bufferedOutputStream.write(this.map.get(key));
                key--;
            }
            
            bufferedInputStream.close();
            this.bufferedOutputStream.flush();
            this.dataOutputStream.flush();
            System.out.println("Data.UserData.upload() Enviado: "+nameDocument);
            return true;
        } catch (IOException ex) {
            System.out.println("Data.UserData.upload() "+ex.toString());
            return false;
//            Logger.getLogger(UserData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void uploadFolder(String userName,PrintWriter send) throws IOException{
        File folder = new File("Carpetas\\"+userName);
        String[] folders = folder.list();
        
        if(folders != null){
            int size = folders.length;
            send.println(size);
            System.out.println("Envia: "+size);
            for (int i = 0; i < size; i++) {
                send.println(folders[i]);
                System.out.println("Envia name: "+folders[i]);
            }
        }
    }
}