import org.junit.Test;

import java.io.File;
import java.net.URL;

public class TestMarkAndCompact extends TestWichExecution {
	public TestMarkAndCompact(File input, String baseName) {
		super(input, baseName);
	}

	@Test
	public void testCodeGen() throws Exception {
		testCodeGen(CompilerUtils.CodeGenTarget.MARK_AND_COMPACT);
	}

	@Test
	public void testExecution() throws Exception {
		URL expectedFile = CompilerUtils.getResourceFile(baseName + ".output");
		String expected = "";
		if (expectedFile != null) {
			expected = CompilerUtils.readFile(expectedFile.getPath(), CompilerUtils.FILE_ENCODING);
		}
		executeAndCheck(input.getAbsolutePath(), expected, false, CompilerUtils.CodeGenTarget.MARK_AND_COMPACT);
	}
}
