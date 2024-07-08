import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;
import java.util.zip.GZIPOutputStream;
import java.util.logging.*;
import java.util.Properties;

public class EnhancedVPNClient {
    private static final String CONFIG_FILE = "client.properties";
    private static Properties properties = new Properties();
    private static final Logger logger = Logger.getLogger(EnhancedVPNClient.class.getName());

    public static void main(String[] args) {
        loadConfig();
        setupLogger();

        String serverAddress = properties.getProperty("server.address", "localhost");
        int port = Integer.parseInt(properties.getProperty("server.port", "12345"));
        String keyStore = properties.getProperty("client.keystore", "clientkeystore.jks");
        String keyStorePassword = properties.getProperty("client.keystore.password", "password");

        try {
            // Setup SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keyStore), keyStorePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keyStorePassword.toCharArray());
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

            // Create SSL socket
            SSLSocketFactory factory = sslContext.getSocketFactory();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(serverAddress, port)) {
                try (
                    OutputStream output = new GZIPOutputStream(socket.getOutputStream());
                    InputStream input = socket.getInputStream()
                ) {
                    PrintWriter writer = new PrintWriter(output, true);
                    writer.println(properties.getProperty("auth.username", "user"));
                    writer.println(properties.getProperty("auth.password", "pass"));

                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String response = reader.readLine();
                    if ("Authentication successful".equals(response)) {
                        byte[] data = "Hello, VPN!".getBytes();
                        output.write(data);
                        output.flush();
                    } else {
                        logger.warning("Authentication failed");
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Client error", e);
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
}
