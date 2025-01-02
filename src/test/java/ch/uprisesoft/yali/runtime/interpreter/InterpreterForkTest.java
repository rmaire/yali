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
package ch.uprisesoft.yali.runtime.interpreter;

import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.helper.ObjectMother;
import ch.uprisesoft.yali.repl.PrintingTracer;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import ch.uprisesoft.yali.scope.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author uprisesoft@gmail.com
 */
//@Disabled
public class InterpreterForkTest {

    private java.util.List<String> outputs;

    private UnthreadedInterpreter it;
    private UnthreadedInterpreter fork;

    private Tracer tracer;
    private Tracer tracer2;

    private OutputObserver oo;
    private InputGenerator ig;

    public InterpreterForkTest() {
    }

    @BeforeEach
    public void setUp() {

        outputs = new ArrayList<>();

        oo = output -> {
            System.out.println(output);
			outputs.add(output);
		};

        ig = new InputGenerator() {

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
        tracer = new PrintingTracer(it, "i");
        it.addTracer(tracer);

        fork = new ObjectMother().fork(it, oo, ig);
        tracer2 = new PrintingTracer(fork, "f");
        fork.addTracer(tracer2);
    }

    @Disabled
    @Test
    public void testRootScopesAteSame(){
        assertThat(it.env().first().equals(fork.env().first()), is(true));
    }

    @Disabled
    @Test
    public void testMakeWorks(){
        String input = "make \"test true";

        it.run(it.read(input));

        assertThat(fork.env().thingable("test"), is(true));
    }

    @Disabled
    @Test
    public void testToWorks(){
        String input = "to testit\n"
                + "print \"Hello\n"
                + "end\n";

        it.run(it.read(input));

        assertThat(fork.env().thingable("testit"), is(true));
    }

    @Disabled
    @Test
    public void testForkedInterpreter() {
        StringBuilder sb = new StringBuilder();
        sb.append("fd 60 rt 120 fd 60 rt 120 fd 60 rt 120 turtlepos").append("\n");
        Node res = fork.run(fork.read(sb.toString()));
        assertThat(res.toString(), is("[0 0]"));
    }

    @Disabled
    @Test
    public void testForkedEnv() {
        StringBuilder sb = new StringBuilder();
        sb.append("make \"first_programmer \"Ada_Lovelace").append("\n");
        it.run(it.read(sb.toString()));

        sb = new StringBuilder();
        sb.append("print :first_programmer").append("\n");
        Node res = fork.run(fork.read(sb.toString()));
        assertThat(res.toString(), is("[Ada_Lovelace]"));
    }

    @Disabled
    @Test
    public void testForkedProcedure1() {
        String input = "to TESTIT\n"
                + "print \"Hello\n"
                + "end\n";

        it.run(it.read(input));

        input = "TESTIT\n";

        fork.run(fork.read(input));

        assertThat(outputs.size(), is(1));
        assertThat(outputs.get(0), is("Hello\n"));
    }

    @Test
    public void testForkedProcedure2() {

        String input = "to testit :testparam\n"
                + "print :testparam\n"
                + "end\n"
                + "testit \"Hello1\n";

        it.run(it.read(input));

        System.out.println(fork.env().thing("testit").toString());
        System.out.println(fork.env().toString());

        input = "testit \"Hello2\n";

        Node prog = fork.read(input);
        Node res = fork.run(prog);

        System.out.println(res.toString());

        assertThat(outputs.size(), is(2));
        assertThat(outputs.get(0), is("Hello1\n"));
    }
}
