
package Server;

import Data.UserData;
import Utility.MyUtility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mario
 */
public class ListenClient extends Thread{

    private Socket socket;
    private PrintWriter send;
    private BufferedReader receive;
    private String nameUser = "";
    private String passWord = "";
    private UserData userData;
    
    public ListenClient(Socket socket) throws IOException{
        this.socket = socket;
        this.userData = new UserData(socket);
    }
    
    @Override
    public void run(){
        try{
            String action;
            this.send = new PrintWriter(socket.getOutputStream(),true);   
            this.receive = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            action = getData();
            this.send.println("Te conectaste");
            
            while(true){
                action = receive.readLine();
                System.out.println("\nRecibe: "+action+"\n");
                
                if(action.equalsIgnoreCase(MyUtility.ADDNEWUSER)){
                    actionAddNewUser();
                }
                if(action.equalsIgnoreCase(MyUtility.LOGINUSER)){
                    actionLoginUser();
                }
                if(action.equalsIgnoreCase(MyUtility.GUARDARENSERVER)){
                    actionSaveData();
                }
                if(action.equalsIgnoreCase(MyUtility.DESCARGARDESERVER)){
                    actionGetData();
                }
                if(action.equalsIgnoreCase(MyUtility.OBTENERDIRECTORIOS)){
                    actionGetFolders();
                }
            }
        }catch(IOException | SQLException e){
            System.out.println("Server.ListenClient.run() "+e);
        }
//            Logger.getLogger(ListenClient.class.getName()).log(Level.SEVERE, null, ex);
//            Logger.getLogger(ListenClient.class.getName()).log(Level.SEVERE, null, ex);

    }
    
    public void actionAddNewUser() throws IOException, SQLException{
        String name = getData();
        String passWord1 = getData();
        if(this.userData.addNewUser(name, passWord1)){
            this.send.println(MyUtility.REGISTRADO);
        }else{
            this.send.println(MyUtility.NOREGISTRADO);
        }
    }
    
    public void actionLoginUser() throws IOException{
        this.nameUser = getData();
        this.passWord = getData();
        if(this.userData.checkUser(this.nameUser,this.passWord)){
            this.send.println(MyUtility.EXISTE);
        }else{
            this.send.println(MyUtility.NOEXISTE);
        }
    }
    
    private void actionSaveData() {
        while(!this.userData.downloadData(this.socket,this.nameUser));
    }
    
    private void actionGetData() throws IOException{
        String nameDocument = getData();
        this.userData.uploadData(nameDocument, this.nameUser);
    }
    
    public void actionGetFolders() throws IOException{
        this.userData.uploadFolder(this.nameUser,this.send);
    }
    
    public String getData() throws IOException{
        return this.receive.readLine();
    }
    
}
