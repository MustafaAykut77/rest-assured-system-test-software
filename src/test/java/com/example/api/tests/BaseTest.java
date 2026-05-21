package com.example.api.tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;

/**
 * Tüm test sınıflarının miras aldığı temel yapılandırma sınıfı.
 * Ortak ayarları (base URL, headers, logging) merkezi olarak yönetir.
 */
public class BaseTest {

    protected static RequestSpecification requestSpec;

    // Performans eşik değerleri (milisaniye)
    protected static final long MAX_RESPONSE_TIME_MS = 3000L;
    protected static final long FAST_RESPONSE_TIME_MS = 1000L;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())   // İstekleri logla
                .addFilter(new ResponseLoggingFilter())  // Yanıtları logla
                .build();
    }
}
