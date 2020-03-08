package net_server;

import library.Library;
import network.ServerSocketThread;
import network.ServerSocketThreadListener;
import network.SocketThread;
import network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    private final ChatServerListener listener;
    private ServerSocketThread server;
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss: ");

    public static final int ACTIVITYTIMEOUT = 12000;
    private Vector<SocketThread> clients = new Vector<>();
    private ExecutorService executorService;
  //  private Thread checkActivity;
    private static final Logger LOGGER = LogManager.getLogger(ChatServer.class);

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    public void start(int port) {
        if (server != null && server.isAlive()) System.out.println("Server is already started");

        else {
            executorService= Executors.newFixedThreadPool(2);
            server = new ServerSocketThread(this, "Server", port, 2000);
            executorService.execute(server);
            executorService.execute(
                 new Thread() {
                synchronized void check() {
                    ClientThread client;
                    for (int i = 0; i < clients.size(); i++) {
                        client = (ClientThread) clients.get(i);
                        if (!client.isAuthorized() && (System.currentTimeMillis() - client.getCreateTime()) > ACTIVITYTIMEOUT) {
                            client.sendMessage(Library.AUTH_DENIED+Library.DELIMITER+"Your connection was closed");
                            clients.get(i).close();
                            clients.remove(i);
                        }
                    }
                }

                @Override
                public void run() {

                    while (!isInterrupted()) {
                        if (!clients.isEmpty()) {
                            try {
                                check();
                                sleep(1000);
                            } catch (InterruptedException e) {
                                LOGGER.error("Поток проверки не смог уснуть ");

                            }
                        }
                    }

                }
            });
         }

    }

    public void stop() {
        LOGGER.info("Server shutdown");
        executorService.shutdown();
    }

    private void putLog(String msg) {
        msg = DATE_FORMAT.format(System.currentTimeMillis()) +
                Thread.currentThread().getName() + ": " + msg;
        listener.onChatServerMessage(msg);
    }

    /**
     * Server methods

     */


    @Override
    public void onServerStart(ServerSocketThread thread) {
        LOGGER.info("Server thread started");
        SqlClient.connect();
    }

    @Override
    public void onServerStop(ServerSocketThread thread) {
        LOGGER.info("Server thread stopped");
        SqlClient.disconnect();
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).close();
        }

    }

    @Override
    public void onServerSocketCreated(ServerSocketThread thread, ServerSocket server) {
        LOGGER.info("Server socket created");





    }

    @Override
    public void onServerTimeout(ServerSocketThread thread, ServerSocket server) {
//        putLog("Server timeout");

    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, ServerSocket server, Socket socket) {
        LOGGER.info("Client connected");
        String name = "SocketThread " + socket.getInetAddress() + ":" + socket.getPort();
        new ClientThread(this, name, socket,"");

    }

    @Override
    public void onServerException(ServerSocketThread thread, Throwable exception) {
        exception.printStackTrace();
    }

    /**
     * Socket methods

     */


    @Override
    public synchronized void onSocketStart(SocketThread thread, Socket socket) {
        LOGGER.info("Socket created");

    }

    @Override
    public synchronized void onSocketStop(SocketThread thread) {
        ClientThread client = (ClientThread) thread;
        clients.remove(thread);

        if (client.isAuthorized() && !client.isReconnecting()) {
            sendToAuthClients(Library.getTypeBroadcast("Server",
                    client.getNickname() + " disconnected"));
        }
        else LOGGER.info("Неудачная попытка входа на "+client.getSocket().toString()+".\nСокет был закрыт по таймауту;");

        sendToAuthClients(Library.getUserList(getUsers()));
    }

    @Override
    public synchronized void onSocketReady(SocketThread thread, Socket socket,String action) {
        clients.add(thread);
    }

    @Override
    public synchronized void onReceiveString(SocketThread thread, Socket socket, String msg) {
        ClientThread client = (ClientThread) thread;
        if (client.isAuthorized()) {
            handleAuthMessage(client, msg);
        } else {
            handleNonAuthMessage(client, msg);
        }
    }

    private void handleNonAuthMessage(ClientThread client, String msg) {
        String[] arr = msg.split(Library.DELIMITER);
        if (arr.length != 3 & !arr[0].equals(Library.AUTH_REQUEST) & !arr[0].equals(Library.REGISTRATION_REQUEST)) {
            client.msgFormatError(msg);
            return;
        }
        if (arr[0].equals(Library.REGISTRATION_REQUEST)){
            try {
                SqlClient.Registration(arr[1],arr[2],arr[3]);
                String login = arr[1];
                String password = arr[2];
                String nickname =arr[3];
            } catch (SQLException e) {
                e.printStackTrace();
                client.registrationFail();
            }


        }
        else if (arr[0].equals(Library.CHANGE_NICKENAME_REQUEST)){
            try {
                String login = arr[1];
                String password = arr[2];
                String nickname = SqlClient.getNickname(login, password);
                if (nickname == null) {
                   LOGGER.info("Invalid login attempt: " + login);
                    client.authFail();
                    return;
                }
                SqlClient.ChangeRegistrationInformation(login,arr[3]);

            } catch (SQLException e) {
                e.printStackTrace();
                client.registrationFail();
            }
        }
        else {
        String login = arr[1];
        String password = arr[2];
        String nickname = SqlClient.getNickname(login, password);
            if (nickname == null) {
                LOGGER.info("Invalid login attempt: " + login);
                client.authFail();
                return;
             }


        else {
            ClientThread oldClient = findClientByNickname(nickname);
            client.authAccept(nickname);
            if (oldClient == null) {
                sendToAuthClients(Library.getTypeBroadcast("Server", nickname + " connected"));
            } else {
                oldClient.reconnect();
                clients.remove(oldClient);
            }

        }
        sendToAuthClients(Library.getUserList(getUsers()));
     }
    }

    private void handleAuthMessage(ClientThread client, String msg) {
        String[] arr = msg.split(Library.DELIMITER);
        String msgType = arr[0];
        switch (msgType) {
            case Library.TYPE_BCAST_CLIENT:
                sendToAuthClients(Library.getTypeBroadcast(
                        client.getNickname(), arr[1]));
                break;
            default:
                client.sendMessage(Library.getMsgFormatError(msg));
        }
    }


    // launch4j

    private void sendToAuthClients(String msg) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            client.sendMessage(msg);
            LOGGER.info(msg);
        }
    }

    @Override
    public synchronized void onSocketException(SocketThread thread, Exception exception) {
        exception.printStackTrace();
    }

    private String getUsers() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            sb.append(client.getNickname()).append(Library.DELIMITER);
        }
        return sb.toString();
    }

    private synchronized ClientThread findClientByNickname(String nickname) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            if (client.getNickname().equals(nickname))
                return client;
        }
        return null;
    }

}