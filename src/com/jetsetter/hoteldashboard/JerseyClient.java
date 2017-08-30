package com.jetsetter.hoteldashboard;

import com.jetsetter.hoteldashboard.model.AvailabilityData;
import com.sun.deploy.net.HttpResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import jdk.internal.dynalink.beans.StaticClass;
import sun.net.www.http.HttpClient;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class JerseyClient {

    private static JerseyClient jerseyClient = null;
    private static String URL_PATH = "http://localhost:8080/jetsetter/api/";

    public static JerseyClient getInstance() {
        if (jerseyClient == null) {
            jerseyClient = new JerseyClient();
        }
        return jerseyClient;
    }

    public String postData(String  data, String path) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource webResource = client.resource(UriBuilder.fromUri(URL_PATH+path).build());
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, data);
        return response.getEntity(String.class);
    }

    public ClientResponse getData(String path, MultivaluedMap<String, String> params ) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(UriBuilder.fromUri(URL_PATH).build());
        if(params != null){
            return service.path(path).queryParams(params).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        }
        return service.path(path).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
    }

}

