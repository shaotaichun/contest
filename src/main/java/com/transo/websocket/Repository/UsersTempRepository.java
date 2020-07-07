package com.transo.websocket.Repository;

import com.transo.websocket.Pojo.UsersTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersTempRepository extends JpaRepository<UsersTemp,Integer>, JpaSpecificationExecutor<UsersTemp> {
    @Query(nativeQuery = true,value = "select `id`,`number`, `name`, `img`, `timeArr`, `totalTimes`, `key1`, `totalCount`,`belongRound`,`userType` from users where belongRound = :belongRound")
    List<UsersTemp> findAll3(@Param("belongRound")int belongRound);//比赛多轮次查找全部
    @Query(nativeQuery = true,value = "(select * from userstemp where belongRound = 1 ORDER BY totalCount desc,totalTimes asc LIMIT 0,3 )UNION(select * from userstemp where belongRound = 2 ORDER BY totalCount desc,totalTimes asc LIMIT 0,3)UNION(select * from userstemp where belongRound = 3 ORDER BY totalCount desc,totalTimes asc LIMIT 0,3)UNION(select * from userstemp where belongRound = 4 ORDER BY totalCount desc,totalTimes asc LIMIT 0,3) ")
    List<UsersTemp> findNine();

}
