import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;public class Server {
  public Client[] client;
	String top_card = "";
	int skip_count = 0;

  public Server() {
    client = new Client[3];
    client[0] = new Client(0);
    client[1] = new Client(1);
    client[2] = new Client(2);
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
		for(int i = 0; i < nCard;i++) {
			int r = new Random().nextInt(cards.size());
			client[i % 3].cards.add(cards.get(r));
			cards.remove(r);
    }
  }

  public void start() {
    int turn = new Random().nextInt(3);

    for (int i = 0; i < 3; i++) {
      client[i].message( String.format("%cさんが親です", 'A'+turn));
    }

    while(true){
      String card = client[turn%3].turn(top_card);
      if (card.equals("0")) {
        skip_count++;
      }
      else {
        top_card = card;
      }
      turn++;
      if(skip_count == 2) {//スキップ処理
        top_card = "";
        skip_count = 0;
      }
      if( client[turn%3].cards.size() == 0){//勝利判定
        for (int i = 0; i < 3; i++ ) {
          client[i].message( String.format("%cさんの勝利", 'A'+turn));
        }
        break;
      }
    }
  }

  public static void main(String[] args) {
    Server server = new Server();
    server.start();
  }
}
