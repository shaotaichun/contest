package com.transo.websocket.Config;

import com.transo.websocket.Pojo.MainEvent;
import com.transo.websocket.Pojo.Users;
import com.transo.websocket.Pojo.UsersTemp;
import com.transo.websocket.Repository.MainEventRepository;
import com.transo.websocket.Repository.UsersRepository;
import com.transo.websocket.Repository.UsersTempRepository;
import com.transo.websocket.Tools.HttpClientUtils;
import com.transo.websocket.Tools.WebSocketServer;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@EnableScheduling
public class TimeTask {
    @Autowired
    WebSocketServer webSocketServer;
    @Autowired
    private MainEventRepository mainEventRepository;
    @Autowired
    private UsersTempRepository usersTempRepository;

    @Scheduled(fixedDelay = 1000-18)
    public void sendResultScheduled() throws Exception {
        long a = new Date().getTime();
        if (!"1".equals(mainEventRepository.findAllById(1).getIsEnd()) && !"0".equals(mainEventRepository.findAllById(1).getIsStart())) {
            MainEvent allById = mainEventRepository.findAllById(1);
            allById.setStartDate(allById.getStartDate() + 1);
            mainEventRepository.save(allById);
        }
        Integer nowRound = mainEventRepository.findAllById(1).getNowRound();
        String urlJudge = "http://127.0.0.1:8089/sendResult";
        String result = HttpClientUtils.httpGet(urlJudge);
        JSONObject jsonObject = JSONObject.fromObject(result);
        if ("SUCCESS".equals(jsonObject.getString("status"))) {//比赛结束
            List<UsersTemp> all3 = usersTempRepository.findAll3(nowRound);
            for (UsersTemp all:all3) {
                all.setId(null);
                all.setBelongRound(nowRound);
                usersTempRepository.save(all);
            }
            MainEvent mainEvent = mainEventRepository.findAllById(1);
            mainEvent.setNowRound(nowRound+1);
            mainEventRepository.save(mainEvent);
            webSocketServer.sendInfo("{\"action\":\"OVER\"}", "1");
        }
        long b = new Date().getTime();
        System.out.println("定时时间差为"+(b-a)+"毫秒");
    }

}
