package com.org.conceptlearning;

import com.org.conceptlearning.config.SwaggerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
@Slf4j
@Import(SwaggerConfig.class)
public class ZakatServerApplication {

    public static void main(String[] args) throws IOException, ParseException {
        SpringApplication.run(ZakatServerApplication.class, args);
    }

}
