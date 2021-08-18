import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.*;
import java.net.*;

public class Client {
  BufferedReader in;
  PrintWriter out;
  char name;


  public ArrayList<String> cards;
  String n = "3456789TJQKA2";

  public Client(String server, int port) throws Exception {
    Socket s = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader( s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(),true /* autoFlush */);
    cards = new ArrayList<String>();
    while ( true ) {
      String str = in.readLine();
      String[] cmd = str.split("\s");
      if ( cmd[0].equals("CN") ) {
        name = cmd[1].charAt(0);
        System.out.format("あなたは %c です\n", name);
      }
      else if ( cmd[0].equals("DC") ) {
        cards.add(cmd[1]);
        System.out.format("%s ", cmd[1]);
      }
      else if ( cmd[0].equals("SF") ) {
        System.out.format("%s さんが親です\n", cmd[1]);
      }
      else if ( cmd[0].equals("TN") ) {
        if (cmd.length == 1) {
          turn("");
        }
        else {
          turn(cmd[1]);
        }
      }
      else if ( cmd[0].equals("WN") ) {
        System.out.format("%s さんの勝ちです\n", cmd[1]);
        s.close();
        break;
      }
      else {
        System.out.println(str);
      }
    }
  }

  void play(String card) {
    if ( card.equals("0") ) {
      out.format("PC\n");
    }
    else {
      out.format("PC %s\n", card);
    }
  }

  void turn( String top_card ) {
    System.out.println( name + "さんの番です。手持ちのカードは");
    // 手持ちのカードを表示
    for(int j = 0; j < cards.size();j++) {
      System.out.print( cards.get(j) + ",");
    }
    System.out.println();
    System.out.println("場のトップは" + ( top_card.equals("") ? "なし" : top_card ) + "です");
    while(true) {
      System.out.println("何を出しますか？(スキップするときは0を入力)");
      Scanner scan = new Scanner(System.in);
      String str = scan.next();
      if (str.equals("0") ) { // スキップ
        play("0");
        return;
      } else if ( cards.indexOf(str) == -1 ) { // 手札にないカード
        System.out.println("ルールに合っていません");
        continue;
      } else if ( !top_card.equals("") ) {
        String s = str.substring(1);
        String t = top_card.substring(1);
        if( n.indexOf(s) <=  n.indexOf(t)) {
          System.out.println("ルールに合っていません"+s+","+t);
          continue;
        }
      }
      play(str);
      cards.remove(cards.indexOf(str));
      return;
    }
  }

  public void message(String msg) {
    System.out.println( name + ">" + msg );
  }

  public static void main(String[] args) throws Exception {
    String server = "localhost";
    if (args.length > 0) {
      server = args[0];
    }
    int port = 8189;
    if (args.length > 1) {
      port = Integer.parseInt(args[1]);
    }
    Client client = new Client(server, port);
  }
}
