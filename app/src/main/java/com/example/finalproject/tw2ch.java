package com.example.finalproject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.*;
import java.nio.file.*;
import java.io.*;
import java.util.stream.*;
import java.nio.charset.StandardCharsets;

public class tw2ch {

    private static final String token = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9" +
            ".eyJzY29wZXMiOiIwIiwidmVyIjowLjEsImlhdCI6MTYwMDEzNzMwNSwic2VydmljZV9pZCI6IjEwIiwiZXhwIjoxNjYzMjA5MzA1LCJhdWQiOiJ3bW1rcy5jc2llLmVkdS50dyIsInN1YiI6IiIsImlkIjozNDAsImlzcyI6IkpXVCIsIm5iZiI6MTYwMDEzNzMwNSwidXNlcl9pZCI6IjExOSJ9.WVjd0GjTKpVEflf2rteOfxL495XYpzUelcSI4SIJwBI5sQIvMGh-z7dcclmGRmFrloEe4qzcTs9Q1nFO8fR74ayXkCsreQY9CyuInYvfWAOJCxM0iWwFCGlBYAfFgEIoQDrv-696KVduywafWjQYOaiX4Ggw0AXmoRrU_TcO6ks";

    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static String sendText2(String host, int port, String text) {
        String result = null;

        // text pre-process
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        byte[] size = String.valueOf(data.length).getBytes(StandardCharsets.UTF_8);

        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length);
        buf.put(data);

//        System.out.println(Arrays.toString(msg));

        try {
            Socket api = new Socket(host, port);

            if (api.isConnected()) {
                OutputStream os = api.getOutputStream();
                os.write(buf.array());
                os.flush();
                //InputStream in = new InputStream(api.getInputStream());
                DataInputStream in = new DataInputStream(api.getInputStream());
                int count = api.getInputStream().available();
                byte[] bs = new byte[count];
                in.read(bs);
                //int buffer = byteArrayToInt(bs);
                result = new String(bs, StandardCharsets.UTF_8);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        String data = token + "@@@" + "覓佇遐五四三";
        String result = sendText2("140.116.245.149", 27002, data);
        System.out.println(result);
    }


}
