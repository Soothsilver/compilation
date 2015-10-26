package compiler.tests;

import compiler.Compilation;
import compiler.generated.CompilerLexer;
import compiler.generated.CompilerParser;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

@RunWith(Parameterized.class)
public class NTests extends TestCase {



    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return TestUtilities.getFiles("semantics");
    }

    private String name;
    private File file;

    public NTests(final String name, final File file) {
        this.name = name;
        this.file = file;
    }

    @Test
    public void testSemantic() {
        TestUtilities.runTest(file, false, this);
    }


}
