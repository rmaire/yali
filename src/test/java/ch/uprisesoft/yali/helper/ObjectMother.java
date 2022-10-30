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
package ch.uprisesoft.yali.helper;

import ch.uprisesoft.yali.lexer.Lexer;
import ch.uprisesoft.yali.parser.Parser;
import ch.uprisesoft.yali.runtime.procedures.builtin.MockTurtleManager;
import ch.uprisesoft.yali.runtime.interpreter.UnthreadedInterpreter;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class ObjectMother {
    
    private UnthreadedInterpreter i;
    private Parser p;
    private Lexer l;
    
    public ObjectMother(OutputObserver o) {
        
        this.i = new UnthreadedInterpreter();
        this.p = new Parser(i);
        this.l = new Lexer();
        this.i.loadStdLib();
        
        MockTurtleManager mtm = new MockTurtleManager();
        mtm.registerProcedures(i);
    }
    
    public ObjectMother(OutputObserver oo, InputGenerator ig) {
        this.i = new UnthreadedInterpreter();
        this.i.loadStdLib(oo, ig);
        
        MockTurtleManager mtm = new MockTurtleManager();
        mtm.registerProcedures(i);
        
        this.p = new Parser(i);
        this.l = new Lexer();
    }
    
    public ObjectMother() {
        
        OutputObserver oo = new OutputObserver() {
            
            @Override
            public void inform(String output) {
                System.out.println(output);
            }
        };
        
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
        
        this.i = new UnthreadedInterpreter();
        this.i.loadStdLib(oo, ig);
        
        MockTurtleManager mtm = new MockTurtleManager();
        mtm.registerProcedures(i);
        
        this.p = new Parser(i);
        this.l = new Lexer();
    }


    public UnthreadedInterpreter getInterpreter() {
        return i;
    }

    public Parser getParser() {
        return p;
    }

    public Lexer getLexer() {
        return l;
    }
}
