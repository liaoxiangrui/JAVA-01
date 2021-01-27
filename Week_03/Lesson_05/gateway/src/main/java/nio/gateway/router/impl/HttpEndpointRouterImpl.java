package nio.gateway.router.impl;

import nio.gateway.router.HttpEndpointRouter;

import java.util.List;
import java.util.Random;

/**
 * 作业四：（可选）：实现路由
 *
 * @author raw
 * @date 2021/1/23
 */
public class HttpEndpointRouterImpl implements HttpEndpointRouter {

    @Override
    public String randomRoute(List<String> endpoints) {
        Random random = new Random();
        int randomEndpoint = random.nextInt(endpoints.size());
        return endpoints.get(randomEndpoint);
    }
}
