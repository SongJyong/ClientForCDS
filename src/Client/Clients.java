package Client;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Clients {
    List<ClientService> clients = new Vector<ClientService>();
    public void connect(int n) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < n; i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    ClientService c = new ClientService();
                    clients.add(c);
                }
            });
        }
        System.out.printf("connect of %d clients is completed \n",n);
    }
    public void start(int n, int m) throws InterruptedException, ExecutionException {
        if (clients.size() < n){
            System.out.println("number of clients is less than input n");
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger index = new AtomicInteger(clients.size() - n);
        for (int j = 0; j < n; j++) {
            ClientService temp = clients.get(index.get());
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    temp.getConnection(m);
                }
            });
            index.set(index.get() + 1);
        }
    }

    public int getData(){
        int total = 0;
        for (ClientService cli : clients){
            System.out.printf("client %d \n",cli.getCount());
            total += cli.getCount();
        }
        return total;
    }
}
