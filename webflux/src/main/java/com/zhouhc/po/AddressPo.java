package com.zhouhc.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

//地址的数据库映射类
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("address")
public class AddressPo {
    @Id
    private long id;

    @Column("name")
    private String userName;

    @Column("province")
    private String pro;

    private String city;

    @Column("addr")
    private String detailAddr;

    private LocalDateTime updatetime;
}
