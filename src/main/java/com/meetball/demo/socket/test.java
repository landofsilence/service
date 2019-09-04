package com.meetball.demo.socket;



import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.Socket;


//这个是为了测试服务器的临时w
public class test {
   //private static final String HOST = "120.79.35.179";//服务器地址
   private static final String HOST = "127.0.0.1";//服务器地址
    private static final int PORT = 2836;//连接端口号



    /**
     * 读取服务器发来的信息，并通过Handler发给UI线程
     */
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            socket = new Socket(HOST, PORT);//连接服务器
            in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));//接收消息的流对象
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())), true);//发送消息的流对象

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in ));
            //java.io.InputStreamReader继承了Reader类
            String read = "";
            read = br.readLine();
            while(read!=null){
             out.println(read);

             if(read.equals("<uploadImage>"))
             {
                 out.println("1");
                 sendFile(socket);
             }

             read = br.readLine();
             if(read.equals("ok")){
                 break;
             }
            }

            while(true) {
                System.out.println(in.readLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
            System.out.println("out is ok,come in waiting");
        }


    public static void sendFile(Socket socket) throws IOException {
        File file = new File("E:\\test.jpg");
        FileInputStream fis = new FileInputStream(file);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());//client.getOutputStream()返回此套接字的输出流
        try {
            //文件名、大小等属性
            dos.writeLong(file.length());
            dos.flush();
            // 开始传输文件
            System.out.println("======== 开始传输文件 ========");
            byte[] bytes = new byte[1024];
            int length = 0;

            while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                System.out.println(length + "and" + bytes.length);
                dos.write(bytes, 0, length);
                dos.flush();
            }
            System.out.println("======== 文件传输成功 ========");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("客户端文件传输异常");
        }
    }


}