package Client;

import Protocol.Request;
import Protocol.Utilities;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    Request request = null;
    public void getConnection(SocketChannel socketChannel, int numberOfConnection){
        try {
            request = new Request( "data", numberOfConnection);
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
