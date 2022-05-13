package com.zhouhc.dao;

import com.zhouhc.po.PersonPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

// 用于操作数据库的 person 表
@Repository
public interface PersonDao extends ReactiveCrudRepository<PersonPo, Long> {
    Flux<PersonPo> findByName(String name);
}
