package com.zhouhc.handler;


import com.zhouhc.dao.AddressDao;
import com.zhouhc.po.AddressPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

// address 的controller
@Component
public class AddressHandler {

    @Autowired
    private AddressDao addressDao;

    //查找用户
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok().body(addressDao.findAll(), AddressPo.class);
    }

    //查找某个指定的用户
    public Mono<ServerResponse> findById(ServerRequest request) {
        long id = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok().body(addressDao.findById(id), AddressPo.class);
    }

    //查找某个指定的用户名
    public Mono<ServerResponse> findByName(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(AddressPo.class).flatMapMany(addressPo -> addressDao.findByUserName(addressPo.getUserName())),
                AddressPo.class);
    }

    //删除某个地址
    public Mono<ServerResponse> deleteById(ServerRequest request) {
        long id = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok().body(addressDao.deleteById(id), Void.class);
    }

    //查找某个指定的用户地址
    public Mono<ServerResponse> save(ServerRequest request) {
        return ServerResponse.ok().body(request.bodyToMono(AddressPo.class).flatMap(addressDao::save), AddressPo.class);
    }

}
