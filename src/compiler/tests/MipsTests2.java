package compiler.tests;

import compiler.Compilation;
import compiler.intermediate.Executable;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MipsTests2 extends TestCase {



    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return TestUtilities.getFiles("mips-simple");
    }

    private File file;

    public MipsTests2(final String name, final File file) {
        this.file = file;
    }

    @Test
    public void testMips() {
        Compilation compilation = TestUtilities.runTest(file, false, this, true);
        if (compilation != null && !compilation.errorTriggered) {
            Executable executable = new Executable(compilation);
            String assemblerCode = executable.toMipsAssembler();
            String expectedOutput = compilation.firstLine != null ? compilation.firstLine.substring(2).trim() : "";
            try {
                java.nio.file.Files.write(Paths.get("test.asm"), assemblerCode.getBytes());
            } catch (IOException e) {
                fail("Due to an I/O exception, the assembler could not be saved to a file.");
            }
            Process mars = null;
            try {
                mars = Runtime.getRuntime().exec("java -jar ../lib/Mars4_5.jar 1000 nc test.asm");
            } catch (IOException e) {
                fail("Due to an I/O exception, the MARS emulator could not be launched.");
            }
            try {
                mars.waitFor();
            } catch (InterruptedException e) {
                fail("This will never happen.");
            }
            java.io.InputStream is = mars.getInputStream();
            byte b[]= new byte[0];
            String marsOutput = null;
            try {
                b = new byte[is.available()];
                is.read(b,0,b.length);
                marsOutput = new String(b);
            } catch (IOException e) {
               fail("MARS output could not be redirected.");
            }
            marsOutput = marsOutput.trim();
            assertEquals(expectedOutput, marsOutput);
        } else {
            fail("An error triggered.");
        }
    }


}
