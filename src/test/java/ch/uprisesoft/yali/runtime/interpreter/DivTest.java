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

import ch.uprisesoft.yali.ast.node.NodeType;
import ch.uprisesoft.yali.exception.NodeTypeException;
import ch.uprisesoft.yali.exception.UnexpectedCharacterException;
import ch.uprisesoft.yali.helper.ObjectMother;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author uprisesoft@gmail.com
 */
//@Disabled
public class DivTest {

    private java.util.List<String> outputs;

    private UnthreadedInterpreter it;

    private OutputObserver oo;
    private InputGenerator ig;

    public DivTest() {
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
        it = om.getInterpreter();
    }

    @Test
    public void testGibberish() {
        StringBuilder sb = new StringBuilder();
        sb.append("repeat 4 fd 100 rt 90]").append("\n");
        UnexpectedCharacterException uce = assertThrows(UnexpectedCharacterException.class, () -> it.read(sb.toString()));

        assertThat(uce.getCharacter(), is("]"));
        assertThat(uce.getLine(), is(1));
        assertThat(uce.getPos(), is(22));
    }
}
