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

import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.ast.node.NodeType;
import ch.uprisesoft.yali.helper.ObjectMother;
import ch.uprisesoft.yali.lexer.Lexer;
import ch.uprisesoft.yali.parser.Parser;
import ch.uprisesoft.yali.runtime.interpreter.UnthreadedInterpreter;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class LogicTest {

    private UnthreadedInterpreter it;
    private OutputObserver oo;
    private InputGenerator ig;
    
    public LogicTest() {
    }
    
    @BeforeEach
    public void setUp() {
        oo = new OutputObserver() {
            @Override
            public void inform(String output) {
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

        it = new ObjectMother().getInterpreter(oo, ig);
    }



    @Test
    public void testEqual() {
        oo.inform("Start testEqual()");
        String input = "2 = 2";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(true));
                
        oo.inform("End testEqual()");
    }
    
    @Test
    public void testNotEqual() {
        oo.inform("Start testNotEqual()");
        String input = "2 = 3";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(false));
                
        oo.inform("End testNotEqual()");
    }
    
    @Test
    public void testDoubleEqual() {
        oo.inform("Start testDoubleEqual()");
        String input = "2 == 2";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(true));
                
        oo.inform("End testDoubleEqual()");
    }
    
    @Test
    public void testNotDoubleEqual() {
        oo.inform("Start testNotDoubleEqual()");
        String input = "2 == 3";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(false));
                
        oo.inform("End testNotDoubleEqual()");
    }

    @Test
    public void testInequal() {
        oo.inform("Start testInequal()");
        String input = "2 != 3";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(true));
                
        oo.inform("End testInequal()");
    }
    
    @Test
    public void testNotInequal() {
        oo.inform("Start testNotInequal()");
        String input = "2 != 2";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(false));
                
        oo.inform("End testNotInequal()");
    }

    @Test
    public void testGreater() {
        oo.inform("Start testGreater()");
        String input = "2 > 1";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(true));
                
        oo.inform("End testGreater()");
    }
    
    @Test
    public void testNotGreater() {
        oo.inform("Start testNotGreater()");
        String input = "1 > 2";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(false));
        
        oo.inform("End testNotGreater()");
    }

    @Test
    public void testLess() {
        oo.inform("Start testLess()");
        String input = "1 < 2";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(true));
        
        oo.inform("End testLess()");
    }
    
    @Test
    public void testNotLess() {
        oo.inform("Start testNotLess()");
        String input = "2 < 1";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(false));
        
        oo.inform("End testNotLess()");
    }

    @Test
    public void testGreaterorequal() {
        oo.inform("Start testGreaterorequal()");
        String input = "2 >= 1";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(true));
        
        oo.inform("End testGreaterorequal()");
    }
    
    @Test
    public void testNotGreaterorequal() {
        oo.inform("Start testNotGreaterorequal()");
        String input = "2 >= 3";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(false));
        
        oo.inform("End testNotGreaterorequal()");
    }
    
    @Test
    public void testGreaterorequalSameVal() {
        oo.inform("Start testGreaterorequal()");
        String input = "2 >= 2";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(true));
        
        oo.inform("End testGreaterorequal()");
    }

    @Test
    public void testLessorequal() {
        oo.inform("Start testLessorequal()");
        String input = "1 <= 2";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(true));
        
        oo.inform("End testLessorequal()");
    }
    
    @Test
    public void testNotLessorequal() {
        oo.inform("Start testNotLessorequal()");
        String input = "3 <= 2";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(false));
        
        oo.inform("End testNotLessorequal()");
    }
    
    @Test
    public void testLessorequalSameVal() {
        oo.inform("Start testLessorequalSameVal()");
        String input = "2 <= 2";
        Node res = it.run(it.read(input));
                
        assertThat(res.type(), is(NodeType.BOOLEAN));
        assertThat(res.toBooleanWord().getBoolean(), is(true));
        
        oo.inform("End testLessorequalSameVal()");
    }
    
}
