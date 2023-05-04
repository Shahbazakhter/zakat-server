package com.org.conceptlearning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
@Slf4j
public class ZakatServerApplication {

    public static void main(String[] args) throws IOException, ParseException {
        SpringApplication.run(ZakatServerApplication.class, args);
    }

}
