package work.mediway.ihe.client.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import work.mediway.ihe.client.entity.ExecResult;
import work.mediway.ihe.client.exec.SystemCmd;
import work.mediway.ihe.client.ntp.NtpClient;
import work.mediway.ihe.client.timezone.TimeZoneClient;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author Scallion
 * @version 1.0
 * @description: TODO
 * @date 2023/6/5 10:54
 */
@RestController
@RequestMapping("/ntpAnysc")
public class NtpTest {
    @Resource
    private NtpClient ntpClient;
    @Resource
    private SystemCmd systemCmd;
    @Resource
    private TimeZoneClient timeZoneClient;

    @GetMapping("/getTime")
    @ResponseBody
    public String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
        TimeZone timeZone = systemCmd.getTimeZone();
        simpleDateFormat.setTimeZone(timeZone);
        return "时区:" + timeZone.getID() +" 时间:" +simpleDateFormat.format(ntpClient.getServerTime());
    }

    @GetMapping("/syncTime")
    @ResponseBody
    public ExecResult syncTime() {
        this.syncWindowsTimezone();
        return systemCmd.changeTime(ntpClient.getServerTime());
    }

    public void syncWindowsTimezone(){
        systemCmd.setWindowsTimeZone(timeZoneClient.getByBio());
    }
    @GetMapping("/sendMessage")
    public String sendMessage(){
        return timeZoneClient.getByBio();
    }
}
