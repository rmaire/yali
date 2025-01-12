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

import ch.uprisesoft.yali.exception.UnexpectedCharacterException;
import ch.uprisesoft.yali.helper.ObjectMother;
import ch.uprisesoft.yali.parser.Parser;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import ch.uprisesoft.yali.runtime.procedures.builtin.MockTurtleManager;
import ch.uprisesoft.yali.scope.Scope;
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
public class SubScopeInterpreterTest {

    private java.util.List<String> outputs;

    private UnthreadedInterpreter it;

    private OutputObserver oo;
    private InputGenerator ig;

    public SubScopeInterpreterTest() {
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

        it = new UnthreadedInterpreter();

        Parser p = new Parser(it);
    }

    @Test
    public void testSubScope() {
        it.env().push(new Scope("test"));
        it.loadStdLib(oo, ig);
        MockTurtleManager mtm = new MockTurtleManager();
        mtm.registerProcedures(it);

        assertThat(it.env().first().thingable("turtlepos"), is(false));
        assertThat(it.env().peek().thingable("turtlepos"), is(true));
    }
}
