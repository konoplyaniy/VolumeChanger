package com.application.firstapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class MainClientSocket {
    private String serverIp;
    private int serverPort;

    String getServerIp() {
        return serverIp;
    }

    void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    int getServerPort() {
        return serverPort;
    }

    void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    int getCurrentVolume() throws IOException {
        String serverResponse;
        Socket clientSocket = new Socket(getServerIp(), getServerPort());
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToServer.writeBytes("currentVolume" + '\n');
        serverResponse = inFromServer.readLine();
        System.out.println("FROM SERVER: " + serverResponse);
        clientSocket.close();
        return Integer.parseInt(serverResponse);
    }

    void changeVolume(int volume) throws IOException {
        String newVolumeValue;
        String serverResponse;
        Socket clientSocket = new Socket(getServerIp(), getServerPort());
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        newVolumeValue = volume + "";
        outToServer.writeBytes(newVolumeValue + '\n');
        serverResponse = inFromServer.readLine();
        System.out.println("FROM SERVER: " + serverResponse);
        clientSocket.close();
    }

    public static void main(String[] args) throws Exception {
        MainClientSocket clientSocket = new MainClientSocket();
        clientSocket.setServerPort(6789);
        clientSocket.setServerIp("localhost");
        System.out.println(clientSocket.getCurrentVolume());
    }
}
