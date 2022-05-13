package com.zhouhc.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

//用户的数据库映射类
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("person")
public class PersonPo implements Serializable {
    @Id
    private long id;

    private String name;

    private int age;

    @Column("sex")
    private boolean isMan;

    @Column("birthDay")
    private LocalDateTime birth;
}
