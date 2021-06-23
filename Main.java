import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
	static ArrayList<String> cards = new ArrayList<>();//すべてのカード
	static ArrayList<String> a_cards = new ArrayList<>();//Aさんのカード
	static ArrayList<String> b_cards = new ArrayList<>();//Bさんのカード
	static ArrayList<String> c_cards = new ArrayList<>();//Cさんのカード
	static String top_card = "";
	static int skip_count = 0;

	static String n = "3456789TJQKA2";
	static String m = "SHDC";
	public static void main(String[] args) {

		//カード生成
		for(int i = 0;i < m.length();i++){
			for(int j = 0;j < n.length();j++) {
				cards.add(String.valueOf(m.charAt(i)) +  String.valueOf(n.charAt(j)));
			}
		}

		//カード配布
		for(int i = 0; i < 52;i++) {
			if(i < 17) {
				int r = new Random().nextInt(cards.size());
				a_cards.add(cards.get(r));
				cards.remove(r);
			}else if(i < 34) {
				int r = new Random().nextInt(cards.size());
				b_cards.add(cards.get(r));
				cards.remove(r);
			}else {
				int r = new Random().nextInt(cards.size());
				c_cards.add(cards.get(r));
				cards.remove(r);
			}
		}

		int turn = new Random().nextInt(3);

		if(turn == 0) {
			System.out.println("Aさんが親です");
		}else if(turn == 1) {
			System.out.println("Bさんが親です");
		}else {
			System.out.println("Cさんが親です");
		}

		while(true){

			if(turn % 3 == 0) {
				System.out.println("Aさんの番です。手持ちのカードは");
				for(int j = 0; j < a_cards.size();j++) {
					System.out.print(a_cards.get(j) + ",");
				}
				System.out.println();
				System.out.println("場のトップは" + ( top_card.equals("") ? "なし" : top_card ) + "です");

				input(a_cards);
				turn++;
			}else if(turn % 3 == 1) {
				System.out.println("Bさんの番です。手持ちのカードは");
				for(int j = 0; j < b_cards.size();j++) {
					System.out.print(b_cards.get(j) + ",");
				}
				System.out.println();
				System.out.println("場のトップは" + ( top_card.equals("") ? "なし" : top_card ) + "です");

				input(b_cards);
				turn++;
			}else if(turn % 3 == 2) {
				System.out.println("Cさんの番です。手持ちのカードは");
				for(int j = 0; j < c_cards.size();j++) {
					System.out.print(c_cards.get(j) + ",");
				}
				System.out.println();
				System.out.println("場のトップは" + ( top_card.equals("") ? "なし" : top_card ) + "です");

				input(c_cards);
				turn++;
			}
			if(skip_count == 2) {//スキップ処理
				top_card = "";
				skip_count = 0;
			}

			if(a_cards.size() == 0){//勝利判定
				System.out.println("Aさんの勝利");
				break;
			}else if(b_cards.size() == 0) {
				System.out.println("Bさんの勝利");
				break;
			}else if(c_cards.size() == 0) {
				System.out.println("Cさんの勝利");
				break;
			}
		}

	}
	static void input(ArrayList<String> c) {
		while(true) {
			System.out.println("何を出しますか？(スキップするときは0を入力)");
			Scanner scan = new Scanner(System.in);
			String str = scan.next();

			if (str.equals("0") ) { // スキップ
				skip_count++;
				break;
			} else if ( c.indexOf(str) == -1 ) { // 手札にないカード
				System.out.println("ルールに合っていません");
				continue;
			}
			if ( !top_card.equals("") ) {
				String s = str.substring(1);
				String t = top_card.substring(1);
				if( n.indexOf(s) <=  n.indexOf(t)) {
					System.out.println("ルールに合っていません"+s+","+t);
					continue;
				}
			}
			top_card = c.get(c.indexOf(str));
			c.remove(c.indexOf(str));
			break;
		}
	}
}
