package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class ClientWorkIterator implements Runnable{
    private Selector selector;
    private SocketChannel socketChannel;
    private Client client = new Client();
    private int count = 0;
    private int requestTimes = 0;
    CountDownLatch countDownLatch;
    public ClientWorkIterator(){
        this.startClient();
    }
    protected void setCountDownLatch(CountDownLatch requestCountDown){
        this.countDownLatch = requestCountDown;
    }
    protected void setRequestTimes(int m) { this.requestTimes = m; }
    protected int getCount(){ return this.count; }
    @Override
    public void run(){
        countDownLatch.countDown();
        try {
            countDownLatch.await();
            for (int i = 0; i < this.requestTimes; i++){
                this.client.getConnection(socketChannel);
            }
            this.count += this.requestTimes;
            setRequestTimes(0);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private void startClient() {
        try { // thread 따로 실행할 필요 없음.(nonblock)
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("localhost",6565)); // localhost:6565 포트로 연결 요청을 한다., non-block
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        } catch (Exception e) {
            if (socketChannel.isOpen()) {
                stopClient();
            }
            return;
        }
        Thread t = new Thread(){ // select() block, thread 따로
            @Override
            public void run() {
                while (true) {
                    try {
                        int keyCount = selector.select();
                        if (keyCount == 0) {
                            continue;
                        }
                        Set<SelectionKey> selectedKeys = selector.selectedKeys();

                        Iterator<SelectionKey> iterator = selectedKeys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey selectionKey = iterator.next();
                            iterator.remove();
                            if (selectionKey.isReadable()) {
                                //receive response
                                //selectionKey.interestOps(0);
                            }
                            else if (selectionKey.isConnectable()) {
                                socketChannel.finishConnect(); // block (connect invoke)
                                selectionKey.interestOps(SelectionKey.OP_READ);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        if (socketChannel.isOpen()) {
                            stopClient();
                        }
                        break;
                    }
                }
            }
        };
        t.start();
    }

    // 연결 끊기 코드
    private void stopClient() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close(); // socketChannel 필드가 not null, 현재 닫혀있지 않을 경우 SocketChannel 닫기
            }
        } catch (IOException e) {}
    }
}
