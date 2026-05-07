import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple TCP server that listens on port 12345.
 * It accepts client connections, receives username, score, and time taken,
 * appends the data to a leaderboard file, and returns the client's ranking along with the top 3 players.
 */

public class SimpleServerTCP {
/**
     * The main method that starts the server and listens for client connections.
     * For each client, it spawns a new thread to handle their data independently.
     */
    public static void main(String[] args) throws IOException 
    {
        ServerSocket serverSocket = new ServerSocket(12345);                                    // Creates server listening on port 12345
        System.out.println("Server running, waiting for Client.....");

        while (true)                                                                                 // Keeps accepting clients
        {
            Socket client = serverSocket.accept();                                                  // Socket to talk to client
            System.out.println("Received client request!");

            Thread th = new Thread(                                                                 //Thread so it can take in multiple users
                new Runnable()                                                                      // Runnable interface
                {
                    public void run()
                    {
                       try 
                       {
                        leaderboard(client);                                                        // Calls leaderbord
                       } 
                       catch (IOException e) 
                       {
                        e.printStackTrace();
                       }   
                    }
                });
            th.start();
        }
    }

    /**
     * Handles leaderboard logic for a connected client.
     * This includes receiving user data, updating the leaderboard file,
     * computing the user's rank, and sending back the top 3 leaderboard entries.
     * The method is synchronized to ensure file consistency when accessed by multiple threads.
     */
private static synchronized void leaderboard(Socket client) throws IOException                      // Method for only one client at a time
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));    // Gets input from client and converts to characters
        String username = in.readLine();
        int score = Integer.parseInt(in.readLine());
        int time = Integer.parseInt(in.readLine());
    
        FileWriter fw = new FileWriter("leaderboard.txt",true);                   // Appends info into leaderboard.txt
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(username + ","+ score + ","+ time+ "\n");
        bw.close();

        PrintWriter out = new PrintWriter(client.getOutputStream(), true);             // Sends message to client
        out.println("Recieved info!");

        List<String[]> entry = new ArrayList<>();                                                 // Stores every line in the file as an array list so its easy to split
        try 
        {
            FileReader fReader = new FileReader ("leaderboard.txt");
            BufferedReader bReader = new BufferedReader(fReader); 
            String line;
            while ((line = bReader.readLine()) != null) 
            {
                String[] parts = line.split(",");
        
                if (parts.length == 3) 
                {
                    entry.add(parts);
                }
            }
            bReader.close();
            fReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } 

        entry.sort((a, b) -> 
    {
        int Ascore = Integer.parseInt(a[1]);
        int Bscore = Integer.parseInt(b[1]);
        int Atime = Integer.parseInt(a[2]);
        int Btime = Integer.parseInt(b[2]);

        if (Bscore != Ascore) 
        {
            return Integer.compare(Bscore, Ascore);
        }
        return Integer.compare(Atime, Btime);
    });

                                                                                                   //Looping through sorted entries to find users current standing
    int place = 1;                                                                                 // Starts from 1
    for (String[] info : entry)                                                                   // Loops through entry (array containting leaderboard contents)
    {
        if (info[0].equals(username) && Integer.parseInt(info[1]) == score && Integer.parseInt(info[2])==time) 
        {
            break;                                                                               // If comparison is true you've found the place

        }
        place++;
    }

    out.println("Your rank: "+place);
    out.println("Top 3 Players:");
    for (int i = 0; i < 3; i++)                                                                 // Loop 3 times to print top 3
    {
        if (i>=entry.size())                                                                    // If less than 3 entries, break                
        {
            break;
        }

        String [] info = entry.get(i);
        out.println((i+1)+"."+ "Name: "+ info[0] + ", Score: "+ info[1]+ ", Time Taken: "+ info[2]);
    }

    in.close();
    out.close();
    client.close();
    }

}