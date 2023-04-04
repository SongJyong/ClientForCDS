package Client;

import Protocol.Request;
import Protocol.Utilities;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public void getConnection(SocketChannel socketChannel){
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
}
