import Client.Clients;
import Protocol.Request;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        System.out.println("'connect n' -- n clients try connect");
        System.out.println("'start n m' -- n clients each request m times");
        System.out.println("'g' -- get request count with client");
        Scanner scanner = new Scanner(System.in);
        Clients clients = new Clients();
        Request.key = new AtomicInteger(); // request 고유 id 초기화
        while (true){
            String s = scanner.nextLine();
            if (!s.isEmpty()) {
                if (s.equals("s")){
                    try {
                        clients.start(10,10000);
                        Thread.sleep(1000);
                        clients.start(10,10000);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (s.equals("a")) clients.setAffinityOption();
                else if (s.equals("g")) {
                    System.out.printf("client total: %d \n",clients.getData());
                }
                String[] spl = s.split(" ");
                try{
                    if (spl[0].equals("connect")){
                        int n = Integer.parseInt(spl[1]);
                        clients.connect(n);
                    }
                    else if (spl[0].equals("start")){
                        int n = Integer.parseInt(spl[1]);
                        int m = Integer.parseInt(spl[2]);
                        clients.start(n,m);
                    }
                } catch (ExecutionException | NumberFormatException | IndexOutOfBoundsException e) {
                    System.out.println("Wrong Input Error");
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Error");
                    throw new RuntimeException(e);
                }
            }
        }
    }
}