package nio.gateway;

import nio.gateway.inbound.HttpInboundServer;

import java.util.Arrays;
import java.util.List;

/**
 * @author raw
 * @date 2021/1/23
 */
public class NettyServerApplication {

    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "3.0";

    public static void main(String[] args) {
        String proxyPort = System.getProperty("proxyPort", "8888");
        int port = Integer.parseInt(proxyPort);
        List<String> urlList = Arrays.asList("http://localhost:8801", "http://localhost:8802", "http://localhost:8803");
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION + " starting...");
        HttpInboundServer server = new HttpInboundServer(port, urlList);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION + " started at http://localhost:" + port + " for server:" + server.toString());
        try {
            server.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
