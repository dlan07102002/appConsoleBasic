/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package remoteclient;

import Util.FileTransfer;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 * @author Acer
 */
public class RemoteClient {

    private static DataInputStream dataInputStream = null;
    private static DataOutputStream dataOutputStream = null;
    private static String fileName;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Server Connected");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());

            Scanner sc = new Scanner(System.in);

            boolean exit = false;

            while (!exit) {
                System.out.println("\nMENU: ");
                System.out.println("1. Shutdown: ");
                System.out.println("2. Restart: ");
                System.out.println("3. Cancel Shutdown/Restart");
                System.out.println("4. ScreenShot");
                System.out.println("5. Download ");
                System.out.println("6. Upload");

                int choice = sc.nextInt();
                sc.nextLine();
                System.out.println("Choosen: " + choice);
                switch (choice) {
                    case 1 -> {
                        pw.println("shutdown");
                        pw.flush();
                        System.out.println(br.readLine());
                    }
                    case 2 -> {
                        pw.println("restart");
                        pw.flush();
                        System.out.println(br.readLine());
                    }
                    case 3 -> {
                        pw.println("cancel");
                        pw.flush();
                        System.out.println(br.readLine());
                    }
                    case 4 -> {
                        pw.println("screenshot");
                        pw.flush();

                        int imageSize = Integer.parseInt(br.readLine());
                        byte[] imageBytes = new byte[imageSize];
                        int byteRead = socket.getInputStream().read(imageBytes);
                        if (byteRead > 0) {
                            System.out.println("Enter image name: ");
                            String imageName = sc.nextLine();
                            Path imagePath = Paths.get(imageName + ".png");
                            Files.write(imagePath, imageBytes);

                            System.out.println("Done!");
                        }
                    }
                    case 5 -> {
                        pw.println("download");
                        pw.flush();

                        System.out.println("Enter server path name");

                        fileName = sc.nextLine();
                        pw.println(fileName);
                        pw.flush();

                        try {
                            

                            dataInputStream = new DataInputStream(socket.getInputStream());
                            dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            System.out.println("Enter client file name: ");
                            fileName = sc.nextLine();

                            FileTransfer.receiveFile(fileName, dataInputStream);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    case 6 -> {
                        pw.println("upload");
                        pw.flush();
                        System.out.println("Enter client path name:");
//D:/Desktop/test.txt
                        try {
                            dataInputStream = new DataInputStream(socket.getInputStream());
                            dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            fileName = sc.nextLine();
                            System.out.println("Enter server file name: ");
                            String newName = sc.nextLine();
                            pw.println(newName);
                            pw.flush();
                            FileTransfer.sendFile(fileName, dataOutputStream);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    default ->
                        throw new AssertionError();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
