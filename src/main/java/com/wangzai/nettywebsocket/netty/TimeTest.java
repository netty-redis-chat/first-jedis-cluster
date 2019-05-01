package com.wangzai.nettywebsocket.netty;

import io.netty.util.internal.logging.InternalLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTest {
    static Logger log = LoggerFactory.getLogger("wbx");
    public static void main(String[] args) {
        Date pre = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
        int x = 0;
        for (int i = 0; i < 10000000; i++){
            x++;
            log.info("111");
        }
        System.out.println(x);
        System.out.println(sdf.format(new Date()));
        System.out.println(sdf.format(pre));
    }
}
