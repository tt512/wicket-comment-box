package com.github.tatarhy;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

public class TutorialPageTest {
    private WicketTester tester;

    @Before
    public void setUp() {
        tester = new WicketTester(new WicketApplication());
    }

    @Test
    public void homepageRendersSuccessfully() {
        tester.startPage(TutorialPage.class);

        tester.assertRenderedPage(TutorialPage.class);
    }
}
