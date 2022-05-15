package com.zhouhc.handler;

import com.zhouhc.po.PersonPo;
import com.zhouhc.security.MyUserDetails;
import com.zhouhc.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


// person 的 ontroller
@Component
public class PersonHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(PersonHandler.class);

    @Autowired
    private PersonService personService;

    //查找用户
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).flatMap(authentication -> {
            LOGGER.info("authentication类型:{},Principal类型:{},credentials类型:{},用户名:{}", authentication.getClass(),
                    authentication.getPrincipal().getClass(),
                    authentication.getCredentials().getClass(),
                    ((MyUserDetails) authentication.getPrincipal()).getUsername());
            return ServerResponse.ok().body(personService.findAll(), PersonPo.class);
        });
//        return ServerResponse.ok().body(personService.findAll(), PersonPo.class);
    }

    //查找某个指定的用户,算是某种实现的自定义返回数据了
    public Mono<ServerResponse> findById(ServerRequest request) {
        Mono<ServerResponse> build = ServerResponse.notFound().build();
        long id = Long.valueOf(request.pathVariable("id"));
//        return ServerResponse.ok().body(personService.findById(id), PersonPo.class);
        return personService.findById(id).map(personPo -> {
            Map<String,Object> temp = new HashMap<String,Object>();
            temp.put("code","444444");
            temp.put("data",personPo);
            return temp;
        }).flatMap(personMap -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(personMap)).switchIfEmpty(build));
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
