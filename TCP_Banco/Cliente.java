package TCP_Banco;
import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private static final Scanner SCANNER = new Scanner(System.in);
    //public static final String SERVER = "6.tcp.ngrok.io";
    public static final String SERVER = "localhost"; // debe ser la direcion IP o el localHost
    public static final int PORT = 3400;	// puerto del servidor
    private PrintWriter toNetwork;
    private BufferedReader fromNetwork;
    private Socket clientSideSocket;

    public Cliente() {
        System.out.println("Bank client is running ...");
    }

    public void init() throws Exception {
        clientSideSocket = new Socket(SERVER, PORT);
        createStreams(clientSideSocket);        
        //processBatchTransactions();       
        protocol(clientSideSocket);
        clientSideSocket.close();
    }
    /* 
    private void processBatchTransactions() throws Exception {
        System.out.print("Ingrese la ruta del archivo de transacciones: ");
        //C:\Users\DOUGLAS\Downloads\txt\prueba.txt
        String fileName = SCANNER.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                toNetwork.println(line);
                String response = fromNetwork.readLine();
                System.out.println(response);
            }
        }
    }
        */

    public void protocol(Socket socket) throws Exception {
        while (true) {
            System.out.println("Elige una opcion: ");
            System.out.println("1. Abrir nueva cuenta");
            System.out.println("2. Depositar dinero");
            System.out.println("3. Retirar dinero");
            System.out.println("4. Consultar balance");
            System.out.println("5. Exit");
            int option = SCANNER.nextInt();
            SCANNER.nextLine(); 

            switch (option) {
                case 1:
                	
                    System.out.print("Ingrese numero de cuenta: ");
                    String numero = SCANNER.next();
                    
                    System.out.print("Ingrese su nombre: ");
                    String nombre = SCANNER.next();
                    
                    System.out.print("Ingresa su saldo: ");
                    double saldo = SCANNER.nextDouble();
                    
                	toNetwork.println("abrir" + "," + 1111 + "," + "Douglas" + "," + 3500);
                    toNetwork.println("ABRIR" + "," + numero + "," + nombre + "," + saldo);
                    break;

                case 2:
                    System.out.print("Ingrese el numero de cuenta: ");
                    int accountNumber = SCANNER.nextInt();
                    System.out.print("Ingrese monto a depostar: ");
                    double amount = SCANNER.nextDouble();
                    toNetwork.println("depositar," + accountNumber + "," + amount);
                    break;

                case 3:
                    System.out.print("Ingrese el numero de cuenta: ");
                    accountNumber = SCANNER.nextInt();
                    System.out.print("Ingrese monto a retirar: ");
                    amount = SCANNER.nextDouble();
                    toNetwork.println("retirar," + accountNumber + "," + amount);
                    break;

                case 4:
                    System.out.print("Ingrese el numero de cuenta: ");
                    accountNumber = SCANNER.nextInt();
                    toNetwork.println("consultar," + accountNumber);
                    break;

                case 5:
                    toNetwork.println("SALIR");
                    return;

                default:
                    System.out.println("Opcion invalida");
            }

            String response = fromNetwork.readLine();
            System.out.println(response);
        }
    }

    private void createStreams(Socket socket) throws Exception {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static void main(String args[]) throws Exception {
        Cliente bankClient = new Cliente();
        bankClient.init();
    }

}
