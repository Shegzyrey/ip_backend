package org.example.ip_backend.repositories;

import org.example.ip_backend.entities.IpLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IpLogRepository extends JpaRepository<IpLog, Integer> {

    // total unique IPs
    @Query("select count(distinct i.ipAddress) from IpLog i")
    long countDistinctIpAddress();

    // last access (by timestamp)
    Optional<IpLog> findTopByOrderByTimestampDesc();
}