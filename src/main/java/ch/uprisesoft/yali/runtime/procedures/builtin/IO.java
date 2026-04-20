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

    public Node print(Interpreter interpreter, java.util.List<Node> args) {
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
        stringifiedArgs.addAll(interpreter.stringify(concreteArgs));

        inform(String.join(" ", stringifiedArgs) + "\n");

        return returnValue;
    }

    public Node show(Interpreter interpreter, java.util.List<Node> args) {
        java.util.List<String> stringifiedArgs = new ArrayList<>();
        stringifiedArgs.addAll(interpreter.stringify(args));

        inform(String.join(" ", stringifiedArgs) + "\n");
        return Node.nil();
    }

    public Node type(Interpreter interpreter, java.util.List<Node> args) {
        java.util.List<String> stringifiedArgs = new ArrayList<>();
        stringifiedArgs.addAll(interpreter.stringify(args));

        inform(String.join(" ", stringifiedArgs));
        return Node.nil();
    }

    public Node readword(Interpreter interpreter, java.util.List<Node> args) {
        QuotedWord result = new QuotedWord(requestLine());
        return result;
    }

    public Node readlist(Interpreter interpreter, java.util.List<Node> args) {

        StringBuffer list = new StringBuffer();
        list.append("[");
        list.append(requestLine());
        list.append("]");

        List result = (List) interpreter.read(list.toString());

        return result;
    }

    @Override
    public Interpreter registerProcedures(Interpreter it) {
        it.env().define(new Procedure("readword", (interpreter, val) -> this.readword(interpreter, val), (interpreter, val) -> false));
        it.env().define(new Procedure("readlist", (interpreter, val) -> this.readlist(interpreter, val), (interpreter, val) -> false));
        it.env().define(new Procedure("show", (interpreter, val) -> this.show(interpreter, val), (interpreter, val) -> false, "__output__"));
        it.env().define(new Procedure("type", (interpreter, val) -> this.type(interpreter, val), (interpreter, val) -> false, "__output__"));
        it.env().define(new Procedure("print", (interpreter, val) -> this.print(interpreter, val), (interpreter, val) -> false, "__output__"));

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
