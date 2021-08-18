import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Server {
  class Player {
    BufferedReader in;
    PrintWriter out;
    char name;
    int nHands;

    public Player(int num, Socket s) throws Exception {
      in = new BufferedReader(new InputStreamReader( s.getInputStream()));
      out = new PrintWriter(s.getOutputStream(),true /* autoFlush */);
      name = (char)('A'+num);
    }

    void close() throws Exception {
      in.close();
      out.close();
    }
  };

	String top_card = "";
	int skip_count = 0;
  Player[] players;

  void sendMessage(Player player, String msg) {
    player.out.println(msg);
    System.out.println(msg);
  }

  String recvMessage(Player player) throws Exception {
    String msg = player.in.readLine();
    System.out.println(msg);
    return msg;
  }

  public void start(Player[] players) throws Exception {
    int turn = new Random().nextInt(3);

    for (int i = 0; i < 3; i++) {
      sendMessage( players[i], String.format("SF %c", 'A'+turn));
    }

    while(true){
      System.out.println( "skip_count:"+skip_count);
      int pid = turn % 3;
      sendMessage( players[pid], String.format("TN %s", top_card) );
      String[] cmd = recvMessage(players[pid]).split("\s");
      if ( cmd[0].equals("PC") ) {
        if ( cmd.length == 1 ) {
          skip_count++;
        }
        else {
          top_card = cmd[1];
          players[pid].nHands -= 1;
          skip_count=0;
        }
      }
      turn++;
      if(skip_count == 2) {//スキップ処理
        top_card = "";
        skip_count = 0;
      }
      if( players[pid].nHands == 0){//勝利判定
        for (int i = 0; i < 3; i++ ) {
          sendMessage( players[i], String.format("WN %c", 'A'+pid));
        }
        break;
      }
    }
  }

  public Server(int port) throws Exception {
    ServerSocket s = new ServerSocket( port );
    players = new Player[3];
    for (int i = 0; i < players.length; i++ ) {
      players[i] = new Player(i, s.accept());
      sendMessage(players[i], String.format("CN %c", players[i].name));
    }
    ArrayList<String> cards = new ArrayList<>();//すべてのカード
  	String n = "3456789TJQKA2";
  	String m = "SHDC";
    int nCard = n.length() * m.length();
		//カード生成
		for(int i = 0;i < m.length();i++){
			for(int j = 0;j < n.length();j++) {
				cards.add(String.valueOf(m.charAt(i)) +  String.valueOf(n.charAt(j)));
			}
		}
		//カード配布
//		for(int i = 0; i < nCard;i++) {
		for(int i = 0; i < 9;i++) {
			int r = new Random().nextInt(cards.size());
      sendMessage(players[i % 3], String.format("DC %s", cards.get(r)));
			cards.remove(r);
      players[i % 3].nHands += 1;
    }
    start(players);
    for (int i = 0; i < players.length; i++) {
      players[i].close();
    }
    s.close();
  }



  public static void main(String[] args) throws Exception {
    int port = 8189;
    if ( args.length > 0 )
        port = Integer.parseInt(args[0]);
    Server server = new Server(port);
    //server.start();
  }
}
