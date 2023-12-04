package org.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 7777);
            System.out.println("Connected!");
            // get the output stream from the socket.
            OutputStream outputStream = socket.getOutputStream();
            // create a data output stream from the output stream so we can send data through it
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            while(true) {
                Scanner scanner = new Scanner(System.in);  // Create a Scanner object
                String theString = scanner.nextLine();  // Read user input
                // write the message we want to send
                dataOutputStream.writeUTF(theString);
                dataOutputStream.flush(); // send the message
                if(theString.equals("quit")){
                    System.out.println("Closing socket and terminating program.");
                    dataOutputStream.close(); // close the output stream when we're done.
                    socket.close();
                    break;
                }
            }
        }
        catch(IOException err){
            throw new RuntimeException("Client error");
        }
    }
}
