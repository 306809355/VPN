Enhanced VPN Server and Client
This project consists of a secure VPN server (EnhancedVPNServer) and client (EnhancedVPNClient) implementation using SSL/TLS for encrypted communication.

Features
EnhancedVPNServer
      . Securely listens for incoming client connections over SSL/TLS.
       . Utilizes a configurable server.properties file for server settings and authentication details.
       . Supports multi-threaded client handling using a fixed thread pool.
       . Logs server activities using java.util.logging with a customizable logging configuration.
EnhancedVPNClient
      . Establishes a secure connection to the VPN server using SSL/TLS.
      . Configured via client.properties for server address, port, and client-side keystore settings.
      . Authenticated communication with the server using username and password.
      . Sends and receives encrypted data over the established SSL connection.
