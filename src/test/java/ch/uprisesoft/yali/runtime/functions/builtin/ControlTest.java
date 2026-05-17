/* 
 * Copyright 2020 Uprise Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.uprisesoft.yali.runtime.functions.builtin;

import ch.uprisesoft.yali.ast.node.List;
import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.ast.node.NodeType;
import ch.uprisesoft.yali.helper.ObjectMother;
import ch.uprisesoft.yali.runtime.interpreter.UnthreadedInterpreter;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class ControlTest {

    UnthreadedInterpreter it;
	private java.util.List<String> outputs;

    public ControlTest() {
    }

    @BeforeEach
    public void setUp() {
        outputs = new ArrayList<>();
		OutputObserver oo = output -> outputs.add(output);

		InputGenerator ig = new InputGenerator() {

			@Override
			public String request() {
				return "requestedinput";
			}

			@Override
			public String requestLine() {
				return "requestedinputline";
			}
		};

        it = new ObjectMother().getInterpreter(oo, ig);
    }

    @Test
    public void testMake() {
        it.run(it.read("make \"testit \"someval"));

        assertThat(it.env().thing("testit").type(), is(NodeType.QUOTE));
        assertThat(it.env().thing("testit").toQuotedWord().getQuote(), is("someval"));
    }

    @Test
    public void testReferenceAsVariableName() {
        String input = "make \"varone \"testvar\n"
                + "make :varone \"one\n";

        it.run(it.read(input));

        assertThat(it.env().thing("testvar").toQuotedWord().getQuote(), is("one"));
    }

    @Test
    public void testSymbolAsVariableName() {
        String input = "make varone \"test\n";

        it.run(it.read(input));

        assertThat(it.env().thingable("varone"), is(true));
        assertThat(it.env().thing("varone").toQuotedWord().getQuote(), is("test"));
    }

    @Test
    public void testTurtle() {
		String sb = "fd 100" + "\n" +
				"rt 90" + "\n" +
				"fd 100" + "\n" +
				"turtlepos" + "\n";
        Node res = it.run(it.read(sb));

        assertThat(res.type(), is(NodeType.LIST));
        assertThat(res.toList().getChildren().get(0).toIntegerWord().getInteger(), is(100));
        assertThat(res.toList().getChildren().get(1).toIntegerWord().getInteger(), is(100));
    }

    @Test
    public void testFunctionAndMake() {
		String sb = "make \"angle 90" + "\n" +
				"to move" + "\n" +
				"fd 100" + "\n" +
				"rt :angle" + "\n" +
				"fd 100" + "\n" +
				"end" + "\n" +
				"move" + "\n" +
				"turtlepos" + "\n";
        Node res = it.run(it.read(sb));

        assertThat(res.type(), is(NodeType.LIST));
        assertThat(res.toList().getChildren().get(0).toIntegerWord().getInteger(), is(100));
        assertThat(res.toList().getChildren().get(1).toIntegerWord().getInteger(), is(100));
    }

    @Test
    public void testFunctionWithParamAndMake() {
		String sb = "make \"angle 90" + "\n" +
				"to move :dist" + "\n" +
				"fd :dist" + "\n" +
				"rt :angle" + "\n" +
				"fd :dist" + "\n" +
				"end" + "\n" +
				"move 100" + "\n" +
				"turtlepos" + "\n";
        Node res = it.run(it.read(sb));

        assertThat(res.type(), is(NodeType.LIST));
        assertThat(res.toList().getChildren().get(0).toIntegerWord().getInteger(), is(100));
        assertThat(res.toList().getChildren().get(1).toIntegerWord().getInteger(), is(100));
    }

    @Test
    public void testRepeat() {
		String sb = "repeat 2 [fd 100 rt 90]" + "\n" +
				"turtlepos" + "\n";
        Node res = it.run(it.read(sb));

        assertThat(res.type(), is(NodeType.LIST));
        assertThat(res.toList().getChildren().get(0).toIntegerWord().getInteger(), is(100));
        assertThat(res.toList().getChildren().get(1).toIntegerWord().getInteger(), is(100));
    }

    @Test
    public void testRepeatAndMake() {
		String sb = "make \"count 2" + "\n" +
				"repeat :count [fd 100 rt 90]" + "\n" +
				"turtlepos" + "\n";
        Node res = it.run(it.read(sb));

        assertThat(res.type(), is(NodeType.LIST));
        assertThat(res.toList().getChildren().get(0).toIntegerWord().getInteger(), is(100));
        assertThat(res.toList().getChildren().get(1).toIntegerWord().getInteger(), is(100));
    }

    @Test
    public void testRepeatScope() {
		String sb = "make \"count 1" + "\n" +
				"repeat :count [make \"testvar \"yes]" + "\n";
		it.run(it.read(sb));

        assertThat(it.env().thing("testvar").type(), is(NodeType.QUOTE));
        assertThat(it.env().thing("testvar").toQuotedWord().getQuote(), is("yes"));
    }

    @Test
    public void testIfScope() {
		it.run(it.read("if true [make \"testvar \"yes]" + "\n"));

		assertThat(it.env().thing("testvar").type(), is(NodeType.QUOTE));
        assertThat(it.env().thing("testvar").toQuotedWord().getQuote(), is("yes"));
    }

    @Test
    public void testRun() {
        StringBuilder sb = new StringBuilder();
        sb.append("run [3 + 2]").append("\n");
        Node res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.INTEGER));
        assertThat(res.toIntegerWord().getInteger(), is(5));

        sb = new StringBuilder();
        sb.append("run [fd 100 rt 90 fd 100]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.LIST));
        List turtlepos = (List) res;
        assertThat(turtlepos.getChildren().size(), is(2));
        assertThat(turtlepos.getChildren().get(0).type(), is(NodeType.INTEGER));
        assertThat(turtlepos.getChildren().get(1).type(), is(NodeType.INTEGER));
        assertThat(turtlepos.getChildren().get(0).toIntegerWord().getInteger(), is(100));
        assertThat(turtlepos.getChildren().get(1).toIntegerWord().getInteger(), is(100));

    }

    @Test
    public void testRunScope() {
		String sb = "run [make \"testvar \"yes]" + "\n" +
				"print :testvar" + "\n";
        Node res = it.run(it.read(sb));

        assertThat(res.type(), is(NodeType.LIST));
//        assertThat(it.scope().defined("testvar"), is(true));
        assertThat(it.env().thing("testvar").type(), is(NodeType.QUOTE));
        assertThat(it.env().thing("testvar").toQuotedWord().getQuote(), is("yes"));
    }

    @Test
    public void testIfTrueGreater() {

		it.run(it.read("if 3 > 2 [print \"Yes]" + "\n"));

		assertThat(outputs.size(), is(1));
        assertThat(outputs.get(0), is("Yes\n"));
    }

    @Test
    public void testIfFalseLess() {
		Node res = it.run(it.read("if 3 < 2 [print \"Yes]" + "\n"));

        assertThat(res.type(), is(NodeType.NIL));
        assertThat(outputs.size(), is(0));
    }

    @Test
    public void testIfFalseLiteral() {
		Node res = it.run(it.read("if false [print \"Yes]" + "\n"));

        assertThat(res.type(), is(NodeType.NIL));
        assertThat(outputs.size(), is(0));
    }

    @Test
    public void testIfTrueLiteral() {
		it.run(it.read("if true [print \"Yes]" + "\n"));

		assertThat(outputs.size(), is(1));
        assertThat(outputs.get(0), is("Yes\n"));
    }

    @Test
    public void testIfTrueVarLiteral() {
		String sb = "make \"testvar true" + "\n" +
				"if :testvar [print \"Yes]" + "\n";
		it.run(it.read(sb));

		assertThat(outputs.size(), is(1));
        assertThat(outputs.get(0), is("Yes\n"));
    }

    @Test
    public void testIfFalseVarLiteral() {
		String sb = "make \"testvar false" + "\n" +
				"if :testvar [print \"Yes]" + "\n";
        Node res = it.run(it.read(sb));

        assertThat(res.type(), is(NodeType.NIL));
        assertThat(outputs.size(), is(0));
    }

    @Test
    public void testIfTrueVarEval() {
		String sb = "make \"testvar 3 > 2" + "\n" +
				"if :testvar [print \"Yes]" + "\n";
		it.run(it.read(sb));

		assertThat(outputs.size(), is(1));
        assertThat(outputs.get(0), is("Yes\n"));
    }

    @Test
    public void testIfFalseVarEval() {
		String sb = "make \"testvar 3 < 2" + "\n" +
				"if :testvar [print \"Yes]" + "\n";
        Node res = it.run(it.read(sb));

        assertThat(res.type(), is(NodeType.NIL));
        assertThat(outputs.size(), is(0));
    }

    @Test
    public void testIfElse() {
        StringBuilder sb;

        sb = new StringBuilder();
        sb.append("ifelse 3 > 2 [output Yes] [output No]").append("\n");
        Node res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.SYMBOL));
        assertThat(res.toSymbolWord().getSymbol(), is("Yes"));

        sb = new StringBuilder();
        sb.append("ifelse 3 < 2 [output Yes] [output No]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.SYMBOL));
        assertThat(res.toSymbolWord().getSymbol(), is("No"));

        sb = new StringBuilder();
        sb.append("ifelse false [output Yes] [output No]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.SYMBOL));
        assertThat(res.toSymbolWord().getSymbol(), is("No"));

        sb = new StringBuilder();
        sb.append("ifelse true [output Yes] [output No]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.SYMBOL));
        assertThat(res.toSymbolWord().getSymbol(), is("Yes"));

        sb = new StringBuilder();
        sb.append("make \"testvar true").append("\n");
        sb.append("ifelse :testvar [output Yes] [output No]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.SYMBOL));
        assertThat(res.toSymbolWord().getSymbol(), is("Yes"));

        sb = new StringBuilder();
        sb.append("make \"testvar false").append("\n");
        sb.append("ifelse :testvar [output Yes] [output No]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.SYMBOL));
        assertThat(res.toSymbolWord().getSymbol(), is("No"));

        sb = new StringBuilder();
        sb.append("make \"testvar 3 > 2").append("\n");
        sb.append("ifelse :testvar [output Yes] [output No]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.SYMBOL));
        assertThat(res.toSymbolWord().getSymbol(), is("Yes"));

        sb = new StringBuilder();
        sb.append("make \"testvar 3 < 2").append("\n");
        sb.append("ifelse :testvar [output Yes] [output No]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.SYMBOL));
        assertThat(res.toSymbolWord().getSymbol(), is("No"));

        sb = new StringBuilder();
        sb.append("make \"testvar 3 > 2").append("\n");
        sb.append("make \"yes \"Yes").append("\n");
        sb.append("make \"no \"No").append("\n");
        sb.append("ifelse :testvar [output :yes] [output :no]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.QUOTE));
        assertThat(res.toQuotedWord().getQuote(), is("Yes"));

        sb = new StringBuilder();
        sb.append("make \"testvar 3 < 2").append("\n");
        sb.append("make \"yes \"Yes").append("\n");
        sb.append("make \"no \"No").append("\n");
        sb.append("ifelse :testvar [output :yes] [output :no]").append("\n");
        res = it.run(it.read(sb.toString()));

        assertThat(res.type(), is(NodeType.QUOTE));
        assertThat(res.toQuotedWord().getQuote(), is("No"));
    }

    @Test
    public void testLocalMake() {

		String sb = "make \"testvar \"Bye!" + "\n" +
				"\n" +
				"to testfun" + "\n" +
				"local \"testvar" + "\n" +
				"make \"testvar \"Hello!" + "\n" +
				"print :testvar" + "\n" +
				"end" + "\n" +
				"\n" +
				"testfun" + "\n" +
				"\n" +
				"print :testvar" + "\n";

        it.run(it.read(sb));

        assertThat(it.env().thingable("testvar"), is(true));
        assertThat(it.env().thing("testvar").type(), is(NodeType.QUOTE));
        assertThat(it.env().thing("testvar").toQuotedWord().getQuote(), is("Bye!"));

        assertThat(outputs.size(), is(2));
        assertThat(outputs.get(0), is("Hello!\n"));
        assertThat(outputs.get(1), is("Bye!\n"));
    }

    @Test
    public void testThing() {

		String sb = "make \"testvar \"Hello!" + "\n" +
				"\n" +
				"print thing \"testvar" + "\n";

        it.run(it.read(sb));

//        assertThat(it.scope().defined("testvar"), is(true));
        assertThat(it.env().thing("testvar").type(), is(NodeType.QUOTE));
        assertThat(it.env().thing("testvar").toQuotedWord().getQuote(), is("Hello!"));

        assertThat(outputs.size(), is(1));
        assertThat(outputs.get(0), is("Hello!\n"));
    }

    @Test
    public void testPause1() {

		String sb = "to pausetest" + "\n" +
				"print \"two" + "\n" +
				"pause" + "\n" +
				"print \"three" + "\n" +
				"end" + "\n" +
				"\n" +
				"print \"one" + "\n" +
				"pause" + "\n" +
				"pausetest" + "\n";

        it.run(it.read(sb));
        it.resume();
        it.resume();

        assertThat(outputs.size(), is(3));
        assertThat(outputs.get(0), is("one\n"));
        assertThat(outputs.get(1), is("two\n"));
        assertThat(outputs.get(2), is("three\n"));
    }

    @Test
    public void testPause2() {

		String sb = "to pausetest" + "\n" +
				"print \"two" + "\n" +
				"pause" + "\n" +
				"print \"three" + "\n" +
				"end" + "\n" +
				"\n" +
				"print \"one" + "\n" +
				"pause" + "\n" +
				"pausetest" + "\n";

        it.run(it.read(sb));
        it.resume();

        assertThat(outputs.size(), is(2));
        assertThat(outputs.get(0), is("one\n"));
        assertThat(outputs.get(1), is("two\n"));
    }

    @Test
    //@Disabled
    public void testPause3() {

		String sb = "to pausetest" + "\n" +
				"if (1 > 0) [pause print \"two]" + "\n" +
				"end" + "\n" +
				"\n" +
				"print \"one" + "\n" +
				"pause" + "\n" +
				"pausetest" + "\n";

        it.run(it.read(sb));
        it.resume();
        it.resume();

        assertThat(outputs.size(), is(2));
        assertThat(outputs.get(0), is("one\n"));
        assertThat(outputs.get(1), is("two\n"));
    }

}
