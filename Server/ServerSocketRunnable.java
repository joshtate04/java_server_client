
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author joshtate
 */
public class ServerSocketRunnable implements Runnable {
    private static Socket socket;
    private static int connection;
    private int timeout = 600000; //10 minute timeout to help avoid zombie threads
    private static long current_time;
    private static long last_command_time;
    public ServerSocketRunnable(Socket socket){
        this.socket = socket;
        this.connection = connection;
    }
    
    @Override
    public void run(){
        boolean running = true;
        DateFormat date_format;
        
        try{
            //Set last command time to now
            last_command_time = System.currentTimeMillis();
            
            //Get IO streams
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            
            //Create scanner and set delimeter
            Scanner sc = new Scanner(is);
            sc.useDelimiter("\0");
            while(running){
                //Check for closed socket
                if(socket.isClosed())
                    break;
                
                //Get current time to check for timeouts
                current_time = System.currentTimeMillis();
                
                //Check for timeout
                if (current_time - last_command_time > timeout){
                    os.write("TIMEOUT\0".getBytes());
                    System.out.println("Client on "+ socket.toString() + " timed out");
                    break;
                }
                
                if(sc.hasNext()){
                    //Set last command time to now to check later for timeout
                    last_command_time = System.currentTimeMillis();
                    String command = sc.next();
                    if(command.contains(">")){
                        String parts[] = command.split(">");
                        if(parts.length > 0){
                            switch(parts[0]){
                                //Return option string back to client
                                case "ECHO":
                                    String echo = "ECHO: ";
                                    for(int i = 1; i < parts.length; ++i)
                                        echo += parts[i];
                                    
                                    os.write((echo+"\0").getBytes());
                                    break;
                                    
                                //Return current time to the client
                                case "TIME":
                                    date_format = new SimpleDateFormat("hh:mm:ss aa");
                                    os.write(("TIME: "+date_format.format(new Date(current_time))+"\0").getBytes());
                                    break;
                                    
                                //Return current date to the client
                                case "DATE":
                                    date_format = new SimpleDateFormat("d MMMM yyyy");
                                    os.write(("DATE: "+date_format.format(new Date(current_time))+"\0").getBytes());
                                    break;
                                    
                                //Sends clear screen command back to client
                                case "CLS":
                                    os.write("CLS\0".getBytes());
                                    break;
                                    
                                //Displays available commands on the server
                                case "?":
                                    os.write(("AVAILABLE SERVICES\n"+
                                              "END\n"+
                                              "ECHO\n"+
                                              "TIME\n"+
                                              "DATE\n"+
                                              "?\n"+
                                              "CLS\n\0").getBytes());
                                    break;
                                    
                                //Terminates the connection
                                case "END":
                                    running = false;
                                    System.out.println("Server socket: Closing client connection...");
                                    os.write("TERMINATE\0".getBytes());
                                    break;
                                    
                                //Unrecognized command
                                default:
                                    os.write(("ERROR: Unrecognized command.\0").getBytes());
                                    break;
                            }
                        }
                        //Not a valid input
                        else
                            os.write(("ERROR: Unrecognized command.\0").getBytes());
                    }
                    //Client terminates the command due to:
                    //  Exiting system
                    //  Connecting to a new server
                    else if(command.equals("TERMINATE")){
                        System.out.println("Server socket: Closing client connection...");
                        running = false;
                        break;
                    }
                }
                try { Thread.sleep(100); }
                catch (InterruptedException ex) {break;}
            }
            
            //Close streams and socket, then end thread
            os.close();
            is.close();
            socket.close();
        } catch (IOException ex) {}
    }
}
