package org.example.ip_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.ip_backend.services.IpLogService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class UserController {

    private final IpLogService service;

    public UserController(IpLogService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String welcome() {
        return "Welcome to IP backend";
    }

    @PostMapping("/hit")
    public IpLogService.StatsResponse hit(HttpServletRequest request,
                                          @RequestHeader(value = "X-Client-Timestamp", required = false) Long clientTs,
                                          @RequestParam(value = "t", required = false) Long clientTsQuery) {

        String ip = extractClientIp(request);
        Long ts = (clientTs != null) ? clientTs : clientTsQuery;
        service.logHit(ip, ts);
        return service.getStats();
    }

    @GetMapping("/stats")
    public IpLogService.StatsResponse stats() {
        return service.getStats();
    }

    @GetMapping("/ip")
    public String clientIP(HttpServletRequest request) {
        return "user is at ip " + extractClientIp(request);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String first = xff.split(",")[0].trim();
            if (!first.isBlank()) return normalizeLocalhost(first);
        }
        return normalizeLocalhost(request.getRemoteAddr());
    }

    private String normalizeLocalhost(String ip) {
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) return "127.0.0.1";
        return ip;
    }
}
