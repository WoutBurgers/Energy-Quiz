package commons;

public class SearchResult {
    private long questionId;
    private String activityTitle;
    private String questionType;

    /**
     * A simple class to allow searches to both show more than one relevant activity in a question,
     * and also to include information about the question.
     *
     * @param questionId the id of the question the activity belongs to
     * @param activityTitle the title of the activity
     * @param questionType the type of question the activity is in
     */
    public SearchResult(long questionId, String activityTitle, String questionType) {
        this.questionId = questionId;
        this.activityTitle = activityTitle;
        this.questionType = questionType;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResult that = (SearchResult) o;

        if (questionId != that.questionId) return false;
        if (!activityTitle.equals(that.activityTitle)) return false;
        return questionType.equals(that.questionType);
    }
}
