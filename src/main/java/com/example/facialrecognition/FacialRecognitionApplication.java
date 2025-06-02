package com.example.facialrecognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class FacialRecognitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacialRecognitionApplication.class, args);
    }

    /**
     * Configures a request logging filter to log incoming requests.
     * This is useful for debugging and understanding what requests are being made.
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true); // Include client IP address
        loggingFilter.setIncludeQueryString(true); // Include query parameters
        loggingFilter.setIncludePayload(true); // Include request payload (body)
        loggingFilter.setMaxPayloadLength(64000); // Max payload length to log
        loggingFilter.setIncludeHeaders(true); // Include request headers
        loggingFilter.setAfterMessagePrefix("REQUEST DATA: "); // Prefix for the log message
        return loggingFilter;
    }
}

