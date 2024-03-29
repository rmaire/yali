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
package ch.uprisesoft.yali.scope;

import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.ast.node.Procedure;
import ch.uprisesoft.yali.runtime.interpreter.Tracer;
import ch.uprisesoft.yali.runtime.procedures.FunctionNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 *A datastructure to manage the scopes in a running program. Yali is dynamic 
 * scoped. This means that variable resolution searches up the calling chain, and 
 * not the lexical (written) definition of functions as most programming languages
 * do. This has the advantage that nested calls behaves kind of like closures and
 * enables some nice tricks with macros and global variables. It is also a little 
 * bit more intuitive for children and absolute beginners (the target demography 
 * for yali). 
 * Neverthless it irks professional programmers because the scoping isn't as clear
 * while reading code as with lexical scoping. Also, it is prone to overwriting
 * variables in global scope.
 * 
 * @author uprisesoft@gmail.com
 */
public class Environment {
    
    private List<Tracer> tracers = new ArrayList<>();

    private List<Scope> scopes = new ArrayList<>();
    
    public void addTracer(Tracer tracer) {
        tracers.add(tracer);
    }
    
    public Scope peek() {
        return scopes.get(scopes.size() - 1);
    }

    public boolean push(Scope scope) {
        scopes.add(scope);
        for(int i = scopes.size() -2; i >= 0; i--) {
                Scope currentScope = scopes.get(i);
                if(scope.name().equals(currentScope.name())) {
//                    for(int j = scopes.size()-1; j>=i; j--) {
//                        scopes.remove(j);
//                        scopes.add(currentScope);
//                    }
//                    return true;
                } else {
                }
            }
        
//        scopes.add(scope);
        return false;
    }

    public Scope pop() {
        return scopes.remove(scopes.size() - 1);
    }
    
    private Scope first() {
        return scopes.get(0);
    }
    
    /**
     * Variables
     */

    public void make(String name, Node value) {

        tracers.forEach(t -> t.make(name, value, this));
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).thingable(name.toLowerCase())) {
                scopes.get(i).make(name.toLowerCase(), value);
                return;
            }
        }
        
        scopes.get(0).make(name.toLowerCase(), value);
    }

    public void local(String name) {
        tracers.forEach(t -> t.local(name, this));
        peek().local(name.toLowerCase());
    }

    public Node thing(String name) {
        
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).thingable(name.toLowerCase())) {
                final Node ret = scopes.get(i).thing(name.toLowerCase());
                tracers.forEach(t -> t.thing(name, ret, this));
                return ret;
            }
        }

        tracers.forEach(t -> t.thing(name, Node.none(), this));
        return Node.none();
    }

    public Boolean thingable(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).thingable(name)) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Procedures
     */
    
    public void define(Procedure function) {
        first().make(function.getName(), function);
    }

    public Boolean defined(String name) {
        return first().thingable(name);
    }
    
    public Procedure procedure(String name) {
        return first().thing(name).toProcedureDef();
    }

    public void alias(String original, String alias) {
        if (!(first().thingable(original))) {
            throw new FunctionNotFoundException(original);
        }

        first().make(alias, first().thing(original));
    }
    
    public String trace() {
        StringBuilder sb = new StringBuilder();
        
        for(Scope s: scopes) {
            sb.append(s.name()).append("\n");
        }
        return sb.toString();
    }

    public Integer size() {
        return scopes.size();
    }
}
