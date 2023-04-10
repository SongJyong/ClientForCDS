package Client;

import Protocol.Request;
import Protocol.Utilities;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public void getConnection(SocketChannel socketChannel, Boolean affinityOption){
        try {
            Request request = new Request("data",affinityOption);
            ByteBuffer byteBuffer = Utilities.convertObjectToBytes(request);
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
