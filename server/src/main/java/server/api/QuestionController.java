package server.api;

import commons.Activity;
import commons.Question;
import commons.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.QuestionGenerator;
import server.database.ActivityRepository;
import server.database.QuestionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionRepository repository;
    private final ActivityRepository activityRepository;

    /**
     * Constructor for the question controller
     * @param questionRepository the questions repository
     * @param activityRepository the activity repository
     */
    @Autowired
    public QuestionController(QuestionRepository questionRepository,
                              ActivityRepository activityRepository) {
        this.repository = questionRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Method to get a random question
     *
     * @return a response containing the activity
     */
    @GetMapping("/question/get")
    public ResponseEntity<Question> get() {
        List<Question> questions = repository.findAll();
        int x = (int) (Math.random() * questions.size());
        return ResponseEntity.ok(questions.get(x));
    }

    /**
     * Method to post a question, it first look if the question
     * is null, after this we look if there is already a question
     * like that in the repo if it is not then we save the
     * question to the repo.
     *
     * @param question the question to add
     * @return a response containing the question
     */
    @PostMapping("/question/add")
    public ResponseEntity<Question> add(@RequestBody Question question) {
        if (question == null)
            return ResponseEntity.badRequest().build();

        List<Question> questions = repository.findAll();
        for (Question q : questions) {
            if (q == question)
                return ResponseEntity.badRequest().build();
        }

        repository.save(question);

        return ResponseEntity.ok(question);
    }

    /**
     * get a question by ID
     *
     * @param id the corresponding ID
     * @return The question with the ID
     */
    @GetMapping("/question/{id}")
    public ResponseEntity<Question> getById(@PathVariable("id") long id) {
        if (id < 0 || !repository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        //System.out.println(repository.getById(id));
        return ResponseEntity.ok(repository.getById(id));
    }

    /**
     * Delete a question by its ID.
     *
     * @param id the corresponding ID
     * @return the question with the ID
     */
    @DeleteMapping("/question/delete/{id}")
    public ResponseEntity<Question> deleteById(@PathVariable("id") long id) {
        if (id < 0 || !repository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Question q = repository.getById(id);
        repository.delete(q);
        ResponseEntity<Question> res = ResponseEntity.ok(q);
        repository.deleteById(id);
        return res;
    }

    /**
     * Delete all the questions in the repository and generate new ones.
     *
     * @return an empty response.
     */
    @GetMapping("/question/generate")
    public ResponseEntity generateQuestions() {
        System.out.println("Generating a new set of questions.");
        repository.deleteAll();
        QuestionGenerator generator = new QuestionGenerator(activityRepository, repository);
        generator.generator();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Search the question database for questions which have an activity with the search term in
     * their title.
     * Sends a bad request if the amount of results is too high.
     *
     * @param term the search term to look for
     * @return a list of all the questions that fit the search
     */
    @GetMapping("/question/search")
    public ResponseEntity<List<SearchResult>> searchQuestions(@RequestParam String term) {
        Stream<Question> s = repository.findAll().stream();
        // Filter for questions that have an activity whose name contains the search term.
        s = s.filter(x -> {
            // This predicate filters the used activities for those whose name contains the search
            // term, then checks if the resulting stream is empty.
            // Essentially, it checks if any of the activities have the search term in their title.
            return x.getUsedActivities().stream().anyMatch(y -> {
                return y.getTitle().contains(term);
            });
        });
        List<Question> questionList = s.collect(Collectors.toList());
        List<SearchResult> searchResults = new ArrayList<>();
        for (Question q : questionList) {
            for (Activity a : q.getUsedActivities()) {
                if (a.getTitle().contains(term)) {
                    searchResults.add(new SearchResult(q.getId(), a.getTitle(), q.getType()));
                }
            }
        }
        return ResponseEntity.ok(searchResults);
    }

    /**
     * Method to get all questions
     *
     * @return a response containing all questions.
     */
    @GetMapping("/question/getAll")
    public ResponseEntity<String> getAll() {
        List<Question> questions = repository.findAll();
        return ResponseEntity.ok(formatQuestionList(questions));
    }

    /**
     * Formats a list of questions into one string for display.
     * @param questions the list of questions
     * @return the string
     */
    public static String formatQuestionList(List<Question> questions) {
        String result = "";
        for (Question q : questions) {
            result += q.pprint() + "\n";
        }
        return result;
    }

}
