package com.zhouhc.dao;

import com.zhouhc.po.AddressPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

//用于操作数据库的 address 表
@Repository
public interface AddressDao extends ReactiveCrudRepository<AddressPo, Long> {
    Flux<AddressPo> findByUserName(String name);
}
