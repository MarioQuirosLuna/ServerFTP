
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
    
    public ListenClient(Socket socket){
        this.socket = socket;
        this.userData = new UserData();
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
                this.sleep(100);
            }
        }catch(IOException e){
            System.out.println("Server.ListenClient.run() "+e);
        } catch (SQLException ex) {
            System.out.println("Server.ListenClient.run() "+ex);
//            Logger.getLogger(ListenClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            System.out.println("Server.ListenClient.run() "+ex);
//            Logger.getLogger(ListenClient.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        String name = getData();
        String passWord2 = getData();
        if(this.userData.checkUser(name,passWord2)){
            this.send.println(MyUtility.EXISTE);
        }else{
            this.send.println(MyUtility.NOEXISTE);
        }
    }
    
    public String getData() throws IOException{
        return this.receive.readLine();
    }
    
}
