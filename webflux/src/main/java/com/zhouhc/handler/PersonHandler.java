package com.zhouhc.handler;



import com.zhouhc.po.PersonPo;
import com.zhouhc.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

// person 的 ontroller
@Component
public class PersonHandler {

    @Autowired
    private PersonService personService;

    //查找用户
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok().body(personService.findAll(), PersonPo.class);
    }

    //查找某个指定的用户
    public Mono<ServerResponse> findById(ServerRequest request) {
        long id = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok().body(personService.findById(id), PersonPo.class);
    }

    //查找某个指定的用户名
    public Mono<ServerResponse> findByName(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(PersonPo.class).flatMapMany(personPo -> personService.findByName(personPo.getName())),
                PersonPo.class);
    }

    //删除某个地址
    public Mono<ServerResponse> deleteById(ServerRequest request) {
        long id = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok().body(personService.deleteById(id), Void.class);
    }

    //查找某个指定的用户地址
    public Mono<ServerResponse> save(ServerRequest request) {
//        request.bodyToMono(PersonPo.class).doOnEach(s -> System.out.printf("onNext : %s%n",s)).doOnSubscribe(s -> System.out.printf("完成%n")).subscribe(date -> System.out.printf("onNext : %s%n",date));
//        try {
//            TimeUnit.MILLISECONDS.sleep(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return ServerResponse.ok().body(BodyInserters.fromValue("sssssssss"));
        return ServerResponse.ok().body(request.bodyToMono(PersonPo.class).flatMap(personService::save), PersonPo.class);
    }

}
