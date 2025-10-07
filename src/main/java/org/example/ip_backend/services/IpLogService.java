package org.example.ip_backend.services;

import org.example.ip_backend.entities.IpLog;
import org.example.ip_backend.repositories.IpLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IpLogService {

    private final IpLogRepository repo;

    public IpLogService(IpLogRepository repo) {
        this.repo = repo;
    }

    public IpLog logHit(String ip, Long clientEpochSeconds) {
        long ts = (clientEpochSeconds != null) ? clientEpochSeconds : Instant.now().getEpochSecond();
        IpLog log = IpLog.of(ip, ts);
        return repo.save(log);
    }

    public StatsResponse getStats() {
        long totalHits = repo.count();
        long uniqueIps = repo.countDistinctIpAddress();
        long last = repo.findTopByOrderByTimestampDesc()
                .map(IpLog::getTimestamp)
                .orElse(0L);
        return new StatsResponse(uniqueIps, totalHits, last);
    }

    public static class StatsResponse {
        private long totalUniqueIps;
        private long totalHits;
        private long lastAccessEpochSeconds;

//        public StatsResponse() {}
        public StatsResponse(long totalUniqueIps, long totalHits, long lastAccessEpochSeconds) {
            this.totalUniqueIps = totalUniqueIps;
            this.totalHits = totalHits;
            this.lastAccessEpochSeconds = lastAccessEpochSeconds;
        }

//        public long getTotalUniqueIps() { return totalUniqueIps; }
//        public long getTotalHits() { return totalHits; }
//        public long getLastAccessEpochSeconds() { return lastAccessEpochSeconds; }
//        public void setTotalUniqueIps(long v) { this.totalUniqueIps = v; }
//        public void setTotalHits(long v) { this.totalHits = v; }
//        public void setLastAccessEpochSeconds(long v) { this.lastAccessEpochSeconds = v; }
    }
}
