package com.apon.chen.service;

import com.apon.chen.common.EmailUtil;
import com.apon.chen.common.HttpRequest;
import com.apon.chen.entity.Hospital;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author chen
 */
@Slf4j
@Component
@EnableScheduling
public class ScheduledService{
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private HttpRequest httpRequest;

    public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {
        StringBuffer buffer = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(requestMethod);
            conn.connect();
            //往服务器端写内容 也就是发起http请求需要带的参数
            if (null != outputStr) {
                OutputStream os = conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }

            //读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static boolean isHttpPortOpen(String host, int port) {
        try {
            // 创建Socket对象
            Socket socket = new Socket();

            // 设置连接超时时间
            int timeout = 5000; // 5秒
            socket.connect(new InetSocketAddress(host, port), timeout);

            // 连接成功
            socket.close();
            return true;
        } catch (IOException e) {
            // 连接失败
            return false;
        }
    }

    public void httpTelnetCheck() {
        List<Hospital> list = hospitalService.list(
                //设置过滤规则为匹配
                new QueryWrapper<Hospital>().lambda().isNotNull(Hospital::getSsh_port)
        );
        System.out.println("ssh_port:" + list.size());
        for (Hospital hospital : list) {
            SimpleDateFormat stf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = stf.format(new Date());
            String hospitalName = hospital.getHospital_name();
            Integer sshStatus = hospital.getSsh_status();
            String host = "116.62.46.33";
            Integer sshPort = hospital.getSsh_port(); // ssh端口
            //调用isHttpPortOpen，判断telnet端口是否通
            boolean isOpen = isHttpPortOpen(host, sshPort);
            if (isOpen && sshStatus != 1) {
                System.out.println(format + "：" + sshPort + "-->>Port is Open-->>");
                String s = httpRequest("http://localhost:9090/hospital/ssh1/" + sshPort, "POST", null);
                // 文本邮件发送（无附件）
                String to = "chenjinjin@apon.com.cn"; // 收件人
                String title = hospitalName + "（" + sshPort + "）" + "[ssh恢复正常]";
                String content = format + "：" + hospitalName + "[ssh恢复正常]";
//                emailUtil.sendMessage(to, title, content);
            } else if (!isOpen && sshStatus != 0) {
                System.out.println(format + "：" + sshPort + "-->>Port is Error-->>");
                String s = httpRequest("http://localhost:9090/hospital/ssh0/" + sshPort, "POST", null);
                String to = "chenjinjin@apon.com.cn";
                String title = hospitalName + "（" + sshPort + "）" + "[ssh异常离线]";
                String content = format + "：" + hospitalName + "[ssh异常离线]";
//                emailUtil.sendMessage(to, title, content);
            }

        }
    }

    public void httpRequestCheck() {
        List<Hospital> list = hospitalService.list(
                //设置过滤规则为匹配
                new QueryWrapper<Hospital>().lambda().isNotNull(Hospital::getSsh_port)
        );
        System.out.println("ssh_port:" + list.size());
        for (Hospital hospital : list) {
            SimpleDateFormat stf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = stf.format(new Date());
            String hospitalName = hospital.getHospital_name();
            Integer sshStatus = hospital.getSsh_status();
            String host = "116.62.46.33";
            Integer sshPort = hospital.getSsh_port(); // ssh端口
            String url = "http://116.62.46.33:"+sshPort+"/Pain/index.html";
            //调用isHttpPortOpen，判断telnet端口是否通
//            boolean isOpen = isHttpPortOpen(host, sshPort);
            boolean isOpen = httpRequest.httpUrlCheck(url);
            if (isOpen && sshStatus != 1) {
                System.out.println(format + "：" + url + ">>is Open-->>");
                String s = httpRequest("http://localhost:9090/hospital/ssh1/" + sshPort, "POST", null);
                // 文本邮件发送（无附件）
                String to = "chenjinjin@apon.com.cn"; // 收件人
                String title = hospitalName + "（" + sshPort + "）" + "[ssh恢复正常]";
                String content = format + "：" + hospitalName + "[ssh恢复正常]";
//                emailUtil.sendMessage(to, title, content);
            } else if (!isOpen && sshStatus != 0) {
                System.out.println(format + "：" + url + ">>is Error-->>");
                String s = httpRequest("http://localhost:9090/hospital/ssh0/" + sshPort, "POST", null);
                String to = "chenjinjin@apon.com.cn";
                String title = hospitalName + "（" + sshPort + "）" + "[ssh异常离线]";
                String content = format + "：" + hospitalName + "[ssh异常离线]";
//                emailUtil.sendMessage(to, title, content);
            }

        }
    }
    //@PostConstruct是Java自带的注解，在方法上加该注解会在项目启动的时候执行该方法，也可以理解为在spring容器初始化的时候执行该方法
//    @PostConstruct
    public void setExecutorService() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

        // 执行任务1，延迟1秒后执行，每隔3秒执行一次
        executorService.scheduleAtFixedRate(() ->

        {
            System.out.println("Task 1: Executing...");
            // TODO: 执行你的任务逻辑
            this.httpRequestCheck();
        }, 10, 60, TimeUnit.SECONDS);

        // 执行任务2，延迟2秒后执行，每隔5秒执行一次
        executorService.scheduleAtFixedRate(() ->

        {
            System.out.println("Task 2: Executing...");
            // TODO: 执行你的任务逻辑
            this.httpTelnetCheck();
        }, 30, 60, TimeUnit.SECONDS);
    }

    // 创建线程池

}

