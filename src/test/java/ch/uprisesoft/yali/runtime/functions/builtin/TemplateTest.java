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
import ch.uprisesoft.yali.lexer.Lexer;
import ch.uprisesoft.yali.helper.ObjectMother;
import ch.uprisesoft.yali.parser.Parser;
import ch.uprisesoft.yali.parser.Parser;
import ch.uprisesoft.yali.runtime.interpreter.UnthreadedInterpreter;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

/**
 *
 * @author uprisesoft@gmail.com
 */

public class TemplateTest {

    private UnthreadedInterpreter it;
    private OutputObserver oo;
    private InputGenerator ig;
    private java.util.List<String> outputs;

    public TemplateTest() {
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

        it = new ObjectMother().getInterpreter(oo, ig);
        
        outputs = new ArrayList<>();
    }
    
    @Test
    public void testMap() {
        Node result = it.run(it.read("map [? * ?] [1 2 3]"));
        
        assertThat(result.getChildren().size(), is(3));
        assertThat(result.getChildren().get(0).type(), is(NodeType.INTEGER));
        assertThat(result.getChildren().get(0).toIntegerWord().getInteger(), is(1));
        assertThat(result.getChildren().get(1).type(), is(NodeType.INTEGER));
        assertThat(result.getChildren().get(1).toIntegerWord().getInteger(), is(4));
        assertThat(result.getChildren().get(2).type(), is(NodeType.INTEGER));
        assertThat(result.getChildren().get(2).toIntegerWord().getInteger(), is(9));
    }
    
    @Test
    public void testMap2() {
        Node result = it.run(it.read("map [equal? (mod ? 2) 1] [1 2 3 4]"));
        
        assertThat(result.getChildren().size(), is(4));
        assertThat(result.getChildren().get(0).type(), is(NodeType.BOOLEAN));
        assertThat(result.getChildren().get(0).toBooleanWord().getBoolean(), is(true));
        assertThat(result.getChildren().get(1).type(), is(NodeType.BOOLEAN));
        assertThat(result.getChildren().get(1).toBooleanWord().getBoolean(), is(false));
        assertThat(result.getChildren().get(2).type(), is(NodeType.BOOLEAN));
        assertThat(result.getChildren().get(2).toBooleanWord().getBoolean(), is(true));
        assertThat(result.getChildren().get(3).type(), is(NodeType.BOOLEAN));
        assertThat(result.getChildren().get(3).toBooleanWord().getBoolean(), is(false));
    }
    
     @Test
    public void testMapQuote() {
        Node result = it.run(it.read("map [uppercase ?] \"abcd"));
        
        assertThat(result.type(), is(NodeType.QUOTE));
        assertThat(result.toQuotedWord().getQuote(), is("ABCD"));
    }
    
    @Test
    public void testFilter() {
        Node result = it.run(it.read("filter [equal? (mod ? 2) 1] [1 2 3 4]"));
        
        assertThat(result.getChildren().size(), is(2));
        assertThat(result.getChildren().get(0).type(), is(NodeType.SYMBOL));
        assertThat(result.getChildren().get(0).toSymbolWord().getSymbol(), is("1"));
        assertThat(result.getChildren().get(1).type(), is(NodeType.SYMBOL));
        assertThat(result.getChildren().get(1).toSymbolWord().getSymbol(), is("3")); 
    }
    
    @Test
    public void testFilterQuote() {
        Node result = it.run(it.read("filter [notequal? ? b] \"abcd"));
        
        assertThat(result.type(), is(NodeType.QUOTE));
        assertThat(result.toQuotedWord().getQuote(), is("acd"));
    }
    
//    @Test
//    public void testFind() {
//        Node result = it.run(it.read("find [equal? ? 2] [1 2 3 4]"));
//        
//        assertThat(result.toSymbolWord().getSymbol(), is("2"));
//    }
    
//    @Test
//    public void testFindWord() {
//        Node result = it.run(it.read("find [equal? ? \"b] \"abcd"));
//        
//        assertThat(result.toQuotedWord().getQuote(), is("b"));
//    }
}
