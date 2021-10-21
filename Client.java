import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Client {
  BufferedReader in;
  PrintWriter out;
  BufferedReader in2;
  PrintWriter out2;
  char name;

  public ArrayList<String> cards;
  String n = "3456789TJQKA2";
  ByteBuffer buff = ByteBuffer.allocate(1024) ;
  List<String> list = new ArrayList<>();


  AsynchronousSocketChannel s = AsynchronousSocketChannel.open();
  public Client(String server, int port,String server2, int port2) throws Exception {




	s.connect(new InetSocketAddress(server, port), null, new CompletionHandler<Void, Void>() {

		@Override
		public void completed(Void result2, Void attachment2) {
			s.read(buff, null, new CompletionHandler<Integer, ByteBuffer>() {
		        @Override
		        public void completed(Integer result, ByteBuffer attachment) {
		            System.out.println("CLIENT: read "+result);



		           System.out.println("CLIENT: done "+buff.limit() + ":::"+ buff.capacity()+ ":::"+ buff.position() );
		           buff.flip();
		           list.add(Charset.forName("UTF-8").decode(buff).toString());
		           System.out.println(list);
		           buff.clear();
		           //System.out.println(list);
		            if(buff.hasRemaining()){
		                s.read(buff, null, this);
		                return;
		            }
		            
		            

		        }
				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					// TODO 自動生成されたメソッド・スタブ

				}

		});
		}
		@Override
		public void failed(Throwable exc, Void attachment) {
			// TODO 自動生成されたメソッド・スタブ

		}

	});
	AsynchronousSocketChannel s2 = AsynchronousSocketChannel.open();
	s2.connect(new InetSocketAddress(server2, port2)).get();

    cards = new ArrayList<String>();
	ByteBuffer buff2 = null;

	String str = null;


    while (true) {

    	Thread.sleep(1);
	    //System.out.println(str);
	    //try {
    	//try {

    	//}catch (NullPointerException e) {
    	//	try {
    	//		Thread.sleep(10000); // 10秒(1万ミリ秒)間だけ処理を止める
    	//	} catch (InterruptedException e2) {
    	//	}

    	//}
	    //}catch (SocketException e) {
	    //	System.out.println(e);
	    //}
	   // try {
	    //	s.read(buff).get(9000, TimeUnit.SECONDS);
	    //}catch (SocketException e) {
	    //	buff = buff2;
	    //	System.out.println(e);
	    //}
	    //System.out.println("2" + str2);
    	
    	if(list.size() != 0) {
    		
	    str =  list.get(0);
	    String[] cmd = str.split("\s");
	    if ( cmd[0].equals("CN") ) {
	    	name = cmd[1].charAt(0);
	    	System.out.println(str );
	        System.out.format("あなたは %c です\n", name);
	    }
	    else if ( cmd[0].equals("DC") ) {
	    	
	        cards.add(cmd[1]);
	        System.out.format("%s ", cmd[1]);
	    }
	    else if ( cmd[0].equals("SF") ) {
	    	System.out.println();
	    	System.out.println(str + " : " );
	        System.out.format("%s さんが親です\n", cmd[1]);
	    }
	    else if ( cmd[0].equals("TN") ) {
	    	System.out.println(str + " : ");
	        if (cmd.length == 1) {
	        	turn("");
	        }
	        else {
	        	turn(cmd[1]);
	        }
	    }
	    else if ( cmd[0].equals("WN") ) {
	      	System.out.println(str + " : ");
	        System.out.format("%s さんの勝ちです\n", cmd[1]);
	        s.close();
	        s2.close();
	        break;
	    }
	    else {
        System.out.println(str);

	    }
	    list.remove(0);
    }
    }


  }




void play(String card) {
    if ( card.equals("0") ) {
    	//s.write(ByteBuffer.allocate(32).put(String.format("PC\n").getBytes("UTF-8")));
		ByteBuffer buff = Charset.forName("UTF-8").encode(String.format("PC\n"));
		s.write(buff);
    }
    else {
    	//s.write(ByteBuffer.allocate(32).put(String.format("PC %s\n", card).getBytes("UTF-8")));
		ByteBuffer buff = Charset.forName("UTF-8").encode(String.format("PC %s\n", card));
		s.write(buff);
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
    String server2 = "localhost";
    if (args.length > 2) {
      server2 = args[2];
    }
    int port2 = 8190;
    if (args.length > 3) {
      port = Integer.parseInt(args[3]);
    }
    Client client = new Client(server, port,server2, port2);
  }
}