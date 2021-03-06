package org.corespring;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.corespring.authentication.AccessTokenProvider;
import org.corespring.resource.Organization;
import org.corespring.resource.Question;
import org.corespring.resource.Quiz;
import org.corespring.resource.player.Mode;
import org.corespring.resource.player.Options;
import org.corespring.resource.player.OptionsResponse;
import org.corespring.resource.player.Role;
import org.corespring.resource.question.Participant;
import org.corespring.rest.CorespringRestException;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CorespringClientTest {

  private String clientId = "524c5cb5300401522ab21db1";
  private String clientSecret = "325hm11xiz7ykeen2ibt";

  private final Organization organization =
      new Organization("51114b307fc1eaa866444648", "Demo Organization", new ArrayList<String>(), false);

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8089);

  @Test
  public void testGetOrganizations() throws CorespringRestException {
    CorespringClient client = new CorespringClient(clientId, clientSecret);
    client.setEndpoint("http://localhost:8089");

    assertOrganizationsAreCorrect(client.getOrganizations());
  }

  private void assertOrganizationsAreCorrect(Collection<Organization> organizations) {
    assertEquals(1, organizations.size());
    Organization organization = organizations.iterator().next();

    assertEquals("Demo Organization", organization.getName());
    assertEquals("51114b307fc1eaa866444648", organization.getId());
  }

  @Test
  public void testGetQuizzes() throws CorespringRestException {

    CorespringClient client = new CorespringClient(clientId, clientSecret);
    client.setEndpoint("http://localhost:8089");

    Collection<Quiz> quizzes = client.getQuizzes(organization);
    for (Quiz quiz : quizzes) {
      checkQuiz(quiz);
    }
  }

  @Test
  public void testGetQuizById() throws CorespringRestException {
    CorespringClient client = new CorespringClient(clientId, clientSecret);
    client.setEndpoint("http://localhost:8089");

    Quiz quiz = client.getQuizById("000000000000000000000002");
    checkQuiz(quiz);
  }

  private void checkQuiz(Quiz quiz) {
    if (!quiz.getQuestions().isEmpty()) {
      Collection<Question> questions = quiz.getQuestions();
      for (Question question : questions) {
        assertNotNull(question.getTitle());
        assertNotNull(question.getItemId());
        assertNotNull(question.getSettings());
        assertNotNull(question.getStandards());
      }

      Collection<Participant> participants = quiz.getParticipants();
      for (Participant participant : participants) {
        assertNotNull(participant.getAnswers());
        assertNotNull(participant.getExternalUid());
      }
    }
  }

  @Test
  public void testCreateQuiz() throws CorespringRestException {
    Quiz quiz = new Quiz.Builder().title("My new quiz!").build();
    CorespringClient client = new CorespringClient(clientId, clientSecret);
    client.setEndpoint("http://localhost:8089");
    Quiz updatedQuiz = client.create(quiz);

    // Created quiz has id and orgId
    assertNotNull(updatedQuiz.getId());
    assertNotNull(updatedQuiz.getOrgId());
  }

  @Test
  public void testUpdateQuiz() throws CorespringRestException {
    Quiz quiz = new Quiz.Builder().title("My new quiz!").build();
    CorespringClient client = new CorespringClient(clientId, clientSecret);
    client.setEndpoint("http://localhost:8089");

    quiz = client.create(quiz);
    quiz = client.update(new Quiz.Builder(quiz).description("description").build());

    assertEquals("description", quiz.getDescription());
  }

  @Test
  public void testDeleteQuiz() throws CorespringRestException {
    Quiz quiz = new Quiz.Builder().title("My new quiz!").build();
    CorespringClient client = new CorespringClient(clientId, clientSecret);
    client.setEndpoint("http://localhost:8089");

    quiz = client.create(quiz);
    assertNotNull(quiz);
    quiz = client.delete(quiz);

    assertNull(quiz);
  }

  @Test
  public void testAuthTokenRetry() throws CorespringRestException {
    AccessTokenProvider mockAccessTokenProvider = mock(AccessTokenProvider.class);

    when(mockAccessTokenProvider.getAccessToken(anyString(), anyString(), anyString()))
        .thenReturn("bad_token").thenReturn("demo_token");

    CorespringClient client = new CorespringClient(clientId, clientSecret);
    client.setAccessTokenProvider(mockAccessTokenProvider);
    client.setEndpoint("http://localhost:8089");

    assertOrganizationsAreCorrect(client.getOrganizations());
  }

  @Test
  public void testEncryptOptions() throws CorespringRestException {
    Options options = new Options.Builder().itemId("*").sessionId("*").mode(Mode.ALL).role(Role.ALL)
        .expiresNever().build();

    CorespringClient client = new CorespringClient(clientId, clientSecret);
    client.setEndpoint("http://localhost:8089");


    OptionsResponse optionsResponse = client.encryptOptions(options);
    assertEquals("a369de25bf73cf3479dbcfc76f8c1b4ad983f777fe4834c33e3e57c98d86d31fe9e46bc606a8e3b61c5f2c1b935a6725e9e3cf227f558d3724895ef84ce43107645baf8f53dd068eafc9759b63b1ad44--b4cee74cb43af0d6b652bb044e9f464d",
        optionsResponse.getOptions()
    );

    assertEquals("525bf17a92699001e6e81e6d", optionsResponse.getClientId());
  }

}