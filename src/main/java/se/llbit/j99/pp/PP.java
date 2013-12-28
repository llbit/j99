package se.llbit.j99.pp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;

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

public class PP {

	private final ArrayList<String[]> defines = new ArrayList<String[]>();

	public PP() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			define("_WIN32_", "_WIN32_");
		}
	}

	/**
	 * Add predefined macro
	 * @param name
	 * @param value
	 */
	public void define(String name, String value) {
		// TODO support other kinds of defines than pure single-identifier defines
		defines.add(new String[] {name, value});
	}

	/**
	 * @param fileName
	 * @param basePath
	 * @param in
	 * @param out
	 * @param errout
	 * @return {@code true} if there were no critical errors
	 * @throws IOException
	 */
	public final boolean preprocess(String fileName, String basePath, InputStream in,
			OutputStream out, OutputStream errout) throws IOException {
		ArrayList<CompileProblem> problems = new ArrayList<CompileProblem>();
		Fragmenter fragmenter = new CommentFragmenter(
				new LineSplicer(new TrigraphReplacer(
						new LineFragmenter(fileName, in))),
						problems);

		PPState state = new PPState();
		state.setProblemCollection(problems);
		state.setIncludeDirs(new LinkedList<String>());

		// predefined macros
		// TODO: these need to be attached to the root tree
		for (String[] macro: defines) {
			state.define(new Identifier(macro[0]),
					new ObjMacro(new Identifier(macro[1]),
					new List<Token>()));
		}

		Fragment processed = preprocess(fragmenter, state, fileName, basePath);

		in.close();

		CompileProblem.reportProblems(problems, new PrintStream(errout));
		if (!CompileProblem.isCritical(problems)) {

			Reader reader = new FragmentReader(processed);

			while (reader.ready()) {
				out.write((char)reader.read());
			}
			reader.close();
			out.flush();
			return true;
		}
		return false;
	}

	private final Fragment preprocess(Fragmenter in, PPState state, String fileName, String basePath) {
		Identifier name = new Identifier("__FILE__");
		StringLit filename = new StringLit("\""+fileName+"\"");
		name.setToken(new Symbol(new LineFragment("@j99", 0, 0, "__FILE__")));
		filename.setToken(new Symbol(new LineFragment("@j99", 0, 0, "\""+fileName+"\"")));
		// TODO: this needs to be attached to the root tree
		Macro fileMacro = new ObjMacro(name,
				new se.llbit.j99.pp.List<se.llbit.j99.pp.Token>().add(filename));
		state.define(name, fileMacro);

		DirectiveParser parser = new DirectiveParser(in, state);
		SourceFile root = parser.parse();
		PPVisitor visitor = new PPVisitor(state, basePath);
		root.accept(visitor);
		return new CompositeFragment(visitor.getResult());
	}
}
