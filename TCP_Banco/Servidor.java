package TCP_Banco;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {
	
	public static final int PORT = 3400;
	private ServerSocket listener;
	private PrintWriter toNetwork;
	private BufferedReader fromNetwork;
	private ArrayList<CuentaBancaria> cuentas;

    public Servidor() {
        System.out.println("Banco TCP server is running on port: " + PORT);
        cuentas = new ArrayList<CuentaBancaria>();
    }

    public void init() throws Exception {
        listener = new ServerSocket(PORT);
        while (true) {
            Socket socket = listener.accept();
            ClienteHandler clienteHandler = new ClienteHandler(socket);
            clienteHandler.start();
        }
    }

private class ClienteHandler extends Thread {
	
    private Socket socket;
   
    public ClienteHandler(Socket socket) throws Exception {
        this.socket = socket;
        createStreams(socket);
    }

    public void run() {
        try {
            while (true) {
                String message = fromNetwork.readLine();
                if (message == null) {
                    break;
                }
                System.out.println("[Server] Received message: " + message);
                
                String[] messageParts = message.split(",");
                String response = "";

                switch (messageParts[0]) {
                    case "abrir":
                        response = abrirCuenta(messageParts[1], messageParts[2], Double.parseDouble(messageParts[3]));
                        break;
                    case "depositar":
                        response = depositarDinero(messageParts[1], Double.parseDouble(messageParts[2]));
                        break;
                    case "retirar":
                        response = retirarDinero(messageParts[1], Double.parseDouble(messageParts[2]));
                        break;
                    case "consultar":
                        response = consultarSaldo(messageParts[1]);
                        break;
                    default:
                        response = "Comando no válido";
                        break;
                }

                toNetwork.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

    private String abrirCuenta(String numero, String nombre, double saldo) {
        for (CuentaBancaria cuenta : cuentas) {
            if (cuenta.getNumero().equals(numero)) {
                return "El numero de cuenta ya está registrado";
            }
        }
        cuentas.add(new CuentaBancaria(numero, nombre, saldo));
        return "Cuenta creada exitosamente\n";
    }

    private String depositarDinero(String numero, double monto) {
        for (CuentaBancaria cuenta : cuentas) {
            if (cuenta.getNumero().equals(numero)) {
                cuenta.depositar(monto);
                return "Depósito exitoso. Nuevo saldo: " + cuenta.getSaldo();
            }
        }
        return "Cuenta no encontrada";
    }

    private String retirarDinero(String numero, double monto) {
        for (CuentaBancaria cuenta : cuentas) {
            if (cuenta.getNumero().equals(numero)) {
                if (cuenta.retirar(monto)) {
                    return "Retiro exitoso. Nuevo saldo: " + cuenta.getSaldo();
                } else {
                    return "Fondos insuficientes";
                }
            }
        }
        return "Cuenta no encontrada";
    }
    
    private String consultarSaldo(String numero) {
    	for(CuentaBancaria cuenta : cuentas) {
            if (cuenta.getNumero().equals(numero)) {
            	return "El saldo de la cuenta es: " + cuenta.getSaldo();
            }           
    	}
    	return "Cuenta no encontrada";
    }
	    
	
	private void createStreams(Socket socket) throws Exception {
		toNetwork = new PrintWriter(socket.getOutputStream(), true);
		fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public static void main(String args[]) throws Exception {
		Servidor es = new Servidor();
		es.init();
	}
    
}
