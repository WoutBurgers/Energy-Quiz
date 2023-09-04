package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchResultTest {

    private SearchResult sut;

    @BeforeEach
    void setUp() {
        sut = new SearchResult(0, "Activity Title", "Test Type");
    }

    void constructorTest() {
        assertNotNull(sut);
    }

    @Test
    void getQuestionId() {
        assertEquals(0, sut.getQuestionId());
    }

    @Test
    void setQuestionId() {
        sut.setQuestionId(1);
        assertEquals(1, sut.getQuestionId());
    }

    @Test
    void getActivityTitle() {
        assertEquals("Activity Title", sut.getActivityTitle());
    }

    @Test
    void setActivityTitle() {
        sut.setActivityTitle("Test Activity 2");
        assertEquals("Test Activity 2", sut.getActivityTitle());
    }

    @Test
    void getQuestionType() {
        assertEquals("Test Type", sut.getQuestionType());
    }

    @Test
    void setQuestionType() {
        sut.setQuestionType("Test Type 2");
        assertEquals("Test Type 2", sut.getQuestionType());

    }

    @Test
    void testEqualsSame() {
        assertTrue(sut.equals(sut));
    }

    @Test
    void testNotEqualNull() {
        assertFalse(sut.equals(null));
    }

    @Test
    void testNotEqualDifferentClass() {
        assertFalse(sut.equals(new Object()));
    }

    @Test
    void testNotEqualsDifferentId() {
        SearchResult other = new SearchResult(1, "Activity Title", "Test Type");
        assertFalse(sut.equals(other));
    }

    @Test
    void testNotEqualsDifferentActivity() {
        SearchResult other = new SearchResult(0, "Activity Title 2", "Test Type");
        assertFalse(sut.equals(other));
    }

    @Test
    void testNotEqualsDifferentType() {
        SearchResult other = new SearchResult(0, "Activity Title", "Test Type 2");
        assertFalse(sut.equals(other));
    }

    @Test
    void testEquals() {
        SearchResult other = new SearchResult(0, "Activity Title", "Test Type");
        assertTrue(sut.equals(other));
    }
}