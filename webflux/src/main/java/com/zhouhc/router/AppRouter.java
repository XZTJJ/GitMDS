package com.zhouhc.router;

import com.zhouhc.handler.AddressHandler;
import com.zhouhc.handler.PersonHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

// 应用的路由器
@Configuration
public class AppRouter {

    @Bean
    public RouterFunction<ServerResponse> personRouter(PersonHandler personHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/person"), personHandler::findAll)
                .andRoute(RequestPredicates.GET("/person/{id}"), personHandler::findById)
                .andRoute(RequestPredicates.POST("/person/name"), personHandler::findByName)
                .andRoute(RequestPredicates.DELETE("/person/{id}"), personHandler::deleteById)
                .andRoute(RequestPredicates.POST("/person/save"), personHandler::save);
    }

    @Bean
    public RouterFunction<ServerResponse> AddressRouter(AddressHandler addressHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/addr"), addressHandler::findAll)
                .andRoute(RequestPredicates.GET("/addr/{id}"), addressHandler::findById)
                .andRoute(RequestPredicates.POST("/addr/name"), addressHandler::findByName)
                .andRoute(RequestPredicates.DELETE("/addr/{id}"), addressHandler::deleteById)
                .andRoute(RequestPredicates.POST("/addr/save"), addressHandler::save);
    }

}
