package com.transo.websocket.Repository;

import com.transo.websocket.Pojo.DefaultTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface DefaultTimeRepository extends JpaRepository<DefaultTime,Integer>, JpaSpecificationExecutor<DefaultTime> {
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE `defaulttime` SET `mainTime` = :date")
    void updateAllDefaultTime(@Param("date")Date date);
    DefaultTime findAllByUserId(int userId);
}
