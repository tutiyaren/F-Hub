package jp.fhub.fhub_feeling.service;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class JwtBlacklistService {
    // 本番環境では、インメモリキャッシュで実装(Redis)
    private Set<String> blacklist = new HashSet<>();
    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
