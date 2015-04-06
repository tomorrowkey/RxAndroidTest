package jp.tomorrowkey.android.rxandroidtest.matcher;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

import java.util.regex.Pattern;

public class RegexMatcher extends TypeSafeMatcher<String> {

    public static TypeSafeMatcher<String> matches(String criteria) {
        return new RegexMatcher(criteria);
    }

    private String criteria;

    private Pattern pattern;

    private String actual;

    public RegexMatcher(String criteria) {
        this.criteria = criteria;
        this.pattern = Pattern.compile(criteria);
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(actual).appendText(" is not matched with ").appendValue(criteria);
    }

    @Override
    public boolean matchesSafely(String actual) {
        this.actual = actual;
        return null != actual && null != criteria
                && pattern.matcher(actual).matches();
    }

}
