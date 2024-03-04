package com.roukaixin.service.impl;

import com.roukaixin.service.AuthenticationService;
import org.springframework.stereotype.Service;

/**
 * 认证管理器
 *
 * @author 不北咪
 * @date 2024/3/4 下午10:31
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public void oauth2RequestRedirect(String registrationId) {

    }

//    @SneakyThrows
//    @Async
//    public void runShell(SseEmitter sseEmitter, String id) {
//        Runtime runtime = Runtime.getRuntime();
//        Process exec = runtime.exec(new String[]{"/bin/sh", "-c", "echo 1 && sleep 5 && echo 1"});
//        BufferedReader reader = exec.inputReader();
//        String flag;
//        // SseEmitter sseEmitter = AuthenticationController.sse.get(id);
//        while ((flag = reader.readLine()) != null) {
//            sseEmitter.send(flag);
//        }
//        sseEmitter.complete();
//
//    }
}
