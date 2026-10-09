package ru.dsobin.otus.spring.boot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class JsonHelperUtils {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static <T> String getStringFromObject(T value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing object to JSON", e); // Обёртка в непроверяемое исключение
        }
    }


    public static <T> List<T> parseJsonList(String json, Class<?> aClass) {
        return parseJsonCollection(json, aClass, List.class);
    }

    public static <T> List<T> parseJsonCollection(String json, Class<?> aClass, Class<? extends Collection> cClass) {
        if (json == null) {
            throw new NullPointerException("json is null");
        }
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(cClass, aClass);
        try {
            return mapper.readValue(json, collectionType);
        } catch (IOException e) {
            log.error("Failed to parse data response:\n{}", json);
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    public static <T> List<T> parseJsonList(File file, Class<?> aClass) {
        return parseJsonCollection(file, aClass, List.class);
    }

    public static <T> List<T> parseJsonCollection(File file, Class<?> aClass, Class<? extends Collection> cClass) {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(cClass, aClass);
        try {
            return mapper.readValue(file, collectionType);
        } catch (IOException e) {
            log.error("Failed to parse File response:\n{}", file);
            throw new RuntimeException("Error parsing File", e);
        }
    }

    public static <T> T parseJson(String json, Class<T> aClass) {
        if (json == null) {
            throw new NullPointerException("json is null");
        }
        try {
            return mapper.readValue(json, aClass);
        } catch (IOException e) {
            log.error("Failed to parse JSON response:\n{}", json);
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    public static <T> T parseJson(File file, Class<T> aClass) {
        if (file == null) {
            throw new NullPointerException("File is null");
        }
        try {
            return mapper.readValue(file, aClass);
        } catch (IOException e) {
            log.error("Failed to parse File response:\n{}", file);
            throw new RuntimeException("Error parsing File", e);
        }
    }

    public static <K, V> Map<K, V> parseJsonMap(String json, Class<?> keyClass, Class<?> valueClass, Class<? extends Map> mapClass) {
        if (json == null) {
            throw new NullPointerException("file is null");
        }
        JavaType keyType = mapper.getTypeFactory().constructType(keyClass);
        JavaType valueType = mapper.getTypeFactory().constructType(valueClass);
        JavaType mapType = mapper.getTypeFactory().constructMapType(mapClass, keyType, valueType);
        try {
            return mapper.readValue(json, mapType);
        } catch (IOException e) {
            log.error("Failed to parse JSON response:\n{}", json);
            throw new RuntimeException("Error parsing JSON", e);
        }
    }


    public static <K, V> Map<K, V> parseJsonMap(File file, Class<?> keyClass, Class<?> valueClass, Class<? extends Map> mapClass) {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        JavaType keyType = mapper.getTypeFactory().constructType(keyClass);
        JavaType valueType = mapper.getTypeFactory().constructType(valueClass);
        JavaType mapType = mapper.getTypeFactory().constructMapType(mapClass, keyType, valueType);
        try {
            return mapper.readValue(file, mapType);
        } catch (IOException e) {
            log.error("Failed to parse File response\n{}", file, e);
            throw new RuntimeException("Error parsing File", e);
        }
    }
}
