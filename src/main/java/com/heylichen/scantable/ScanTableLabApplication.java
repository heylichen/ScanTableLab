package com.heylichen.scantable;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.heylichen.scantable.dao.mapper")
@SpringBootApplication
public class ScanTableLabApplication {

  public static void main(String[] args) {
    SpringApplication.run(ScanTableLabApplication.class, args);
  }

}