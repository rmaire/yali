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
import ch.uprisesoft.yali.ast.node.NodeType;
import ch.uprisesoft.yali.exception.NodeTypeException;
import ch.uprisesoft.yali.helper.ObjectMother;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import org.junit.jupiter.api.BeforeEach;
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
public class ThreadedInterpreterTest {

    private java.util.List<String> outputs;

    private ThreadedInterpreter it;

    private OutputObserver oo;
    private InputGenerator ig;

    public ThreadedInterpreterTest() {
    }

    @BeforeEach
    public void setUp() {

        outputs = new ArrayList<>();

        oo = new OutputObserver() {

            @Override
            public void inform(String output) {
//                System.out.println(output);
                outputs.add(output);
            }
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



        ObjectMother om = new ObjectMother(oo, ig);
        it = new ThreadedInterpreter(om.getInterpreter());
        it.loadStdLib(oo, ig);
//        it = om.getInterpreter();
    }

    @Test
    public void testNestedFunDefAndCall() throws InterruptedException {
        String input = "to testfun :in\n"
                + "testfun2\n"
                + "end\n"
                + "\n"
                + "to testfun2\n"
                + "print \"testit!!!\n"
                + "end\n"
                + "\n"
                + "testfun";
        it.run(it.read(input));

        while(!it.finished()) {Thread.sleep(10);}

        assertThat(outputs.get(0), is("testit!!!\n"));
    }

    @Test
    public void testRecursion() throws InterruptedException {
        String input = "to recurse :i\n"
                + "print :i\n"
                + "if (:i > 0) [recurse :i - 1]\n"
                + "end\n"
                + "\n"
                + "recurse 8000\n";

        it.run(it.read(input));

        while(!it.finished()) {Thread.sleep(10);}

        assertThat(outputs.size(), is(8001));

        for (int i = 8000; i >= 0; i--) {
            assertThat(outputs.get(8000 - i), is(i + "\n"));
        }
    }

    @Test
    public void testNestedRunList() throws InterruptedException {
        String input = "to testit :i\n"
                + "if (:i < 10) [print \"first if (:i > 5) [print \"yes]]\n"
                + "end\n"
                + "\n"
                + "testit 6\n";

        Node res = it.run(it.read(input));

        while(!it.finished()) {Thread.sleep(10);}

        assertThat(outputs.size(), is(2));
        assertThat(outputs.get(0), is("first\n"));
        assertThat(outputs.get(1), is("yes\n"));
    }

//    @Test
//    public void testReset() throws InterruptedException {
//        String badInput = "Blabla\n";
//
//        try {
//            Node res = it.run(it.read(badInput));
//        } catch (NodeTypeException nte) {
//            assertThat(nte.getExpected().get(0), is(NodeType.PROCCALL));
//            assertThat(nte.getReceived(), is(NodeType.SYMBOL));
//        }
//
//        String goodInput = "print \"Blabla\n";
//
//        it.reset();
//        Node res = it.run(it.read(goodInput));
//
//        while(!it.finished()) {Thread.sleep(10);}
//
//        assertThat(outputs.size(), is(1));
//        assertThat(outputs.get(0), is("Blabla\n"));
//
//    }

    @Test
    public void testThreadedRecursion() throws InterruptedException {
        String input = "to recurse :i\n"
                + "print :i\n"
                + "if (:i > 0) [recurse :i - 1]\n"
                + "end\n"
                + "\n"
                + "recurse 8000\n";

        Node res = it.run(it.read(input));

        while(!it.finished()) {Thread.sleep(10);}

        assertThat(outputs.size(), is(8001));

        for (int i = 8000; i >= 0; i--) {
            assertThat(outputs.get(8000 - i), is(i + "\n"));
        }
    }
}
