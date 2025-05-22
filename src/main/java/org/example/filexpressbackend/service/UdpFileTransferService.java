package org.example.filexpressbackend.service;

import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UdpFileTransferService {

    private static final int SERVER_UDP_PORT = 9999;
    private static final int BUFFER_SIZE = 65507; // Max UDP packet size
    private final Map<String, FileOutputStream> fileStreams = new HashMap<>();

    public void startUdpServer() {
        new Thread(() -> {
            try (DatagramSocket serverSocket = new DatagramSocket(SERVER_UDP_PORT)) {
                System.out.println("UDP Server listening on port " + SERVER_UDP_PORT);

                byte[] receiveBuffer = new byte[BUFFER_SIZE];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(packet);

                    handleIncomingPacket(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleIncomingPacket(DatagramPacket packet) throws IOException {
        byte[] data = packet.getData();
        String packetHeader = new String(data, 0, 50).trim(); // Extract header info
        String[] headerParts = packetHeader.split(":");

        if (headerParts.length < 2) return;

        String receiverUsername = headerParts[0];
        String fileIdentifier = headerParts[1];

        // Save packet to file
        File file = new File("uploads/" + fileIdentifier);
        FileOutputStream fos = fileStreams.computeIfAbsent(fileIdentifier, k -> {
            try {
                return new FileOutputStream(file, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        fos.write(data, 50, packet.getLength() - 50); // Write packet data

        // Relay the packet to the receiver
        relayPacketToReceiver(packet, receiverUsername);
    }

    private void relayPacketToReceiver(DatagramPacket packet, String receiverUsername) {
        try (DatagramSocket relaySocket = new DatagramSocket()) {
            InetAddress receiverAddress = InetAddress.getByName("receiver-ip-here"); // Replace dynamically
            int receiverPort = 8888; // Receiver listening port

            DatagramPacket relayPacket = new DatagramPacket(
                    packet.getData(),
                    packet.getLength(),
                    receiverAddress,
                    receiverPort
            );

            relaySocket.send(relayPacket);
            System.out.println("Relayed packet to receiver: " + receiverUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}