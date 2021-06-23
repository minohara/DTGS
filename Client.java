import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Client {
  public ArrayList<String> cards;
  String n = "3456789TJQKA2";
  char name;
  public Client(int num) {
    cards = new ArrayList<>();
    name = (char)('A'+num);
  }

  String turn( String top_card ) {
    System.out.println( name + "さんの番です。手持ちのカードは");
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
        return "0";
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
      cards.remove(cards.indexOf(str));
      return str;
    }
  }

  public void message(String msg) {
    System.out.println( name + ">" + msg );
  }
}
