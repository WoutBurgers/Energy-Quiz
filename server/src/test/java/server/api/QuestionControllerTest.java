package server.api;
import commons.Activity;
import commons.Question;
import commons.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionControllerTest {

    private TestActivityRepository activityRepository;
    private TestQuestionRepository questionRepository;

    private QuestionController sut;
    private ArrayList<String> qRepoMethodCalls;
    private ArrayList<String> aRepoMethodCalls;
    private Question q;

    @BeforeEach
    void setUp() {
        activityRepository = new TestActivityRepository();
        questionRepository = new TestQuestionRepository();
        sut = new QuestionController(questionRepository, activityRepository);
        Activity activity1 = new Activity("testActivity1", 100, "test/path1");
        Activity activity2 = new Activity("testActivity2", 200, "test/path2");
        activityRepository.save(activity1);
        activityRepository.save(activity2);
        q = new Question(100, "OpenQuestion", "NoAnswers");
        ArrayList<Activity> activities = new ArrayList<>();
        activities.add(activity1);
        activities.add(activity2);
        q.setUsedActivities(activities);
        q.setCorrectAnswer();
        questionRepository.save(q);
        qRepoMethodCalls = new ArrayList<>();
        qRepoMethodCalls.add("save");
        aRepoMethodCalls = new ArrayList<>();
        aRepoMethodCalls.add("save");
        aRepoMethodCalls.add("save");
    }

    @Test
    public void get() {
        assertEquals(ResponseEntity.ok(q), sut.get());
        assertTrue(questionRepository.calledMethods.contains("findAll"));
    }

    @Test
    public void add() {
        Question q2 = new Question(2, "test", "test");
        assertEquals(ResponseEntity.ok(q2), sut.add(q2));
        assertTrue(questionRepository.calledMethods.contains("findAll"));
        assertTrue(questionRepository.calledMethods.contains("save"));
    }

    @Test
    public void addFalse() {
        assertEquals(ResponseEntity.badRequest().build(), sut.add(q));
        assertTrue(questionRepository.calledMethods.contains("findAll"));
    }

    @Test
    public void addNull() {
        assertEquals(ResponseEntity.badRequest().build(), sut.add(null));
        assertFalse(questionRepository.calledMethods.contains("findAll"));
    }

    @Test
    public void getById() {
        q.setId(4L);
        assertEquals(ResponseEntity.ok(q), sut.getById(4L));
        assertTrue(questionRepository.calledMethods.contains("existsById"));
        assertTrue(questionRepository.calledMethods.contains("getById"));
    }

    @Test
    public void getByIdFalse() {
        assertEquals(ResponseEntity.badRequest().build(), sut.getById(24L));
        assertTrue(questionRepository.calledMethods.contains("existsById"));
        assertFalse(questionRepository.calledMethods.contains("getById"));
    }

    @Test
    void deleteById() {
        assertEquals(sut.deleteById(0).getStatusCode(), HttpStatus.OK);
        qRepoMethodCalls.add("existsById");
        qRepoMethodCalls.add("getById");
        qRepoMethodCalls.add("delete");
        assertEquals(qRepoMethodCalls, questionRepository.calledMethods);
    }

    @Test
    void deleteByIdBadRequest() {
        assertEquals(sut.deleteById(1).getStatusCode(), HttpStatus.BAD_REQUEST);
        qRepoMethodCalls.add("existsById");
        assertEquals(qRepoMethodCalls, questionRepository.calledMethods);
    }

    @Test
    void generateQuestions() {
        assertEquals(HttpStatus.NO_CONTENT, sut.generateQuestions().getStatusCode());
        aRepoMethodCalls.add("findAll");
        assertEquals(aRepoMethodCalls, activityRepository.calledMethods);
        qRepoMethodCalls.add("deleteAll");
        assertEquals(qRepoMethodCalls, questionRepository.calledMethods);
        assertEquals(new ArrayList<>(), questionRepository.questions);
    }

    @Test
    void searchQuestions() {
        ResponseEntity<List<SearchResult>> res = sut.searchQuestions("test");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        qRepoMethodCalls.add("findAll");
        assertEquals(qRepoMethodCalls, questionRepository.calledMethods);
        List<SearchResult> searchResults = new ArrayList<>();
        searchResults.add(new SearchResult(0, "testActivity1", "OpenQuestion"));
        searchResults.add(new SearchResult(0, "testActivity2", "OpenQuestion"));
        assertEquals(searchResults, res.getBody());
    }

    @Test
    void searchQuestionsNoResults() {
        ResponseEntity<List<SearchResult>> res = sut.searchQuestions("absent term");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        qRepoMethodCalls.add("findAll");
        assertEquals(qRepoMethodCalls, questionRepository.calledMethods);
        List<SearchResult> searchResults = new ArrayList<>();
        assertEquals(searchResults, res.getBody());
    }

    @Test
    void getAll() {
        ResponseEntity<String> res = sut.getAll();
        assertEquals(HttpStatus.OK, res.getStatusCode());
        qRepoMethodCalls.add("findAll");
        assertEquals(qRepoMethodCalls, questionRepository.calledMethods);
        String questions = "OpenQuestion: \n" +
                " - testActivity1: 100wH X\n" +
                " - testActivity2: 200wH O\n\n";
        assertEquals(questions, res.getBody());
    }
}