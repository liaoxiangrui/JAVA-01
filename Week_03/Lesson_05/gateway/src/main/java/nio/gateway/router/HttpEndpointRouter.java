package nio.gateway.router;

import java.util.List;

public interface HttpEndpointRouter {

    String randomRoute(List<String> endpoints);
}
