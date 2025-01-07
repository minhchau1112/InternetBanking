package com.example.backend.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSonUtil {
    public JSonUtil() {
    }

    public static String toJson(Object object) throws JsonProcessingException {
        // Convert the body to a compact JSON string without altering value types
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Serialize the original body directly into a JSON string
        String jsonBody = objectMapper.writeValueAsString(object);
        return jsonBody;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return null;
    }
}
