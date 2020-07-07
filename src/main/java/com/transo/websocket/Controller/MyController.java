package com.transo.websocket.Controller;

import com.alibaba.fastjson.JSONObject;
import com.transo.websocket.Pojo.*;
import com.transo.websocket.Repository.*;
import com.transo.websocket.Tools.HttpClientUtils;
import com.transo.websocket.Tools.JsonResult;
import com.transo.websocket.Tools.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@Api(value = "总工会比赛操作修改", tags = "总工会比赛操作")
public class MyController {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private DefaultTimeRepository defaultTimeRepository;
    @Autowired
    private MainEventRepository mainEventRepository;
    @Autowired
    private UsersTempRepository usersTempRepository;

    @Transactional
    @GetMapping(value = "/demo")
    @ApiIgnore
    public String demo(){
        String url = "http://127.0.0.1:8089/putInfo";//测试
        String content = "{\"id\":1}";
        String result = HttpClientUtils.httpPost(url,content);
        return result;
    }
    /**
     * 远程调用接口
     * @return
     */
    @Transactional
    @PostMapping(value = "/putInfo")
    @ApiOperation(value = "远程调用接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "id", value = "参赛者ID", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "body", name = "nowRound", value = "当前关卡", required = true, dataType = "Integer"),
    })
    public String putInfo(@RequestBody JSONObject obj){
        String url = "http://127.0.0.1:8089/push/1";//测试
        String result = HttpClientUtils.httpPost(url, obj.toJSONString());
        return result;
    }
    /**
     * 对接前端WebSocket实时更新
     * @param obj
     * @param toUserId
     * @return
     * @throws IOException
     */
    @Transactional
    @PostMapping("/push/{toUserId}")
    @ApiIgnore
    public ResponseEntity<String> pushToWeb(@RequestBody JSONObject obj, @PathVariable String toUserId) throws IOException {
        int id  = obj.getInteger("id");
        int nowRound  = obj.getInteger("nowRound");//当前参赛者的当前关卡
        int round = mainEventRepository.findAllById(1).getNowRound();//当前轮次
        Users allById = usersRepository.findAllByBelongRoundAndAndKey1(round,id);
        DefaultTime defaultTime = defaultTimeRepository.findAllByUserId(allById.getId());//时间计算表
        Date mainTime = defaultTime.getMainTime();
        long costTime = (new Date().getTime() - mainTime.getTime())/1000;//当前关卡花费的时间
        if (nowRound != allById.getTotalCount()) {
            String timeArr = allById.getTimeArr();
            if (timeArr.length() == 2) {
                timeArr = timeArr.substring(0, timeArr.length() - 1) + costTime + "]";
            } else {
                timeArr = timeArr.substring(0, timeArr.length() - 1) + "," + costTime + "]";
            }
            allById.setTimeArr(timeArr);
            allById.setTotalCount(allById.getTotalCount() + 1);
            Users save = usersRepository.save(allById);
            List<Users> all = usersRepository.findAll1(round);
            for (Users a : all) {
                String substring = a.getTimeArr().substring(1, a.getTimeArr().length() - 1);
                String[] newString = substring.split(",");
                if (substring == null || "".equals(substring)) {
                    newString = new String[0];
                }
                a.setTimeArrays(newString);
                int totalTime = Arrays.stream(newString).parallel().map(e ->Integer.parseInt(e)).reduce(0,Integer::sum,Integer::sum);
                a.setTotalTimes(totalTime);
                usersRepository.save(a);
            }
            List<Users> all2 = usersRepository.findAll1(round);
            defaultTime.setMainTime(new Date());
            defaultTimeRepository.save(defaultTime);

            Map<String, Object> map = new HashMap<>();
            map.put("action", "getUserSingle");
            map.put("sort", all2);
            map.put("singleItem", save);
            WebSocketServer.sendInfo(net.sf.json.JSONObject.fromObject(map).toString(), toUserId);

        }else{
            List<Users> all2 = usersRepository.findAll1(round);
            for (Users a : all2) {
                String substring = a.getTimeArr().substring(1, a.getTimeArr().length() - 1);
                String[] newString = substring.split(",");
                if (substring == null || "".equals(substring)) {
                    newString = new String[0];
                }
                a.setTimeArrays(newString);
                int totalTime = Arrays.stream(newString).parallel().map(e ->Integer.parseInt(e)).reduce(0,Integer::sum,Integer::sum);
                a.setTotalTimes(totalTime);
                usersRepository.save(a);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("action", "getUserSingle");
            map.put("sort", all2);
            map.put("singleItem", allById);
            WebSocketServer.sendInfo(net.sf.json.JSONObject.fromObject(map).toString(), toUserId);
        }
        return ResponseEntity.ok("MSG SEND SUCCESS");
    }

    /**
     * websocket初始化
     * @return
     */
    @Transactional
    @GetMapping ("/getAll")
    @ApiIgnore
    public  Map<String,Object> getAll() {
        int round = mainEventRepository.findAllById(1).getNowRound();
        Map<String,Object> map  = new HashMap<>();
        List<Users> all = usersRepository.findAll1(round);
        for (Users a:all) {
            String substring = a.getTimeArr().substring(1, a.getTimeArr().length() - 1);
            String[] newString = substring.split(",");
            if (substring == null || "".equals(substring)){
                newString = new String[0];
            }
            a.setTimeArrays(newString);
        }
        //已经过了多少时间
        int nowGetTime = mainEventRepository.findAllById(1).getStartDate();
        map.put("action","getUserAll");
        map.put("sort",all);
        map.put("isBegin",mainEventRepository.findAllById(1).getIsStart());
        map.put("nowRound",mainEventRepository.findAllById(1).getNowRound());
        map.put("nowGetTime",nowGetTime);
        map.put("isEnd", mainEventRepository.findAllById(1).getIsEnd());
        return map;
    }
    @Transactional
    @GetMapping ("/getUserAll")
    @ApiIgnore
    public  Map<String,Object> getUserAll() {
        Map<String,Object> map  = new HashMap<>();
        map.put("action","getAll");
        map.put("isBegin",mainEventRepository.findAllById(1).getIsStart());
        map.put("nowRound",mainEventRepository.findAllById(1).getNowRound());
        return map;
    }

  /*  *//**
     * 初始化参赛者的信息
     * @return
     *//*
    @Transactional
    @PostMapping ("/initializeData")
    @ApiOperation(value = "初始化参赛者的信息")
    public JsonResult initializeData() {
        usersRepository.updateAllUsers();
        MainEvent allById = mainEventRepository.findAllById(1);
        allById.setStartDate(0);
        allById.setIsStart("0");
        allById.setIsEnd("0");
        mainEventRepository.save(allById);
        return new JsonResult(200);
    }*/

    /**
     * 比赛开始
     * @return
     */
 /*   @Transactional
    @PostMapping ("/beginContest")
    @ApiOperation(value = "比赛开始")
    public JsonResult beginContest() throws IOException {
        int round = mainEventRepository.findAllById(1).getNowRound();
        usersRepository.updateAllUsers(round);
        Date date = new Date();
        defaultTimeRepository.updateAllDefaultTime(date);
        MainEvent allById = mainEventRepository.findAllById(1);
        allById.setStartDate(0);
        allById.setIsStart("1");
        allById.setIsEnd("0");
        mainEventRepository.save(allById);
        return new JsonResult(date,200);
    }*/
    @Transactional
    @GetMapping ("/sendResult")
    @ApiOperation(value = "比赛结束时间")
    public JsonResult sendResult() {
        MainEvent allById = mainEventRepository.findAllById(1);
        List<Users> all = usersRepository.findAllByBelongRound(allById.getNowRound());
        Boolean b = true;
        for (Users users:all) {
            if (users.getTotalCount() !=4){
                b = false;//表示还有人没开完全部的锁
            }
        }
        if ("1".equals(allById.getIsEnd())||"0".equals(allById.getIsStart())){
            return new JsonResult("比赛未开始或者已经结束!");
        }
        else if (allById.getStartDate()>=allById.getEndDate()) {
            allById.setIsEnd("1");
            allById.setIsStart("0");
            mainEventRepository.save(allById);
            return new JsonResult("时间到,比赛结束!", 200);
        }else if(b==true){
            allById.setIsEnd("1");
            allById.setIsStart("0");
            mainEventRepository.save(allById);
            return new JsonResult("所有人都开完了锁,比赛结束!", 200);
        }
       return new JsonResult("比赛还在进行中!");
    }
    @Transactional
    @GetMapping ("/ranking")
    @ApiOperation(value = "比赛结束，名次排行")
    public JsonResult ranking(){
        int round = mainEventRepository.findAllById(1).getNowRound();
        List<Users> all2 = usersRepository.findAll2(round-1);
        MainEvent mainEvent = mainEventRepository.findAllById(1);
        Integer nowRound = mainEvent.getNowRound()-1;
        Map<String,Object> map = new HashMap<>();
        map.put("nowRound",nowRound);
        map.put("usersRank",all2);
        return  new JsonResult(map,200);
    }
    @Transactional
    @GetMapping ("/rankingFor4")
    @ApiOperation(value = "比赛结束，查看不同组别的比赛者名次排行")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "param", name = "type", value = "参赛者类型", required = true, dataType = "String"),
    })
    public JsonResult rankingFor4(){
            List<UsersTemp>  all2 = usersTempRepository.findNine();
            List<UsersTemp> collect = all2.stream().sorted(Comparator.comparing(UsersTemp::getTotalCount).reversed().thenComparing(UsersTemp::getTotalTimes)).collect(Collectors.toList());
            //Collections.sort(all2);
            //List<UsersTemp> collect = all2.stream().peek(s ->{s.setName(s.getName()+"123");s.setNumber(s.getNumber()+1);}).collect(Collectors.toList());
        System.out.println(collect);
        return  new JsonResult(collect,200);
    }
    @Transactional
    @PostMapping ("/beginWithInstruct")
    @ApiOperation(value = "总比赛开始进行初始化")
    public JsonResult beginWithInstruct() throws IOException {
        usersRepository.updateAllUsers2();
        Date date = new Date();
        defaultTimeRepository.updateAllDefaultTime(date);
        MainEvent allById = mainEventRepository.findAllById(1);
        allById.setStartDate(0);
        allById.setIsStart("0");
        allById.setIsEnd("0");
        allById.setNowRound(1);
        mainEventRepository.save(allById);
        usersTempRepository.deleteAll();
        WebSocketServer.sendInfo("{\"action\":\"BEGIN\"}","1");
        return new JsonResult(date,200);
    }

    @Transactional
    @PostMapping ("/sendRedirect")
    @ApiOperation(value = "发送跳转请求")
    public JsonResult sendRedirect() throws IOException {
        int round = mainEventRepository.findAllById(1).getNowRound();
        WebSocketServer.sendInfo("{\"action\":\"REDIRECT\"}", "1");
        usersRepository.updateAllUsers(round);
        Date date = new Date();
        defaultTimeRepository.updateAllDefaultTime(date);
        MainEvent allById = mainEventRepository.findAllById(1);
        allById.setStartDate(0);
        allById.setIsStart("1");
        allById.setIsEnd("0");
        mainEventRepository.save(allById);
        return new JsonResult(date,200);
    }
    @Transactional
    @GetMapping ("/goIndex")
    @ApiOperation(value = "跳转首页")
    public JsonResult goIndex() throws IOException {
        WebSocketServer.sendInfo("{\"action\":\"goIndex\"}", "1");
        return new JsonResult(200);
    }
    @Transactional
    @GetMapping ("/goRanking")
    @ApiOperation(value = "跳转排名")
    public JsonResult goRanking() throws IOException {
        WebSocketServer.sendInfo("{\"action\":\"goRanking\"}", "1");
        return new JsonResult(200);
    }

}
