package com.meetball.demo;

import com.meetball.demo.socket.SocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MeetBallApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(MeetBallApplication.class, args);

        context.getBean(SocketServer.class).startServer();
    }

}
