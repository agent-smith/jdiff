package com.jdiff;

import junit.framework.TestCase;

/**
 * Unit tests for Diff.
 */
public class DiffTest extends TestCase
{

    public void testBothEmpty()
    {
        String[] x = new String[] {
        };
        String[] y = new String[] {
        };

        Diff lcsDiff = new Diff(x, y);
        String actualDiff = lcsDiff.runDiff();

        String expectedDiff = ""; // they are the same!

        assertEquals(expectedDiff, actualDiff);
    }

    public void testBothSame()
    {
        String[] x = new String[] {
                "This is first line",
                "lines",
                "",
                "And this is the last line."
        };
        String[] y = new String[] {
                "This is first line",
                "lines",
                "",
                "And this is the last line."
        };

        Diff lcsDiff = new Diff(x, y);
        String actualDiff = lcsDiff.runDiff();

        String expectedDiff = ""; // they are the same!

        assertEquals(expectedDiff, actualDiff);
    }

    public void testAllAdds()
    {
        String[] x = new String[] {
        };
        String[] y = new String[] {
                "This is first line",
                "lines",
                "",
                "And this is the last line."
        };

        Diff lcsDiff = new Diff(x, y);
        String actualDiff = lcsDiff.runDiff();

        String expectedDiff =
                "0a1,4\n"+
                        "> This is first line\n"+
                        "> lines\n"+
                        "> \n"+
                        "> And this is the last line.";

        assertEquals(expectedDiff, actualDiff);
    }

    public void testAllRemoves()
    {
        String[] x = new String[] {
                "This is first line",
                "lines",
                "",
                "And this is the last line."
        };
        String[] y = new String[] {
        };

        Diff lcsDiff = new Diff(x, y);
        String actualDiff = lcsDiff.runDiff();

        String expectedDiff =
                "1,4d0\n"+
                        "< This is first line\n"+
                        "< lines\n"+
                        "< \n"+
                        "< And this is the last line.";

        assertEquals(expectedDiff, actualDiff);
    }

    public void testDiff()
    {
        String[] x = new String[] {
                "This is first line",
                "And then here is a paragraph",
                "of text that is being written",
                "over several",
                "lines",
                "",
                "",
                "And this is the last line."
        };
        String[] y = new String[] {
                "This is new first line",
                "And then here is a paragraph",
                "of text that is being written",
                "over several",
                "several",
                "and even more several",
                "lines",
                "",
                "And this is the last line.",
                "No actually this one is.",
                ""
        };

        Diff lcsDiff = new Diff(x, y);
        String actualDiff = lcsDiff.runDiff();

        String expectedDiff =
                "1c1\n"+
                        "< This is first line\n"+
                        "---\n"+
                        "> This is new first line\n"+
                        "4a5,6\n"+
                        "> several\n"+
                        "> and even more several\n"+
                        "7d8\n"+
                        "< \n"+
                        "8a10,11\n"+
                        "> No actually this one is.\n"+
                        "> ";

        assertEquals(expectedDiff, actualDiff);
    }

    public void testLongerDiff()
    {

        String[] x = new String[] {
                "This part of the",
                "document has stayed the",
                "same from version to",
                "version.  It shouldn't",
                "be shown if it doesn't",
                "change.  Otherwise, that",
                "would not be helping to",
                "compress the size of the",
                "changes.",
                "",
                "This paragraph contains",
                "text that is outdated.",
                "It will be deleted in the",
                "near future.",
                "",
                "It is important to spell",
                "check this dokument. On",
                "the other hand, a",
                "misspelled word isn't",
                "the end of the world.",
                "Nothing in the rest of",
                "this paragraph needs to",
                "be changed. Things can",
                "be added after it."
        };
        String[] y = new String[] {
                "This is an important",
                "notice! It should",
                "therefore be located at",
                "the beginning of this",
                "document!",
                "",
                "This part of the",
                "document has stayed the",
                "same from version to",
                "version.  It shouldn't",
                "be shown if it doesn't",
                "change.  Otherwise, that",
                "would not be helping to",
                "compress anything.",
                "",
                "It is important to spell",
                "check this document. On",
                "the other hand, a",
                "misspelled word isn't",
                "the end of the world.",
                "Nothing in the rest of",
                "this paragraph needs to",
                "be changed. Things can",
                "be added after it.",
                "",
                "This paragraph contains",
                "important new additions",
                "to this document."
        };

        Diff lcsDiff = new Diff(x, y);
        String actualDiff = lcsDiff.runDiff();

        String expectedDiff =
                "0a1,6\n"+
                        "> This is an important\n"+
                        "> notice! It should\n"+
                        "> therefore be located at\n"+
                        "> the beginning of this\n"+
                        "> document!\n"+
                        "> \n"+
                        "8,14c14\n"+
                        "< compress the size of the\n"+
                        "< changes.\n"+
                        "< \n"+
                        "< This paragraph contains\n"+
                        "< text that is outdated.\n"+
                        "< It will be deleted in the\n"+
                        "< near future.\n"+
                        "---\n"+
                        "> compress anything.\n"+
                        "17c17\n"+
                        "< check this dokument. On\n"+
                        "---\n"+
                        "> check this document. On\n"+
                        "24a25,28\n"+
                        "> \n"+
                        "> This paragraph contains\n"+
                        "> important new additions\n"+
                        "> to this document.";

        assertEquals(expectedDiff, actualDiff);
    }
}
