package Client;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class Clients {
    List<ClientWorkIterator> clientWorkIterators = new Vector<ClientWorkIterator>();
    public static boolean affinityOption = false;
    public void connect(int n) throws ExecutionException, InterruptedException {
        CountDownLatch connectCountDown = new CountDownLatch(n);
        for (int i = 0; i < n; i++){
            Thread connectThread = new Thread(){
                @Override
                public void run() {
                    connectCountDown.countDown();
                    try {
                        connectCountDown.await();
                        ClientWorkIterator c = new ClientWorkIterator();
                        clientWorkIterators.add(c);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            };
            connectThread.start();
        }
        System.out.printf("connect of %d clients is completed \n",n);
    }
    public void start(int n, int m) throws InterruptedException, ExecutionException {
        if (clientWorkIterators.size() < n){
            System.out.println("number of clients is less than input n");
            return;
        }

        CountDownLatch requestCountDown = new CountDownLatch(n);
        Iterator<ClientWorkIterator> iterator = clientWorkIterators.iterator();
        for (int j = 0; j < n; j++) {
            ClientWorkIterator temp = iterator.next();
            temp.setRequestTimes(m);
            temp.setCountDownLatch(requestCountDown);
            new Thread(temp).start();
        }
    }

    public int getData(){
        int total = 0;
        for (ClientWorkIterator cli : clientWorkIterators){
            System.out.printf("client %d \n",cli.getCount());
            total += cli.getCount();
        }
        return total;
    }
}
