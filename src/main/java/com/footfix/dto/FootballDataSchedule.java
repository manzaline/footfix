package com.footfix.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FootballDataSchedule(List<Match> matches) {
  public FootballDataSchedule {
    matches = List.copyOf(matches);
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Match(long id, Instant utcDate, String status, Team homeTeam, Team awayTeam, Score score) {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    public String getKickoff() {
      return utcDate == null ? "" : DATE.withZone(SEOUL).format(utcDate);
    }

    // The existing reminder endpoints interpret this string in the server's time zone.
    public String getReminderDate() {
      return utcDate == null ? "" : DATE.withZone(ZoneId.systemDefault()).format(utcDate);
    }

    public String getResult() {
      if (!"FINISHED".equals(status) && !"AWARDED".equals(status)) {
        return "";
      }
      FullTime goals = score == null ? null : score.fullTime();
      if (goals == null || goals.home() == null || goals.away() == null) {
        return "";
      }
      return goals.home() + " - " + goals.away();
    }

    public String getStatusLabel() {
      if (status == null) {
        return "";
      }
      return switch (status) {
        case "TIMED", "SCHEDULED" -> "예정";
        case "IN_PLAY" -> "진행 중";
        case "PAUSED" -> "휴식";
        case "FINISHED" -> "종료";
        case "AWARDED" -> "결과 확정";
        case "SUSPENDED" -> "중단";
        case "POSTPONED" -> "연기";
        case "CANCELLED" -> "취소";
        default -> status;
      };
    }

    public boolean isUpcoming() {
      return utcDate != null && ("SCHEDULED".equals(status) || "TIMED".equals(status));
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Team(String name, String shortName, String crest) {
    public String getDisplayName() {
      return shortName == null || shortName.isBlank() ? name : shortName;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Score(FullTime fullTime) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record FullTime(Integer home, Integer away) {}
}
