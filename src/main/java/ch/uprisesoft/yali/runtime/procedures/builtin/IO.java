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
package ch.uprisesoft.yali.runtime.procedures.builtin;

import ch.uprisesoft.yali.ast.node.Procedure;
import ch.uprisesoft.yali.ast.node.List;
import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.ast.node.NodeType;
import ch.uprisesoft.yali.exception.NodeTypeException;
import ch.uprisesoft.yali.ast.node.word.QuotedWord;
import ch.uprisesoft.yali.runtime.interpreter.Interpreter;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.InputReceiver;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import ch.uprisesoft.yali.runtime.io.OutputSubject;
import ch.uprisesoft.yali.scope.Scope;
import java.util.ArrayList;
import ch.uprisesoft.yali.runtime.procedures.ProcedureProvider;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class IO implements ProcedureProvider, OutputSubject, InputReceiver {

    private java.util.List<OutputObserver> observers = new ArrayList<>();
    private InputGenerator generator;

    public Node print(Scope scope, java.util.List<Node> args) {
        java.util.List<Node> concreteArgs = new ArrayList<>();
        
        List returnValue = new List();
        for(Node arg: args) {
            switch (arg.type()) {
                case SYMBOL:
                    throw new NodeTypeException(arg, NodeType.SYMBOL, NodeType.BOOLEAN, NodeType.FLOAT, NodeType.PROCCALL, NodeType.INTEGER, NodeType.LIST, NodeType.NIL, NodeType.QUOTE, NodeType.REFERENCE);
                case PROCCALL:
                case REFERENCE:
                    concreteArgs.add(arg);
                    break;
                default:
                    concreteArgs.add(arg);
                    break;
            }
            returnValue.addChild(arg);
        }
        
        java.util.List<String> stringifiedArgs = new ArrayList<>();
        stringifiedArgs.addAll(scope.callingInterpreter().stringify(concreteArgs));

        inform(String.join(" ", stringifiedArgs) + "\n");

        return returnValue;
    }

    public Node show(Scope scope, java.util.List<Node> args) {
        java.util.List<String> stringifiedArgs = new ArrayList<>();
        stringifiedArgs.addAll(scope.callingInterpreter().stringify(args));

        inform(String.join(" ", stringifiedArgs) + "\n");
        return Node.nil();
    }

    public Node type(Scope scope, java.util.List<Node> args) {
        java.util.List<String> stringifiedArgs = new ArrayList<>();
        stringifiedArgs.addAll(scope.callingInterpreter().stringify(args));

        inform(String.join(" ", stringifiedArgs));
        return Node.nil();
    }

    public Node readword(Scope scope, java.util.List<Node> args) {
        QuotedWord result = new QuotedWord(requestLine());
        return result;
    }

    public Node readlist(Scope scope, java.util.List<Node> args) {

        StringBuffer list = new StringBuffer();
        list.append("[");
        list.append(requestLine());
        list.append("]");

        List result = (List) scope.callingInterpreter().read(list.toString());

        return result;
    }

    @Override
    public Interpreter registerProcedures(Interpreter it) {
        it.env().define(new Procedure("readword", (scope, val) -> this.readword(scope, val), (scope, val) -> Node.none()));
        it.env().define(new Procedure("readlist", (scope, val) -> this.readlist(scope, val), (scope, val) -> Node.none()));
        it.env().define(new Procedure("show", (scope, val) -> this.show(scope, val), (scope, val) -> Node.none(), "__output__"));
        it.env().define(new Procedure("type", (scope, val) -> this.type(scope, val), (scope, val) -> Node.none(), "__output__"));
        it.env().define(new Procedure("print", (scope, val) -> this.print(scope, val), (scope, val) -> Node.none(), "__output__"));

        return it;
    }

    @Override
    public void register(InputGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void register(OutputObserver observer) {
        observers.add(observer);
    }

    private void inform(String output) {
        for (OutputObserver oo : observers) {
            oo.inform(output);
        }
    }

    private String request() {
        return generator.request();
    }

    private String requestLine() {
        if (generator == null) {
            return "";
        }

        return generator.requestLine();
    }
}
