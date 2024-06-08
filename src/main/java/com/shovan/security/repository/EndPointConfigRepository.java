package com.shovan.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shovan.security.entity.EndPointConfig;


public interface EndPointConfigRepository extends JpaRepository<EndPointConfig, Long> {

    List<EndPointConfig> findByPathAndMethod(String path, String method);
}
