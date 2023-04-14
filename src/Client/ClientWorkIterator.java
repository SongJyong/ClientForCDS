package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

public class ClientWorkIterator implements Runnable{
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
                this.client.getConnection(socketChannel, Clients.affinityOption);
            }
            this.count += this.requestTimes;
            setRequestTimes(0);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private void startClient() {
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true); // blocking channel
                    socketChannel.connect(new InetSocketAddress("localhost",6565)); // localhost:6565 포트로 연결 요청을 한다.
                } catch (Exception e) {
                    if (socketChannel.isOpen()) {
                        stopClient();
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
