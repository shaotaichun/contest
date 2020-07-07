package com.transo.websocket.Pojo;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "defaultTime")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 11, columnDefinition = "int COMMENT' 时间计算表id'")
    private Integer id;
    @Column(name="userId",length = 11,columnDefinition = "int COMMENT'选手编号'")
    private Integer userId;
    @Column(name="mainTime",columnDefinition = "datetime comment '计算时间'")
    private Date mainTime;
}
