package net.hwyz.iov.cloud.gateway.route;

import cn.hutool.core.collection.ListUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认路由仓库
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class DefaultRouteDefinitionRepository implements RouteDefinitionRepository, ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;
    private List<RouteDefinition> routeDefinitionList = new ArrayList<>();

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostConstruct
    public void init() {
        load();
    }

    /**
     * 监听事件刷新配置
     */
    @EventListener
    public void listenEvent(RefreshRoutesEvent event) {
        load();
//        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 加载
     */
    private void load() {
        try {
            List<RouteDefinition> list = new ArrayList<>();
            RouteDefinition route = new RouteDefinition();
            route.setId("account-service");
            route.setUri(URI.create("lb://account-service"));
            route.setPredicates(ListUtil.of(new PredicateDefinition("Path=/account/**")));
            route.setFilters(ListUtil.of(new FilterDefinition("StripPrefix=1"), new FilterDefinition("Authentication")));
            list.add(route);
            routeDefinitionList = list;
            logger.info("路由配置已加载,加载条数:{}", routeDefinitionList.size());
        } catch (Exception e) {
            logger.error("从文件加载路由配置异常", e);
        }
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return Mono.defer(() -> Mono.error(new NotFoundException("Unsupported operation")));
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return Mono.defer(() -> Mono.error(new NotFoundException("Unsupported operation")));
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routeDefinitionList);
    }

}
