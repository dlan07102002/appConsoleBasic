/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package remoteserver;

import Util.FileTransfer;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;

/**
 *
 * @author Acer
 */
public class RemoteServer {

    private static DataInputStream dataInputStream = null;
    private static DataOutputStream dataOutputStream = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());

                //Create Threate 
                Thread th = new Thread(
                        () -> handleClientRequest(socket)
                );
                th.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleClientRequest(Socket socket) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
//            pw.println("Pending...");
            while (true) {
                String req = br.readLine();

                System.out.println(req);
                if (req.equals("shutdown")) {
                    //Use Runtime
                    Runtime.getRuntime().exec("shutdown -s -t 3600");
                    pw.println("Computer is shuting down");
                    pw.flush();

                } else if (req.equals("restart")) {
                    //Use Runtime
                    Runtime.getRuntime().exec("shutdown -r -t 3600");
                    pw.println("Computer is restarting ");
                    pw.flush();

                } else if (req.equals("cancel")) {
                    //Use Runtime
                    Runtime.getRuntime().exec("shutdown -a");
                    pw.println("Canceled");
                    pw.flush();

                } else if (req.equals("screenshot")) {
                    //Take a screenshot
                    BufferedImage screenshot = new Robot().createScreenCapture(
                            new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
                    );

                    //Image into OutputStream
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(screenshot, "png", baos);

                    byte[] imageBytes = baos.toByteArray();
                    baos.close();

                    //Get image length
                    pw.println(imageBytes.length);
                    pw.flush();
                    //Send image 
                    socket.getOutputStream().write(imageBytes);

                } else if (req.equals("download")) {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    String t;
                    while ((t = br.readLine()) != null) {
                        FileTransfer.sendFile(t, dataOutputStream);
                        break;
                    }

                  
                    //close dataoutputStream make socket close

                } else if (req.equals("upload")) {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    String t1;

                    while ((t1 = br.readLine()) != null) {
                        System.out.println("t1: " + t1);
                        FileTransfer.receiveFile(t1, dataInputStream);
                        break;
                    }
                   

                }

            }

        } catch (Exception e) {
        }
    }

}
