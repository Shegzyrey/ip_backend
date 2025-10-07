package org.example.ip_backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ip_log")
public class IpLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    public IpLog() {}

    public IpLog(Integer id, String ipAddress, long timestamp) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    public static IpLog of(String ip, long ts) {
        return new IpLog(null, ip, ts);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
