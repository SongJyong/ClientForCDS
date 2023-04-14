package Client;

import Protocol.Request;
import Protocol.Utilities;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    AtomicInteger requestId = new AtomicInteger();
    Request request = null;
    public void getConnection(SocketChannel socketChannel, Boolean affinityOption){
        try {
            if (affinityOption) request = new Request(requestId.get(),"data", true);
            else request = new Request(requestId.incrementAndGet(), "data", false);
            ByteBuffer byteBuffer = Utilities.convertObjectToBytes(request);
            byteBuffer.position(0);
            request = null;
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
