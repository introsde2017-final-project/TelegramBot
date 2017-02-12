package introsde.telegramservice.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;


public class BotClient {

	private WebTarget service;
    private static BotClient instance;


    private BotClient() {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        service = client.target(getBaseURI());
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("https://process-centric--service.herokuapp.com/fitApp/").build();
    }

    public static WebTarget getService() {
        if (instance == null) {
            instance = new BotClient();
        }
        return instance.service;
    }

}
