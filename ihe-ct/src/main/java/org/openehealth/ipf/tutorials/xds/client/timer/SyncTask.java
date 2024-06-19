package org.openehealth.ipf.tutorials.xds.client.timer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.openehealth.ipf.tutorials.xds.client.exec.SystemCmd;
import org.openehealth.ipf.tutorials.xds.client.ntp.NtpClient;
import org.openehealth.ipf.tutorials.xds.client.timezone.TimeZoneClient;

import javax.annotation.Resource;

/**
 * @author BoVane
 * @version 1.0
 * @description: TODO
 * @date 2023/7/6 13:28
 */
@Component
@Slf4j
public class SyncTask {

    @Resource
    private NtpClient ntpClient;
    @Resource
    private SystemCmd systemCmd;
    @Resource
    private TimeZoneClient timeZoneClient;

//    @Scheduled(cron = "0/2 * * * * ?")
//    public void doTask() {
//        systemCmd.setWindowsTimeZone(timeZoneClient.getByBio());
//        Date serverTime = ntpClient.getServerTime();
//        ExecResult execResult = systemCmd.changeTime(serverTime);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
//        TimeZone timeZone = systemCmd.getTimeZone();
//        simpleDateFormat.setTimeZone(timeZone);
//        log.info("时区:" + timeZone.getID() +" 时间:" +simpleDateFormat.format(ntpClient.getServerTime()));
//        log.info("处理结果:" + execResult.toString());
//    }
}
