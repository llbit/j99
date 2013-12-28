package se.llbit.j99.pp;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import se.llbit.j99.fragment.CommentFragmenter;
import se.llbit.j99.fragment.CompositeFragment;
import se.llbit.j99.fragment.Fragment;
import se.llbit.j99.fragment.FragmentReader;
import se.llbit.j99.fragment.Fragmenter;
import se.llbit.j99.fragment.LineFragment;
import se.llbit.j99.fragment.LineFragmenter;
import se.llbit.j99.fragment.LineSplicer;
import se.llbit.j99.fragment.TrigraphReplacer;
import se.llbit.j99.parser.Symbol;
import se.llbit.j99.problem.CompileProblem;
import se.llbit.j99.util.DirectiveParser;

@RunWith(Parameterized.class)
public class TestPP {

	private static String SYS_LINE_SEP = System.getProperty("line.separator");
	private static final String TEST_ROOT = "tests/";

	private static void addTestDir(Collection<Object[]> tests, File dir) {

		if (dir.isDirectory()) {
			String path = dir.getPath().replace(File.separatorChar, '/');
			if (path.startsWith(TEST_ROOT)) {
				path = path.substring(TEST_ROOT.length());
			}
			File testFile = new File(dir, "test.c");
			File resultFile = new File(dir, "out.expected");
			if (testFile.isFile() || resultFile.isFile()) {
				tests.add(new Object[] { path });
			} else {
				for (File child: dir.listFiles()) {
					addTestDir(tests, child);
				}
			}
		}
	}

	@Parameters(name="{0}")
	public static Iterable<Object[]> testCases() {
		java.util.List<Object[]> tests = new LinkedList<Object[]>();
		addTestDir(tests, new File(TEST_ROOT));

		// sort the tests lexicographically
		Collections.sort(tests, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] a, Object[] b) {
				return a[0].toString().compareTo(b[0].toString());
			}
		});
		return tests;
	}

	private final String test;

	public TestPP(String test) {
		this.test = test;
	}

	@Test
	public void test() throws IOException {
		String stdout;
		String stderr;

		String testCase = TEST_ROOT + test + "/test.c";
		String basePath = "";
		InputStream in = new FileInputStream(testCase);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		ArrayList<CompileProblem> problems = new ArrayList<CompileProblem>();
		Fragmenter fragmenter = new CommentFragmenter(new LineSplicer(new TrigraphReplacer(
						new LineFragmenter(testCase, in))), problems);

		PPState state = new PPState();
		state.setProblemCollection(problems);
		state.setIncludeDirs(new LinkedList<String>());
		// predefined macros
		// TODO: these need to be attached to the root tree
		state.define(new Identifier("_WIN32_"),
				new ObjMacro(new Identifier("_WIN32_"),
				new se.llbit.j99.pp.List<se.llbit.j99.pp.Token>()));

		Fragment processed = preprocess(fragmenter, state, testCase, basePath);

		in.close();

		ByteArrayOutputStream errout = new ByteArrayOutputStream();
		CompileProblem.reportProblems(problems, new PrintStream(errout));
		stderr = new String(errout.toByteArray());
		if (!CompileProblem.isCritical(problems)) {

			Reader reader = new FragmentReader(processed);

			while (reader.ready()) {
				out.write((char)reader.read());
			}
			reader.close();
			out.close();
			stdout = new String(out.toByteArray());
		} else {
			stdout = "";
		}

		String expectedOut = readFileToString(new File(TEST_ROOT + test, "out.expected"));
		String expectedErr = readFileToString(new File(TEST_ROOT + test, "err.expected"));
		assertEquals("output mismatch", expectedOut, stdout);
		assertEquals("error message mismatch", expectedErr.trim(), stderr.trim());
	}

	/**
	 * <p>Reads an entire file to a string object.
	 *
	 * <p>If the file does not exist an empty string is returned.
	 *
	 * <p>The system dependent line separator char sequence is replaced by
	 * the newline character.
	 *
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	private static String readFileToString(File file) throws FileNotFoundException {
		if (!file.isFile() || !file.canRead()) {
			return "";
		}

		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("\\A");
		String theString = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		theString = theString.replace(SYS_LINE_SEP, "\n");
		return theString;
	}

	private static Fragment preprocess(Fragmenter in, PPState state, String fn, String basePath) {
		Identifier name = new Identifier("__FILE__");
		StringLit filename = new StringLit("\""+fn+"\"");
		name.setToken(new Symbol(new LineFragment("@j99", 0, 0, "__FILE__")));
		filename.setToken(new Symbol(new LineFragment("@j99", 0, 0, "\""+fn+"\"")));
		Macro fileMacro = new ObjMacro(name,
				new se.llbit.j99.pp.List<se.llbit.j99.pp.Token>().add(filename));// TODO: this needs to be attached to the root tree
		state.define(name, fileMacro);

		DirectiveParser parser = new DirectiveParser(in, state);
		SourceFile root = parser.parse();
		PPVisitor visitor = new PPVisitor(state, basePath);
		root.accept(visitor);
		return new CompositeFragment(visitor.getResult());
	}

}