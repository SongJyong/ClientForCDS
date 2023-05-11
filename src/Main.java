import Client.Clients;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        System.out.println("'connect n' -- n clients try connect");
        System.out.println("'request n m' -- n clients each request m times");
        System.out.println("'get' -- get request count with client");
        System.out.println("'affinity n' -- For each request, get n connections. (base is 'affinity 1')");
        System.out.println("'wait n' -- cli waits n seconds");
        Scanner scanner = new Scanner(System.in);
        Clients clients = new Clients();
        while (true){
            String s = scanner.nextLine();
            if (!s.isEmpty()) {
                if (s.equals("get")) {
                    System.out.printf("client total: %d \n",clients.getData());
                    continue;
                }
                String[] spl = s.split(" ");
                try{
                    int n = Integer.parseInt(spl[1]);
                    if (spl[0].equals("connect")){
                        clients.connect(n);
                    }
                    else if (spl[0].equals("request")){
                        int m = Integer.parseInt(spl[2]);
                        clients.start(n,m);
                    }
                    else if (spl[0].equals("affinity")){
                        clients.needNumberOfConnection = n;
                    }
                    else if (spl[0].equals("wait")){
                        Thread.sleep(1000*n);
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