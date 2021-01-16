package me.xemor.MinecraftServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(25565);
        System.out.println("Started!");
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            int length = readVarInt(inputStream);
            int packetID = readVarInt(inputStream);
            System.out.println(length + " <-- length");
            System.out.println(packetID + " <-- packet ID");
            if (packetID == 0x00) {
                int protocolVersion = readVarInt(inputStream);
                System.out.println(protocolVersion + " <-- protocol version");
                String serverAddress = readString(inputStream);
                System.out.println(serverAddress + " <-- server address");
                int port = readShort(inputStream);
                System.out.println(port + " <-- port number");
                int nextState = readVarInt(inputStream);
                System.out.println(nextState + " <-- next state (1 for status) (2 for login)");
            }
        }
    }

    public static int readVarInt(InputStream inputStream) throws IOException {
        int numRead = 0;
        int result = 0;
        int read;
        do {
            read = inputStream.read();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));
            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static String readString(InputStream inputStream) throws IOException {
        int length = readVarInt(inputStream);
        String string = "";
        for (int i = 0; i < length; i++) {
            string += (char) inputStream.read();
        }
        return string;
    }

    public static int readShort(InputStream inputStream) throws IOException{
        int byte1 = inputStream.read();
        int byte2 = inputStream.read();
        byte1 = byte1 << 8;
        return byte1 | byte2;
    }

}
