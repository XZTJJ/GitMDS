package com.zhouhc.service;

import com.zhouhc.dao.PersonDao;
import com.zhouhc.po.PersonPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// 用户的 的service
@Service
public class PersonService {
    @Autowired
    private PersonDao personDao;

    //查找用户
    public Flux<PersonPo> findAll(){
        return personDao.findAll();
    }

    //查找某个指定的用户
    public Mono<PersonPo> findById(long id){
        return personDao.findById(id);
    }

    //查找某个指定的用户名
    public Flux<PersonPo> findByName(String userName){
        return personDao.findByName(userName);
    }

    //删除某个地址
    public Mono<Void> deleteById(long id){
        return personDao.deleteById(id);
    }

    //查找某个指定的用户地址
    public Mono<PersonPo> save(PersonPo personPo){
        return personDao.save(personPo);
    }
}
