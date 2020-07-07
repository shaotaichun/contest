package com.transo.websocket.Pojo;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 11, columnDefinition = "int COMMENT' 选手表id'")
    private Integer id;
    @Column(length = 11,columnDefinition = "int COMMENT'选手编号'")
    private Integer number;
    @Column(columnDefinition = "varchar(255) COMMENT' 选手名称'")
    private String name;
    @Column(columnDefinition = "varchar(255) COMMENT' 图片'")
    private String img;
    @Column(columnDefinition = "varchar(255) COMMENT' 选手每次开锁时间数组'")
    private String timeArr;
    @Column(length = 11,columnDefinition = "int COMMENT'总耗时'")
    private Integer totalTimes;
    @Column(length = 11,columnDefinition = "int COMMENT'key'")
    private Integer key1;
    @Transient
    private String[] timeArrays;
    @Column(length = 11,columnDefinition = "int COMMENT'开锁数量'")
    private Integer totalCount;
    @Column(length = 11,columnDefinition = "int COMMENT'所属轮次'")
    private Integer belongRound;
    @Column(columnDefinition = "varchar(11) COMMENT'参赛者组别，1-员工组，2-学生组'")
    private Integer userType;


}
