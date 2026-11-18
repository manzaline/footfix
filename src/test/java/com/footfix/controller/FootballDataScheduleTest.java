package com.footfix.controller;

import com.footfix.config.FootfixConfig;
import com.footfix.domain.User;
import com.footfix.dto.FootballDataSchedule.FullTime;
import com.footfix.dto.FootballDataSchedule.Match;
import com.footfix.dto.FootballDataSchedule.Score;
import com.footfix.security.config.auth.PrincipalDetails;
import com.footfix.service.FootballDataScheduleService;
import com.footfix.service.FootfixService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FootballDataScheduleTest {
  private static final String MATCHES = "https://api.football-data.org/v4/competitions/PL/matches";
  private static final String SEASON = """
      {"matches":[
        {"id":101,"utcDate":"2026-11-01T16:00:00Z","status":"FINISHED",
         "homeTeam":{"name":"West FC","shortName":"West","crest":"https://crests.football-data.org/1.svg"},
         "awayTeam":{"name":"East FC","crest":"https://crests.football-data.org/2.png"},
         "score":{"fullTime":{"home":0,"away":0}}},
        {"id":102,"utcDate":"2027-02-01T20:00:00Z","status":"TIMED",
         "homeTeam":{"name":"East FC","crest":"https://crests.football-data.org/2.png"},
         "awayTeam":{"name":"West FC","crest":"https://crests.football-data.org/1.svg"},
         "score":{"fullTime":{"home":null,"away":null}}}
      ]}
      """;

  private MockRestServiceServer provider;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    RestTemplate http = new FootfixConfig().restTemplate();
    provider = MockRestServiceServer.bindTo(http).build();
    FootfixController controller = new FootfixController(mock(FootfixService.class),
        new FootballDataScheduleService(http, "test-token"));

    SpringResourceTemplateResolver resources = new SpringResourceTemplateResolver();
    resources.setApplicationContext(new StaticApplicationContext());
    resources.setPrefix("classpath:/templates/");
    resources.setSuffix(".html");
    resources.setCharacterEncoding("UTF-8");
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setTemplateResolver(resources);
    ThymeleafViewResolver views = new ThymeleafViewResolver();
    views.setTemplateEngine(engine);
    views.setCharacterEncoding("UTF-8");
    mvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(views).build();

    User user = new User();
    user.setId(1L);
    user.setUsername("tester");
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(new PrincipalDetails(user), null));
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private String renderPage() throws Exception {
    return mvc.perform(get("/footfix/listAll")
            .requestAttr("_csrf", new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "test-csrf")))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
  }

  @Test
  void oneProviderRequestRendersBothTeamsAndResultsAndIsReusedOnReload() throws Exception {
    provider.expect(requestTo(MATCHES)).andExpect(method(HttpMethod.GET))
        .andExpect(header("X-Auth-Token", "test-token")).andRespond(withSuccess(SEASON, MediaType.APPLICATION_JSON));

    String html = renderPage();
    assertThat(html).contains("https://crests.football-data.org/1.svg", "https://crests.football-data.org/2.png",
        ">West</td>", ">East FC</td>", "2026-11-02 01:00", "data-match-id=\"fd-102\"");
    assertThat(html).containsOnlyOnce("class=\"match-result text-center\">0 - 0</td>")
        .containsOnlyOnce("class=\"match-result text-center\"></td>")
        .contains("id=\"reminder-102\"").doesNotContain("id=\"reminder-101\"", "test-token");
    assertThat(renderPage()).contains("0 - 0");
    provider.verify();
  }

  @ParameterizedTest
  @ValueSource(strings = {"TIMED", "SCHEDULED", "IN_PLAY", "PAUSED", "POSTPONED", "SUSPENDED", "CANCELLED"})
  void nonFinalScoresStayBlankEvenIfTheProviderSuppliesGoals(String status) {
    assertThat(new Match(1, null, status, null, null, new Score(new FullTime(2, 1))).getResult()).isEmpty();
  }

  @Test
  void distinguishesMissingGoalsFromZero() {
    assertThat(new Match(1, null, "FINISHED", null, null, new Score(new FullTime(2, null))).getResult()).isEmpty();
    assertThat(new Match(2, null, "FINISHED", null, null, null).getResult()).isEmpty();
    assertThat(new Match(3, null, "AWARDED", null, null, new Score(new FullTime(3, 0))).getResult()).isEqualTo("3 - 0");
  }

  @Test
  void anUpstreamErrorCanRecoverAndIsDistinctFromAnEmptySeason() throws Exception {
    provider.expect(requestTo(MATCHES)).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
    provider.expect(requestTo(MATCHES)).andRespond(withSuccess("{\"matches\":[]}", MediaType.APPLICATION_JSON));
    assertThat(renderPage()).contains("경기 일정을 불러오지 못했습니다").doesNotContain("표시할 경기 일정이 없습니다");
    assertThat(renderPage()).contains("표시할 경기 일정이 없습니다").doesNotContain("경기 일정을 불러오지 못했습니다");
    provider.verify();
  }
}
