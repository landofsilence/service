package com.meetball.demo.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SocketServer {
    private static final int PORT = 2836;
    public static List<Socket> mClientList = new ArrayList<Socket>();
    private static ServerSocket server = null;
    private static ExecutorService mExecutors = null; // 线程池对象

    public static void main(String[] args) {
        SocketServer s = new  SocketServer();
        startServer();
    }

    /**
     * 构造方法：任务是启动服务器，等待客户端连接
     */
    public static void startServer() {
        try {
            server = new ServerSocket(PORT);
            mExecutors = Executors.newCachedThreadPool(); // 创建线程池
            System.out.println("服务器已启动，等待客户端连接...");
            Socket client = null;
            /*
             * 用死循环等待多个客户端的连接，连接一个就启动一个线程进行管理
             */
            while (true) {
                client = server.accept();
                // 把客户端放入集合中
                ClientService cs = new ClientService(client);
                mExecutors.execute(cs); // 启动一个线程，用以守候从客户端发来的消息
                System.out.println("client+1...");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
