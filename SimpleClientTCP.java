import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
/**
 * A simple TCP client that connects to a server running on localhost at port 12345.
 * It collects a username, score, and time taken from the user via console input,
 * sends this data to the server, and prints out the server’s response.
 */

public class SimpleClientTCP {

    /**
     * The main method that runs the client application.
     * It prompts the user for input, establishes a connection to the server,
     * sends the data, and displays the response from the server.
     */
    public static void main(String[] args) throws UnknownHostException, IOException 
    {
        Scanner scan = new Scanner(System.in);                                                      // To read user input
        
        System.out.println("Enter username: ");                                                   // Prompt to enter username
        String username = scan.nextLine();

        System.out.println("Enter score: ");                                                      // Prompt to enter score
        int score = scan.nextInt();

        System.out.println("Enter time taken (in seconds): ");                                    // Prompt to enter time taken
        int time = scan.nextInt();

        System.out.println("Thankyou! Your information has been sent to the server.\n");

        Socket socket = new Socket("localhost", 12345);                                 // Creates TCP socket connection to server ( local host - server on same machine), (port number - port server is litening on)
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);              // Send data to server through socket
        out.println(username);                                                                    // Send to server
        out.println(score);
        out.println(time);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));   // To read incoming messages from server; converts bytes to characters
        String line;
        while ((line = in.readLine()) != null)
        {
            System.out.println(line);
        }
        
        
        

        in.close();
        out.close();
        socket.close();
        scan.close();
    }

        
}