package com.zhouhc.service;

import com.zhouhc.dao.AddressDao;
import com.zhouhc.po.AddressPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


// address 的service
@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    //查找地址
    public Flux<AddressPo> findAll(){
        return addressDao.findAll();
    }

    //查找某个指定的地址
    public Mono<AddressPo> findById(long id){
        return addressDao.findById(id);
    }

    //查找某个指定的用户地址
    public Flux<AddressPo> findByName(String userName){
        return addressDao.findByUserName(userName);
    }

    //删除某个地址
    public Mono<Void> deleteById(long id){
        return addressDao.deleteById(id);
    }

    //保存某个用户的地址
    public Mono<AddressPo> save(AddressPo addressPo){
        return addressDao.save(addressPo);
    }
}
