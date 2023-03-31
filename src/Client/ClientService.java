package Client;

import Protocol.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientService {
    Selector selector;
    SocketChannel socketChannel;
    AtomicInteger count = new AtomicInteger();
    public ClientService(){
        this.startClient();
    }
    private void startClient() {
        try { // thread 따로 실행할 필요 없음.
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
        Thread t = new Thread(){
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
    void stopClient() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close(); // socketChannel 필드가 not null, 현재 닫혀있지 않을 경우 SocketChannel 닫기
            }
        } catch (IOException e) {}
    }

    protected void getConnection(int m) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            for (int i=0; i<m; i++) {
                count.set(count.get() + 1); // client request count
                Future f = executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request request = new Request("getConnection");
                            ByteBuffer byteBuffer = Utilities.convertObjectToBytes(request);
                            socketChannel.write(byteBuffer);
                            byteBuffer.clear();
                            //System.out.printf("getConnection "+Thread.currentThread().getName() + " %d\n");
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                });
                f.get(); // 큰 의미 없음. 리퀘스트 생성 순서대로 메서드 호출 순서 보장 정도?
            }
        } catch (Exception e) {
            System.out.println("getConnection Failed "+Thread.currentThread().getName());
            System.out.println(e.getMessage());
            stopClient();
        }
    }

    protected int getCount() {
        return count.get();
    }
}
