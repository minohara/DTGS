import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Client {
  char name;

  public ArrayList<String> cards;
  final String n = "3456789TJQKA2";
  final int timeOut = 10000;
  ByteBuffer[] buff = new ByteBuffer[2];
  String[] rmsg = new String[2];
  @SuppressWarnings("unchecked")
  ArrayList<String>[] cmdLists = new ArrayList[2];
  AsynchronousSocketChannel[] s = new AsynchronousSocketChannel[2];
  int nConnect = 0;
  int nRead = 0;
  String myName;
  Thread mainThread;
  int[] status = new int[2];

  // 接続処理のハンドラ
  CompletionHandler<Void, Integer> connectHander = new CompletionHandler<Void, Integer>() {
    @Override
    public void completed(Void result, Integer attachment) {
      // 接続が完了したら サーバ番号を表示
      int sid = attachment.intValue();
      status[sid] = 1;
      buff[sid] = ByteBuffer.allocate(1024);
      cmdLists[sid] = new ArrayList<String>();
      try {
        InetSocketAddress isa = (InetSocketAddress)s[sid].getRemoteAddress();
        System.out.format("Conneceted to server:%s:%d\n",	isa.getHostString(), isa.getPort());
      }
      catch( IOException e ) {}
      nConnect += 1; // 接続数をカウントアップ
      if (nConnect >= 2) { // 2つのサーバに接続したら，メインスレッドに通知
        mainThread.interrupt();
      }
    }
    @Override
    public void failed(Throwable exc, Integer attachment) {
    }
  };

  // コマンド読み込み処理のハンドラ
  CompletionHandler<Integer, Integer> readCommandHander
  = new CompletionHandler<Integer, Integer>() {
    @Override
    public void completed(Integer nData, Integer attachment) {
      int sid = attachment.intValue();
      buff[sid].flip();
      while (buff[sid].hasRemaining()) {
        int length = buff[sid].getInt();
        byte[] msg = new byte[1024];
        buff[sid].get(msg, 0, length);
        //System.out.format("CMD:%s\n", new String(msg, Charset.forName("UTF-8")));
        cmdLists[sid].add(new String(msg, Charset.forName("UTF-8")));
      }
      buff[sid].clear();
      nRead += 1; // 読み込みサーバ数をカウントアップ
      if (nRead >= 2) {
        mainThread.interrupt();
      }
    }
    @Override
    public void failed(Throwable exc, Integer attachment) {
    }
  };

  public Client(String[] servers, int[] ports) throws Exception {
    mainThread = Thread.currentThread(); // メインのスレッドを参照する
    cards = new ArrayList<String>();
    for (int i = 0; i < 2; i++ ) {
      s[i] = AsynchronousSocketChannel.open(); // サーバ iのチャネルをオープンして接続する
      s[i].connect(new InetSocketAddress(servers[i], ports[i]), Integer.valueOf(i), connectHander);
    }
    try {
      mainThread.sleep(timeOut); // 接続完了を大気する
    }
    catch ( InterruptedException e ) {
    }
    Thread.interrupted();
    for (int i = 0; i < 2; i++) {
      if ( buff[i] != null )
        buff[i].clear();
    }
    System.out.println(nConnect);
  }

  public synchronized void getCommand() {
    mainThread = Thread.currentThread();
    nRead = 0;
    for (int i = 0; i < 2; i++ ) {
      if (cmdLists[i] != null && cmdLists[i].isEmpty()) {
        if (status[i] > 0) {
          System.out.println("Reading command ...");
          s[i].read(buff[i], Integer.valueOf(i), readCommandHander);
        }
      }
      else {
        nRead++;
      }
    }
    if ( nRead == 0) {
      try {
        mainThread.sleep(timeOut);
      }
      catch ( InterruptedException e ) {
      }
      Thread.interrupted();
    }
    for (int i = 0; i < 2; i++ ) {
      if (status[i] >= 1 && cmdLists[i] != null && !cmdLists[i].isEmpty()
        && cmdLists[i].get(0).startsWith("CN ")) {
        if ( myName != null ) {
          if ( !myName.equals(cmdLists[i].get(0).substring(3)) ) {
            System.err.println("Name mismatch!!\n");
            System.exit(1);
          }
        }
        else {
          myName = cmdLists[i].get(0).substring(3);
        }
        status[i]++;
        System.out.format("%d MyName is %s\n", i, myName );
        cmdLists[i].remove(0);
      }
      else if (status[i] >= 2 && cmdLists[i] != null && !cmdLists[i].isEmpty()
       && cmdLists[i].get(0).startsWith("DC ")) {
        System.out.println(cmdLists[i].get(0));
        cmdLists[i].remove(0);
      }
    }
  }
/*
@Override
public void failed(Throwable exc, Void attachment) {
}
});
AsynchronousSocketChannel s2 = AsynchronousSocketChannel.open();
s2.connect(new InetSocketAddress(server2, port2)).get();
cards = new ArrayList<String>();
ByteBuffer buff2 = null;
String str = null;
while (true) {
Thread.sleep(1);
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
*/
/*
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
*/
/*
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

*/

  public void message(String msg) {
    System.out.println( name + ">" + msg );
  }

  public static void main(String[] args) throws Exception {
    String[] servers = new String[2];
    int[] ports = {8189, 8190};
    for (int i = 0; i < 2; i++ ) {
      servers[i] = "localhost";
      if (args.length > (2 * i)) {
        servers[i] = args[2 * i];
      }
      if (args.length > (2 * i + 1)) {
        ports[i] = Integer.parseInt(args[2 * i + 1]);
      }
    }
    Client client = new Client(servers, ports);
    while (true) {
      client.getCommand();
      Thread.sleep(100);
    }
    /*
    synchronized(client) {
      client.wait();
    }
    */
  }
}
