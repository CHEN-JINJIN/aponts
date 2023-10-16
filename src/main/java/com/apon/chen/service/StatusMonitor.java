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

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author chen
 */
@Slf4j
@Component
@EnableScheduling   //启用定时任务
public class StatusMonitor {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private HttpRequest httpRequest;


    //处理http请求  requestUrl为请求地址  requestMethod请求方式，值为"GET"或"POST"
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

//    @Scheduled(fixedDelay = 60 * 1000)
    public void sshPortCheck() {
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
                System.out.println(format + "：" + url + "-->>is Open-->>");
                String s = httpRequest("http://localhost:9090/hospital/ssh1/" + sshPort, "POST", null);
                // 文本邮件发送（无附件）
                String to = "chenjinjin@apon.com.cn"; // 收件人
                String title = hospitalName + "（" + sshPort + "）" + "[ssh恢复正常]";
                String content = format + "：" + hospitalName + "[ssh恢复正常]";
//                emailUtil.sendMessage(to, title, content);
            } else if (!isOpen && sshStatus != 0) {
                System.out.println(format + "：" + url + "-->>is Error-->>");
                String s = httpRequest("http://localhost:9090/hospital/ssh0/" + sshPort, "POST", null);
                String to = "chenjinjin@apon.com.cn";
                String title = hospitalName + "（" + sshPort + "）" + "[ssh异常离线]";
                String content = format + "：" + hospitalName + "[ssh异常离线]";
//                emailUtil.sendMessage(to, title, content);
            }

        }
    }

    @Scheduled(fixedDelay = 300 * 1000)
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
                String to = "chenjinjin@apon.com.cn;fangcc@apon.com.cn;cheny@apon.com.cn"; // 收件人,多个;隔开
                String title = hospitalName + "（" + sshPort + "）" + "[ssh恢复正常]";
                String content = format + "：" + hospitalName + "[ssh恢复正常]";
                emailUtil.sendMessage(to, title, content);
            } else if (!isOpen && sshStatus != 0) {
                System.out.println(format + "：" + sshPort + "-->>Port is Error-->>");
                String s = httpRequest("http://localhost:9090/hospital/ssh0/" + sshPort, "POST", null);
                String to = "chenjinjin@apon.com.cn;fangcc@apon.com.cn;cheny@apon.com.cn"; // 收件人,多个;隔开
                String title = hospitalName + "（" + sshPort + "）" + "[ssh异常离线]";
                String content = format + "：" + hospitalName + "[ssh异常离线]";
                emailUtil.sendMessage(to, title, content);
            }

        }
    }

    //定时任务N*秒
//    @Scheduled(fixedDelay = 60 * 1000)
    public void m1() throws IOException {
        List<Hospital> list = hospitalService.list(
                //设置过滤规则为匹配
                new QueryWrapper<Hospital>().lambda().isNotNull(Hospital::getSsh_port)
        );
        System.out.println("ssh_port:" + list.size());
        for (Hospital hospital : list) {
            SimpleDateFormat stf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = stf.format(new Date());
            String hospitalName = hospital.getHospital_name();
            Integer sshPort = hospital.getSsh_port();
            Integer sshStatus = hospital.getSsh_status();
            URL url = new URL("http://116.62.46.33:" + sshPort + "/Pain/index.html");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.connect();
                int httpStatusCode = connection.getResponseCode();
                if (httpStatusCode == 200 && sshStatus != 1) {
                    System.out.println(sshPort + "-->>http-200-->>" + format);
                    String s = httpRequest("http://localhost:9090/hospital/ssh1/" + sshPort, "POST", null);
                    System.out.println("http ok:" + sshPort);
                    // 测试文本邮件发送（无附件）
                    String to = "chenjinjin@apon.com.cn"; // 这是个假邮箱，写成你自己的邮箱地址就可以
                    String title = hospitalName + "（" + sshPort + "）" + "[ssh恢复正常]";
                    String content = format + "：" + hospitalName + "[ssh恢复正常]";
                    emailUtil.sendMessage(to, title, content);

                } else if (httpStatusCode != 200 && sshStatus != 0) {
                    System.out.println(sshPort + "-->>http-error-->>" + format);
                    String s = httpRequest("http://localhost:9090/hospital/ssh0/" + sshPort, "POST", null);
                }
            } catch (Exception e) {
                if (sshStatus != 0) {
                    System.out.println(sshPort + "-->>" + format + "-->>http-Exception-->>" + e);
                    String s = httpRequest("http://localhost:9090/hospital/ssh0/" + sshPort, "POST", null);
                    // 测试文本邮件发送（无附件）
                    String to = "chenjinjin@apon.com.cn"; // 这是个假邮箱，写成你自己的邮箱地址就可以
                    String title = hospitalName + "（" + sshPort + "）" + "[ssh异常离线]";
                    String content = format + "：" + hospitalName + "[ssh异常离线]";
                    emailUtil.sendMessage(to, title, content);
                }
            }
        }
    }
}
