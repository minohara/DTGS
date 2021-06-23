import java.io.*;
import java.net.*;

public class EchoServer {
  public static void main(String[] args) {
    try {
        int port = 8189;
        if ( args.length > 0 )
            port = Integer.parseInt(args[0]);
        ServerSocket s = new ServerSocket( port );
        Socket incoming = s.accept();
        BufferedReader in
            = new BufferedReader(
                  new InputStreamReader( incoming.getInputStream()));
        PrintWriter out 
            = new PrintWriter(incoming.getOutputStream(),true /* autoFlush */);
        out.println( "Hello! Enter BYE to exit." );
        boolean done = false;
        while (!done) {
            String str = in.readLine();
            if ( str == null )
                done = true;
            else {
                out.println( "Echo: " + str );
                System.out.println( "Echo: " + str );
                if ( str.trim().equals("BYE") )
                    done = true;
            }
        }
        incoming.close();
    }
    catch (Exception e) {
        System.out.println(e);
    }
  }
}
