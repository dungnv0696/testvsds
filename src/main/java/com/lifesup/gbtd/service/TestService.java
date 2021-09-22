package com.lifesup.gbtd.service;

import com.lifesup.gbtd.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService extends BaseService {

    public void test() {
        FileUtil.getInstance().getServerInfo();
    }
}
