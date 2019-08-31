package com.meetball.demo.socket;

import com.meetball.demo.service.UserService;
import com.meetball.demo.service.impl.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.Socket;


public class ClientService implements Runnable {
    //该服务线程用来守候特定的socket
    public Socket socket;
    private BufferedReader in = null;
    private String message = "";
    String userName;
    String separator = System.getProperty("line.separator");

    /*@Autowired
    private com.meetball.demo.service.UserService userService;*/
    com.meetball.demo.service.impl.UserServiceImpl userService = UserServiceImpl.getInstance();
    com.meetball.demo.service.impl.OrderServiceImpl orderService = OrderServiceImpl.getInstance();


    public ClientService(Socket socket) {
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));// 获得输入流对象
            // 客户端只要一连到服务器，便发送连接成功的信息


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            while (true) {
                if ((message = in.readLine()) != null) {
                    // 当客户端发送的信息为：exit时，关闭连接
                    if (message.equals("<publishOrder2List>")) {       //发布订单
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</publishOrder2List>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        orderService.publishOrder2List(json);
                        this.sendMessage("<publishOrderRe>" + separator + "1" + separator + "</publishOrderRe>");
                    } else if (message.equals("<takeOrder>")) {         //接受订单
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</takeOrder>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        orderService.takeOrder(json);
                        this.sendMessage("<takeOrderRe>" + separator + "1" + separator + "</takeOrderRe>");
                    } else if (message.equals("<finishOrder>")) {       //完成订单
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</finishOrder>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("result", 1);
                        orderService.finishOrder(json);
                        this.sendMessage("<finishOrderRe>" + separator + jsonObject + separator + "</finishOrderRe>");
                    } else if (message.equals("<getUserOrderList>")) {      //根据用户名获取订单号
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</getUserOrderList>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        String result = orderService.getUserOrderList(json);
                        this.sendMessage("<getUserOrderListRe>" + separator + result + separator + "</getUserOrderListRe>");

                    } else if (message.equals("<getDriverOrderList>")) {      //根据用户名获取订单号
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</getDriverOrderList>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        String result = orderService.getDriverOrderList(json);
                        this.sendMessage("<getDriverOrderListRe>" + separator + result + separator + "</getDriverOrderListRe>");

                    } else if (message.equals("<getOrderInfo>")) {      //根据订单ID获取订单详情
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</getOrderInfo>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        String result = orderService.getOrderInfo(json);
                        this.sendMessage("<getOrderInfoRe>" + separator + result + separator + "</getOrderInfoRe>");

                    } else if (message.equals("<getInfo>")) {
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</getInfo>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        JSONObject jsonObject = JSONObject.fromObject(json);
                        String result = userService.getInfo((String)jsonObject.get("userName"));
                        this.sendMessage( "<userInfo>" + separator + result + separator + "</userInfo>");
                    } else if (message.equals("<client>")) {
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</client>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        userName = userService.client(json,this);

                    } else if (message.equals("<updateInfo>")) {
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</updateInfo>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        String result = userService.updateInfo(json);
                        this.sendMessage( "<updateResult>" + separator + result + separator + "</updateResult>");
                    } else if(message.equals("<uploadImage>")){
                        String s = in.readLine();
                        if(s.equals("1"))
                        {
                            saveImg("avatar_" + userName);
                        }
                    } else if (message.equals("<downloadImage>")) {
                        String s = in.readLine();
                        String json = "";
                        while (!s.equals("</downloadImage>")) {
                            json = json + s + System.getProperty("line.separator");
                            s = in.readLine();
                        }
                        JSONObject result = userService.getImage(json);
                        this.sendMessage( "<sendImage>" + separator + result.toString() + separator + "</sendImage>");
                        if(result.getInt("result") != -1)
                            sendFile(userService.file);
                    } else {
                        System.out.println(message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭客户端
     *
     * @throws IOException
     */
    public void closeSocket() throws IOException {
        SocketServer.mClientList.remove(socket);
        in.close();
        message = "主机:" + socket.getInetAddress() + "关闭连接\n目前在线:"
                + SocketServer.mClientList.size();
        socket.close();
        this.sendMessageToAll(message);
    }

    /**
     * 将接收的消息转发给每一个客户端
     *
     * @param msg
     */

    public void sendMessageToAll(String msg) {
        System.out.println(msg);// 先在控制台输出
        int count = SocketServer.mClientList.size();
        // 遍历客户端集合
        for (int i = 0; i < count; i++) {
            Socket mSocket = SocketServer.mClientList.get(i);
            PrintWriter out = null;
            try {
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(mSocket.getOutputStream())),
                        true);// 创建输出流对象
                out.println(msg);// 转发
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendMessage(String msg) {
        System.out.println(msg);// 先在控制台输出
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);// 创建输出流对象
            out.println(msg);// 转发
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        //BufferedInputStream bi=new BufferedInputStream(new InputStreamReader(new FileInputStream(file),"GBK"));
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());//client.getOutputStream()返回此套接字的输出流
        try {
            //文件名、大小等属性
            System.out.println(file.length());

            // 开始传输文件
            byte[] bytes = new byte[1024];
            int length = 0;

            while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                dos.write(bytes, 0, length);
                dos.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("客户端文件传输异常");
        }
    }

    public void saveImg(String imageName) {
        //获取上传图片
        try {

                DataInputStream dataInput = new DataInputStream(socket.getInputStream());

                Long size0 = dataInput.readLong();
                int size = size0.intValue();
                byte[] data = new byte[size];
                int len = 0;
                while (len < size) {
                    len += dataInput.read(data, len, size - len);
                }
                saveFile(data,getAbsolutePath(),imageName + ".jpg");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void saveFile(byte[] bfile, String filePath,String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + System.getProperty("file.separator") +fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static String getAbsolutePath(){
        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if (!path.exists()) path = new File("");
            System.out.println("path:" + path.getAbsolutePath());

            //如果上传目录为/static/images，则可以如下获取：
            File upload = new File(path.getAbsolutePath(), "static/images");
            if (!upload.exists()) upload.mkdirs();
            System.out.println("upload url:" + upload.getAbsolutePath());
            //在开发测试模式时，得到的地址为：{项目跟目录}/target/static/images/upload/
            //在打包成jar正式发布时，得到的地址为：{发布jar包目录}/static/images/upload/
            return  upload.getAbsolutePath();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    return  null;
}
}