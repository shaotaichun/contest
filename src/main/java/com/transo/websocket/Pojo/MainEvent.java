package com.transo.websocket.Pojo;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "mainEvent")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 11, columnDefinition = "int COMMENT' 主键ID'")
    private Integer id;
    @Column(name="startDate",columnDefinition = "int(11) comment '开始时间'")
    private Integer startDate;
    @Column(columnDefinition = "varchar(11) COMMENT' 是否已经结束，0-未结束,1-结束'")
    private String isEnd;
    @Column(name="endDate",columnDefinition = "int(11) comment '结束时间'")
    private Integer endDate;
    @Column(columnDefinition = "varchar(11) COMMENT' 是否已经结束，0-未开始,1-开始'")
    private String isStart;
    @Column(name="nowRound",columnDefinition = "int(11) comment '当前轮次'")
    private Integer nowRound;
}
