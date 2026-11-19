package com.footfix.service;

import com.footfix.dto.FootballDataSchedule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;

@Service
public class FootballDataScheduleService {
  private static final String MATCHES_URL = "https://api.football-data.org/v4/competitions/PL/matches";
  private static final Duration CACHE_DURATION = Duration.ofMinutes(5);

  private final RestTemplate restTemplate;
  private final String token;
  private FootballDataSchedule cachedSchedule;
  private Instant expiresAt = Instant.MIN;

  public FootballDataScheduleService(RestTemplate restTemplate, @Value("${FOOTBALL_DATA_TOKEN:}") String token) {
    this.restTemplate = restTemplate;
    this.token = token;
  }

  public synchronized FootballDataSchedule getSchedule() {
    if (Instant.now().isBefore(expiresAt)) {
      return cachedSchedule;
    }
    if (token.isBlank()) {
      throw new RestClientException("FOOTBALL_DATA_TOKEN is missing");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Auth-Token", token);
    FootballDataSchedule schedule = restTemplate.exchange(
        MATCHES_URL, HttpMethod.GET, new HttpEntity<>(headers), FootballDataSchedule.class).getBody();
    if (schedule == null) {
      throw new RestClientException("Empty response from football-data.org");
    }

    cachedSchedule = schedule;
    expiresAt = Instant.now().plus(CACHE_DURATION);
    return cachedSchedule;
  }
}
