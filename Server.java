import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public class Server {
	ByteBuffer buff = ByteBuffer.allocate(1024) ;
  class Player {
	  ByteBuffer buff = ByteBuffer.allocate(1024) ;
	  BufferedReader in;
	  AsynchronousSocketChannel client;
    char name;
    int nHands;
    
    
    public Player(int num, AsynchronousSocketChannel future) throws Exception {
        name = (char)('A'+num);
        client = future;
      }
    

    void close() throws Exception {
      ((AsynchronousChannel) server).close();
      ((AsynchronousChannel) server).close();
    }
  };
  public void Connect() throws Exception {
  	
  }
  int num = 0;
  List<String> list = new ArrayList<>();
  Future<AsynchronousSocketChannel> server;
  BiConsumer<Throwable, Void> error = (t, v) -> t.printStackTrace();
  AsynchronousServerSocketChannel s ;
	String top_card = "";
	int skip_count = 0;
  Player[] players;
  
  public <V, A> CompletionHandler<V, A> completion(BiConsumer<V, A> complete, BiConsumer<Throwable, A> error){
      return new CompletionHandler<V, A>() {
          @Override
          public void completed(V result, A attachment) {
              complete.accept(result, attachment);
              
          }

          @Override
          public void failed(Throwable exc, A attachment) {
              error.accept(exc, attachment);
          }
      };
  }
  

 


void sendMessage(Player player, String msg) {
	  ByteBuffer buff = Charset.forName("UTF-8").encode(msg);
	  System.out.println(Charset.forName("UTF-8").encode(msg));
	  try {
		player.client
		         .write(buff).get();
	} catch (InterruptedException | ExecutionException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	}
    System.out.println(msg);
  }

  String recvMessage(Player player) throws Exception {
	  /*ByteBuffer buff = ByteBuffer.allocate(1024);
	  AsynchronousSocketChannel c = player.client.get();
	  c.read(buff, null, new CompletionHandler<Integer, ByteBuffer>() {

		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			
			// TODO 自動生成されたメソッド・スタブ
			buff.flip();
	        list.add(Charset.forName("UTF-8").decode(buff).toString());
	        System.out.println(list);
	           //System.out.println(list);
	           // if(buff.hasRemaining()){
	           //     worker.read(buff, null, this);
	           //     return;
	           // }
	          
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			// TODO 自動生成されたメソッド・スタブ
			
		}
		  
	  });
	  //player.client.get().read(buff);
	  buff.flip();*/
    //System.out.println(buff + "::" + Charset.forName("UTF-8").decode(buff).toString());
	  while(true) {
		  Thread.sleep(100);
	  if(list.size() != 0) {
		  String str = list.get(0);
		  list.remove(0);
		  return str;
	  }
	  }
  }

  public void start(Player[] players) throws Exception {
    int turn = 0;

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

	  s =
		      AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
    players = new Player[3];
      s.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

  		@Override
  		public void completed(AsynchronousSocketChannel socket, Void attachment) {
  			
			try {
				players[num] = new Player(num, socket);
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			sendMessage(players[num], String.format("CN %c", players[num].name));
			num++;
			
  			socket.read(buff, buff, new CompletionHandler<Integer, ByteBuffer>() {
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
  		                socket.read(buff, null, this);
  		                return;
  		            }
  		            
  		            

  		        }
  				@Override
  				public void failed(Throwable exc, ByteBuffer attachment) {
  					// TODO 自動生成されたメソッド・スタブ

  				}

  		});
  			if(num <= 2) {
  			s.accept(null, this);
  			}else {
  				try {
					DC();
				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
  			}
  		}
  		
		@Override
  		public void failed(Throwable exc, Void attachment) {
  			// TODO 自動生成されたメソッド・スタブ

  		}

  	});
      while(true) {
    	  Thread.sleep(100);
      }
    
    
    
  }

public void DC() throws Exception {
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


