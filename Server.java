import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class Server {
	ByteBuffer buff = ByteBuffer.allocate(1024) ;

	AsynchronousSocketChannel peerServer; // 別のサーバとの通信チャネル
	static final int waitPeer = 10000; // 別のサーバの起動を待つ時間

	class Player {
		ByteBuffer buff = ByteBuffer.allocate(1024) ;
		BufferedReader in;
		AsynchronousSocketChannel ch;
		char name;
		int nHands;
		final long writeWait = 3000;


		public Player(int num, AsynchronousSocketChannel ch) throws Exception {
			name = (char)('A'+num);
			this.ch = ch;
		}

		CompletionHandler<Integer, Void> nullHandler = new CompletionHandler<Integer, Void>(){
			@Override
			public void completed(Integer nData, Void attachment) {
			}
			@Override
			public void failed(Throwable exc, Void attachment) {
			}
		};
		void sendMessage(String msg) {
			Thread th = Thread.currentThread();
			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putInt(msg.length());
			System.out.println(msg.length());
			buff.put(Charset.forName("UTF-8").encode(msg));
			buff.flip();
			ch.write(buff, null, nullHandler);
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

	}socket.read(buff, buff, new CompletionHandler<Integer, ByteBuffer>() {
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

		void close() throws Exception {
			ch.close();
		}
	};

	int num = 0;
	List<String> list = new ArrayList<>();
	Future<AsynchronousSocketChannel> server;
	BiConsumer<Throwable, Void> error = (t, v) -> t.printStackTrace();
	AsynchronousServerSocketChannel s ;
	String top_card = "";
	int skip_count = 0;
	Player[] players;
	int seed;





	public void start(Player[] players) throws Exception {
		int turn = 0;

		for (int i = 0; i < 3; i++) {
			players[i].sendMessage( String.format("SF %c", 'A'+turn));
		}
		while(true){
			System.out.println( "skip_count:"+skip_count);
			int pid = turn % 3;
			players[pid].sendMessage( String.format("TN %s", top_card) );
			String[] cmd = players[pid].recvMessage(players[pid]).split("\s");
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
					players[i].sendMessage(String.format("WN %c", 'A'+pid));
				}
				break;
			}

		}
	}

  // 第一サーバのコンストラクタ
	public Server() throws Exception {
		Thread th = Thread.currentThread();
		// 非同期サーバソケットチャネルを作って
		AsynchronousServerSocketChannel peer
			= AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(8188));
		// 第二サーバからの接続を受け入れる
		peer.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
			@Override
			public void completed(AsynchronousSocketChannel peer, Void attachment) {
				// 接続されたら ソケットを保存して
				peerServer = peer;
				try {
					// 相手のアドレスとポートを表示
					InetSocketAddress isa = (InetSocketAddress)peer.getRemoteAddress();
					System.out.format("PeerServer:%s:%d\n",	isa.getHostString(), isa.getPort());
				}
				catch( IOException e ) {}
				// 接続待ちを止める
				th.interrupt();
			}
			@Override
			public void failed(Throwable exc, Void attachment) {
			}
		});
		try {
			System.out.println("Waiting for peer ...");
			th.sleep(waitPeer); // 2番目のサーバが接続するのを待つ
		}
		catch  ( InterruptedException e ) {}
	}

	// 第二サーバのコンストラクタ
	public Server(String master) throws Exception {
		Thread th = Thread.currentThread();
		// 非同期ソケットチャネルを作って
		peerServer = AsynchronousSocketChannel.open();
		// 第一サーバに接続する
		peerServer.connect(
			new InetSocketAddress(master, 8188), null, new CompletionHandler<Void, Void>() {
				@Override
				public void completed(Void result, Void attachment) {
					// 接続待ちを止める
					th.interrupt();
				}
				@Override
				public void failed(Throwable exc, Void attachment) {
				}
		});
		try {
			System.out.println("Connecting to peer ...");
			th.sleep(waitPeer); // 1番目のサーバに接続するのを待つ
		}
		catch  ( InterruptedException e ) {}
	}

  // プレイヤー(クライアント)からの接続を待つ
	public synchronized void waitPlayer(int port) throws Exception {
		Thread th = Thread.currentThread();
		System.out.println("Waiting for players ...");
		players = new Player[3];
		AsynchronousServerSocketChannel s
			= AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
		s.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
			// クライアントが接続してきたときの処理
			@Override
			public void completed(AsynchronousSocketChannel socket, Void attachment) {
				try {
					players[num] = new Player(num, socket); // プレイヤーを追加
				} catch (Exception e) {
					e.printStackTrace();
				}
				players[num].sendMessage(String.format("CN %c", players[num].name)); // 初期メッセージを遅る
				num++;
				if(num <= 2) { // プレイヤーの数が2以下だったら
					s.accept(null, this); // 次の接続を待つ
				}
				else {
					// プレイヤーが3人そろったら，接続待ちを止める
					th.interrupt();
				}
			}
			@Override
			public void failed(Throwable exc, Void attachment) {
			}
		});
		try {
			// プレイヤーガ揃うの待つ
			wait();
		}
		catch (InterruptedException e ) {}
	}

	// 	カードの配布
	public void dealCards() throws Exception {
		Thread th = Thread.currentThread();
		ArrayList<String> cards = new ArrayList<>();//すべてのカード
		String n = "3456789TJQKA2";
		String m = "SHDC";
		int nCard = n.length() * m.length();

		Random random = new Random();
		int seed = random.nextInt(10000);
		ByteBuffer msg = Charset.forName("UTF-8").encode(String.format("%d", seed));
		String rmsg;
		if (peerServer != null) {
			peerServer.write(msg);
			peerServer.read(buff, null, new CompletionHandler<Integer, Void>() {
				@Override
				public void completed(Integer nData, Void attachment) {
					th.interrupt();
				}
				@Override
				public void failed(Throwable exc, Void attachment) {
				}
			});
			try {
				th.sleep(waitPeer);
			}
			catch( InterruptedException e ) {
				buff.flip();
				rmsg = Charset.forName("UTF-8").decode(buff).toString();
				random.setSeed(seed * Integer.parseInt(rmsg));
			}
		}
		//カード生成
		for(int i = 0;i < m.length();i++){
			for(int j = 0;j < n.length();j++) {
				cards.add(String.valueOf(m.charAt(i)) +  String.valueOf(n.charAt(j)));
			}
		}
		//カード配布
		//		for(int i = 0; i < nCard;i++) {
		for(int i = 0; i < 9;i++) {
			int r = random.nextInt(cards.size());
			players[i % 3].sendMessage(String.format("DC %02d %s", i, cards.get(r)));
			cards.remove(r);
			players[i % 3].nHands += 1;
		}
		// ゲームスタート
		start(players);
		for (int i = 0; i < players.length; i++) {
			players[i].close();
		}
		s.close();
	}

	public static void main(String[] args) throws Exception {
		Server server;
		if (args.length < 2) {
			server = new Server();
		}
		else {
			server = new Server(args[1]);
		}
		if (args.length < 1) {
			server.waitPlayer(8189);
		}
		else {
			server.waitPlayer(Integer.parseInt(args[0]));
		}
		server.dealCards();
		/*
		synchronized (server) {
			server.wait();
		}
		*/
	}
}
