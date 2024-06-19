package org.openehealth.ipf.tutorials.xds.client.controller;


import org.openehealth.ipf.tutorials.xds.client.entity.ExecResult;
import org.openehealth.ipf.tutorials.xds.client.exec.SystemCmd;
import org.openehealth.ipf.tutorials.xds.client.ntp.NtpClient;
import org.openehealth.ipf.tutorials.xds.client.timezone.TimeZoneClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author BoVane
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
        // 获取本机时区
        TimeZone timeZone = systemCmd.getTimeZone();
        simpleDateFormat.setTimeZone(timeZone);
        return "时区:" + timeZone.getID() +" 时间:" +simpleDateFormat.format(ntpClient.getServerTime());
    }

    @GetMapping("/syncTime")
    @ResponseBody
    public ExecResult syncTime() {
        // 设置时区
        this.syncWindowsTimezone();
        // 设置时间
        return systemCmd.changeTime(ntpClient.getServerTime());
    }

    public void syncWindowsTimezone(){
        systemCmd.setWindowsTimeZone(timeZoneClient.getByBio());
    }
    @GetMapping("/getZone")
    public String getTimeZone(){
        return timeZoneClient.getByBio();
    }
}
