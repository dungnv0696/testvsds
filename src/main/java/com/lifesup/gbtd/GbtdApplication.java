package com.lifesup.gbtd;

import com.lifesup.gbtd.server.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@ServletComponentScan
@Slf4j
public class GbtdApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        log.info("starting app ...");
        SpringApplication.run(GbtdApplication.class, args);
    }
}