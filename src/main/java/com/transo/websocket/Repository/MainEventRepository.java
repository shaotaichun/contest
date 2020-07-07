package com.transo.websocket.Repository;

import com.transo.websocket.Pojo.DefaultTime;
import com.transo.websocket.Pojo.MainEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MainEventRepository extends JpaRepository<MainEvent,Integer>, JpaSpecificationExecutor<MainEvent> {
    MainEvent findAllById(int id);
}
