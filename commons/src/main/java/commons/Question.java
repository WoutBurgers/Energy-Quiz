package commons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;


import java.util.*;

import static java.lang.Math.abs;


/**
 * This is the class that we will use for the question,
 * so there will be a list with the activities,
 * a number of activities assigned ot it and
 * the answer the player should give.
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Activity> usedActivities;

    private long answer;

    private String type;

    private String answers;

    @SuppressWarnings("unused")
    Question() {
    }

    /**
     * Constructor for the question
     *
     * @param answers a string containing all the different options
     * @param answer  the answer
     * @param type    the type
     */
    public Question(long answer, String type, String answers) {
        this.usedActivities = new ArrayList<>();
        this.answer = answer;
        this.type = type;
        this.answers = answers;
    }

    /**
     * Generates the score for a player based on the game's current question's starting time,
     * the time the player answered at, and how much time they had to answer.
     *
     * @param p               the player for whom the score is calculated
     * @param startOfQuestion the time the question was started
     * @param questionTime    the max time allowed for a player to answer
     * @return the score the player should get
     */
    public int achievedScore(Person p, Date startOfQuestion, int questionTime) {
        int scoreAchieved = (int) (300L + 700L -
                (p.getTimeAnswered().getTime() - startOfQuestion.getTime())
                        * (700f / (questionTime * 1000f)));
        if (p.getDoublePoints()) {
            scoreAchieved *= 2;
            p.setDoublePoints(false);
        }
        return scoreAchieved;
    }

    /**
     * This method returns to us a list with players
     * that choose the right answer.
     *
     * @param players
     * @return the players that need to be assigned points
     */
    public List<Person> whoAnsweredCorrectly(List<Person> players) {
        if (players == null)
            throw new IllegalArgumentException();
        List<Person> winners = new ArrayList<>();
        for (Person player : players) {
            if (player.getCurrentAnswer() == this.getAnswer()) {
                winners.add(player);
            } else {
                player.setDoublePoints(false);
            }
        }
        return winners;
    }

    /**
     * Updates the scores of the player's in winners by adding the points they should receive if
     * they answered correctly. winners are intended to be generated by whoAnsweredCorrectly, so
     * only players who answered correctly get points.
     *
     * @param winners         a list of players who answered the last question correctly
     * @param startOfQuestion the time the question was started
     * @param questionTime    the max time allowed for a player to answer.
     */
    public void updateScore(List<Person> winners, Date startOfQuestion, int questionTime) {
        for (Person p : winners) {
            p.updateScore(achievedScore(p, startOfQuestion, questionTime));
        }
    }

    /**
     * This method selects the correct answer from the activities ,so
     * we know to which player we need to give points
     */
    public void setCorrectAnswer() {
        long answer = 0;
        for (int i = 0; i < usedActivities.size(); i++)
            if (answer < this.usedActivities.get(i).getConsumption())
                answer = this.usedActivities.get(i).getConsumption();
        this.answer = answer;
    }
    /**
     * This method selects the correct answer from the activities for the closest to question,so
     * we know to which player we need to give points
     */
    public void setCorrectAnswerClosest() {
        long answer = 0;
        long possibility1 = Math.abs(this.usedActivities.get(0).getConsumption() -
                this.usedActivities.get(2).getConsumption());
        long possibility2 = Math.abs(this.usedActivities.get(1).getConsumption() -
                this.usedActivities.get(2).getConsumption());
        if (possibility1 < possibility2) {
            answer = this.usedActivities.get(0).getConsumption();
        }
        else {
            answer = this.usedActivities.get(1).getConsumption();
        }
        this.answer = answer;
    }

    public List<Activity> getUsedActivities() {
        return usedActivities;
    }

    public void setUsedActivities(List<Activity> usedActivities) {
        this.usedActivities = usedActivities;
    }

    public long getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String getType() {
        return type;
    }

    public String getAnswers() {
        return answers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Equals method for question
     *
     * @param o the object to compare
     * @return true if equals and false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return answer == question.answer && usedActivities.equals(question.usedActivities)
                && type.equals(question.getType());
    }

    @Override
    public String toString() {
        return "Question{" +
                "usedActivities=" + usedActivities +
                ", answer=" + answer +
                '}';
    }

    /**
     * Returns a string representing the question.
     * Formatted to be more easily readable for users.
     *
     * @return the string
     */
    public String pprint() {
        String result = type + ": \n";
        for (Activity a : usedActivities) {
            result += " - " + a.pprint();
            result += " " + (a.getConsumption() == getAnswer() ? "O" : "X") + "\n";
        }
        return result;
    }

    /**
     * This method assigns each player the percentage that they need to get from
     * the total number of points they can get from the open question.
     *
     * @param players
     * @return the players and the percentage of points that they need to be assigned
     */
    public HashMap<Person, Double> whoAnsweredCorrectlyOpen(List<Person> players) {
        HashMap<Person, Double> map = new HashMap<>();
        if (players == null)
            throw new IllegalArgumentException();
        for (Person player : players) {
            if (player.getCurrentAnswer() > this.getAnswer() * 2 || player.getCurrentAnswer() < 0) {
                player.setCurrentAnswer(0);
            }
            long fraction = abs(this.getAnswer() - player.getCurrentAnswer());
            if (fraction == 0)
                fraction = 1;
            double x = 1 - (double) fraction / this.getAnswer();
            x = Math.round(x * 100.0) / 100.0;
            if ((x * 100) % 10 >= 5) {
                x = ((double) ((int) ( x * 10)) + 1) / 10;
            }
            else
            {
                x = ((double) ((int) ( x * 10))) / 10;
            }
            map.put(player, x);
        }
        return map;
    }

    /**
     * Updates the scores of players based on the percentage of total points they
     * could get from the question
     *
     * @param map the players and the percentage of points they should get
     */
    public void updatePersonOpen(HashMap<Person, Double> map) {
        for (Person p : map.keySet()) {
            int score = (int) (1000 * map.get(p));
            if (p.getDoublePoints()) {
                score *= 2;
                p.setDoublePoints(false);
            }
            p.updateScore(score);
        }
    }


}