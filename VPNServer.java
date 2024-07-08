import java.io.*;
import java.net.*;
import java.security.*;
import java.util.concurrent.Executors;
import java.util.logging.*;
import javax.net.ssl.*;
import java.util.Properties;

public class EnhancedVPNServer {
    private static final String CONFIG_FILE = "server.properties";
    private static Properties properties = new Properties();
    private static final Logger logger = Logger.getLogger(EnhancedVPNServer.class.getName());

    public static void main(String[] args) {
        loadConfig();
        setupLogger();

        int port = Integer.parseInt(properties.getProperty("server.port", "12345"));
        String keyStore = properties.getProperty("server.keystore", "keystore.jks");
        String keyStorePassword = properties.getProperty("server.keystore.password", "password");

        try {
            // Setup SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keyStore), keyStorePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keyStorePassword.toCharArray());
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

            // Create SSL server socket
            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
            try (SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(port)) {
                logger.info("VPN Server is running...");

                var threadPool = Executors.newFixedThreadPool(10);
                while (true) {
                    SSLSocket socket = (SSLSocket) serverSocket.accept();
                    threadPool.execute(new ClientHandler(socket));
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Server error", e);
        }
    }

    private static void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load configuration", ex);
        }
    }

    private static void setupLogger() {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not setup logger configuration", e);
        }
    }

    private static class ClientHandler implements Runnable {
        private SSLSocket socket;

        public ClientHandler(SSLSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
            ) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String username = reader.readLine();
                String password = reader.readLine();

                if (authenticate(username, password)) {
                    output.write("Authentication successful\n".getBytes());
                    // Handle encrypted communication
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        // Handle data (decrypt, process, etc.)
                    }
                } else {
                    output.write("Authentication failed\n".getBytes());
                    socket.close();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Client handler error", e);
            }
            
        }

        private boolean authenticate(String username, String password) {
            String expectedUsername = properties.getProperty("auth.username", "user");
            String expectedPassword = properties.getProperty("auth.password", "pass");
            return expectedUsername.equals(username) && expectedPassword.equals(password);
        }
    }
}
