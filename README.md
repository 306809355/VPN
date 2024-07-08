Enhanced VPN Server and Client
This project consists of a secure VPN server (EnhancedVPNServer) and client (EnhancedVPNClient) implementation using SSL/TLS for encrypted communication.

Features:
EnhancedVPNServer:
      1 .Securely listens for incoming client connections over SSL/TLS.
      2 .Utilizes a configurable server.properties file for server settings and authentication details.
      3 .Supports multi-threaded client handling using a fixed thread pool.
      4 .Logs server activities using java.util.logging with a customizable logging configuration.
EnhancedVPNClient:
      1 .Establishes a secure connection to the VPN server using SSL/TLS.
      2 .Configured via client.properties for server address, port, and client-side keystore settings.
      3 .Authenticated communication with the server using username and password.
      4 .Sends and receives encrypted data over the established SSL connection.
