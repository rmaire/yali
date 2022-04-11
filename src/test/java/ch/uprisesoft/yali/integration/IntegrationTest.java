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
package ch.uprisesoft.yali.integration;

import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.helper.ObjectMother;
import ch.uprisesoft.yali.runtime.interpreter.Interpreter;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class IntegrationTest {

    Interpreter it;
    private OutputObserver oo;
    private InputGenerator ig;
    private java.util.List<String> outputs;

    public IntegrationTest() {
    }

    @BeforeEach
    public void setUp() {
        oo = new OutputObserver() {

            @Override
            public void inform(String output) {
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

        outputs = new ArrayList<>();
    }

    @Test
    public void testExp1() {
        StringBuilder sb = new StringBuilder();
        sb.append("fd 60 rt 120 fd 60 rt 120 fd 60 rt 120").append("\n");
        Node res = it.run(it.read(sb.toString()));
    }
    
    @Test
    public void testExp2() {
        StringBuilder sb = new StringBuilder();
        sb.append("make \"first_programmer \"Ada_Lovelace").append("\n");
        sb.append("print :first_programmer").append("\n");
        Node res = it.run(it.read(sb.toString()));
    }
    
    @Test
    public void testExp3() {
        StringBuilder sb = new StringBuilder();
        sb.append("make \"angle 0").append("\n");
        sb.append("repeat 10 [fd 3 rt :angle make \"angle :angle + 7]").append("\n");
        Node res = it.run(it.read(sb.toString()));
    }
    
    @Test
    public void testExp4() {
        StringBuilder sb = new StringBuilder();
        sb.append("make \"size 81 / 9").append("\n");
        sb.append("print 2*3").append("\n");
        sb.append("print :size - 4").append("\n");
        Node res = it.run(it.read(sb.toString()));
    }

}
