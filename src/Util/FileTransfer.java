package Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Acer
 */
public class FileTransfer {

    public static void receiveFile(String fileName, DataInputStream dataInputStream) throws Exception {
        int bytes = 0;
        fileName = "D:/Desktop/" + fileName;
        FileOutputStream fos = new FileOutputStream(fileName);

        long size = dataInputStream.readLong();

        byte[] b = new byte[4 * 1024];
        while (size > 0
                && (bytes = dataInputStream.read(b, 0, (int) Math.min(b.length, size))) != -1) {
            fos.write(b, 0, bytes);
            size -= bytes;
        }

        System.out.println("File is received");
        dataInputStream = null;
        fos.close();
        {

        }
    }

    public static void sendFile(String path,  DataOutputStream dataOutputStream) throws Exception {
        int bytes = 0;
        File file = new File(path);

        FileInputStream fis = new FileInputStream(file);

        dataOutputStream.writeLong(file.length());

        byte[] b = new byte[4 * 1024];

        while ((bytes = fis.read(b)) != -1) {
            dataOutputStream.write(b, 0, bytes);
            dataOutputStream.flush();
        }

        System.out.println("Send file");
        dataOutputStream = null;
        fis.close();

    }
}
