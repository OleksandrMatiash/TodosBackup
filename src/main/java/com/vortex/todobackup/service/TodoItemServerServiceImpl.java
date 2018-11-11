package com.vortex.todobackup.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vortex.todobackup.domain.UserEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class TodoItemServerServiceImpl implements TodoItemServerService {

    private static final TypeReference<List<UserEntity>> USER_ENTITIES_TYPE_REF = new TypeReference<List<UserEntity>>() {
    };
    private static final HttpClient httpClient = HttpClientBuilder.create().build();
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Value("${TodoItemServerUri}")
    private String serverUri;

    @Override
    public List<UserEntity> getAllUserEntities() {
        try {
            HttpResponse response = httpClient.execute(new HttpGet(serverUri));
            InputStream content = response.getEntity().getContent();
            return mapper.readValue(content, USER_ENTITIES_TYPE_REF);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
