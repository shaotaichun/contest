package com.transo.websocket.Repository;

import com.transo.websocket.Pojo.Users;

import com.transo.websocket.Pojo.UsersTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users,Integer>, JpaSpecificationExecutor<Users> {
        Users findAllById(int id);
        @Query("FROM Users where belongRound=:belongRound ORDER BY totalCount DESC ,totalTimes ASC,key1 ASC")
        List<Users> findAll1(@Param("belongRound")int belongRound);
        @Modifying
        @Query(nativeQuery = true,value = "UPDATE `users` SET `timeArr` = '[]', `totalTimes` = 0,  `totalCount` = 0 where belongRound = :belongRound")
        void updateAllUsers(@Param("belongRound")int belongRound);
        @Query("FROM Users where belongRound=:belongRound ORDER BY totalCount DESC ,totalTimes ASC")
        List<Users> findAll2(@Param("belongRound")int belongRound);//比赛排名
        @Modifying
        @Query(nativeQuery = true,value = "UPDATE `users` SET `timeArr` = '[]', `totalTimes` = 0,  `totalCount` = 0 ")
        void updateAllUsers2();
        List<Users> findAllByBelongRound(int belongRound);
        Users findAllByBelongRoundAndAndKey1(int belongRoundId,int key1);




}

