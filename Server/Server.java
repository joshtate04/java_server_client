
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author joshtate
 */
public class Server {
    private static int port;
    private static ServerSocket server_socket;
    private static String host = "localhost";
    private static ExecutorService tpool;
    private static int max_threads = 10;
    
    public static void main(String args[]){
        
        
        if (args.length < 1 || args[0].isEmpty())
            port = 8088;
        else
            port = Integer.parseInt(args[0]);
        
        try {
            server_socket = new ServerSocket(port);
            server_socket.setSoTimeout(10000);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        tpool = Executors.newFixedThreadPool(max_threads);
        
        //<editor-fold defaultstate="collapsed" desc=" Startup display ">
        System.out.println("    _____   ______   __     __   ______");
        System.out.println("   |     \\ /      \\ |  \\   |  \\ /      \\ ");
        System.out.println("    \\$$$$$|  $$$$$$\\| $$   | $$|  $$$$$$\\");
        System.out.println("      | $$| $$__| $$| $$   | $$| $$__| $$");
        System.out.println(" __   | $$| $$    $$ \\$$\\ /  $$| $$    $$");
        System.out.println("|  \\  | $$| $$$$$$$$  \\$$\\  $$ | $$$$$$$$");
        System.out.println("| $$__| $$| $$  | $$   \\$$ $$  | $$  | $$");
        System.out.println(" \\$$    $$| $$  | $$    \\$$$   | $$  | $$");
        System.out.println("  \\$$$$$$  \\$$   \\$$     \\$     \\$$   \\$$");
    
        System.out.print("\n" + 
                         "–––––––––––––––––––––––––––––––––––––––––––\n" +
                         "           JOSH'S SERVER STARTING          \n" +
                         "–––––––––––––––––––––––––––––––––––––––––––\n\n");
        //</editor-fold>
        
        //Display host and port information
        System.out.println("Port:        " + port);
        System.out.println("Host:        " + host);
        while(true){
            try {
                //Accept incoming connections
                Socket socket = server_socket.accept();
                
                //Client has connected
                System.out.println("Connecting to a client " + socket.toString());
                
                //Add new thread to accept commands
                tpool.execute(new ServerSocketRunnable(socket));
            } catch (IOException ex) {}
        }
    }
}
