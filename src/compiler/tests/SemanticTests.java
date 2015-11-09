package compiler.tests;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SemanticTests extends TestCase {



    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return TestUtilities.getFiles("semantics");
    }

    private File file;

    public SemanticTests(final String name, final File file) {
        this.file = file;
    }

    @Test
    public void testSemantic() {
        TestUtilities.runTest(file, false, this);
    }


}
