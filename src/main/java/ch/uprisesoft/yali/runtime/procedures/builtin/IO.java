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

import ch.uprisesoft.yali.ast.node.*;
import ch.uprisesoft.yali.exception.NodeTypeException;
import ch.uprisesoft.yali.ast.node.word.QuotedWord;
import ch.uprisesoft.yali.runtime.interpreter.Interpreter;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.InputReceiver;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import ch.uprisesoft.yali.runtime.io.OutputSubject;
import java.util.ArrayList;
import java.util.Optional;

import ch.uprisesoft.yali.runtime.procedures.ProcedureProvider;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class IO implements ProcedureProvider, OutputSubject, InputReceiver {

    private final java.util.List<OutputObserver> observers = new ArrayList<>();
    private InputGenerator generator;

    public Optional<Node> print(Interpreter interpreter, java.util.List<Node> args) {
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

		java.util.List<String> stringifiedArgs = new ArrayList<>(interpreter.stringify(concreteArgs));

        inform(String.join(" ", stringifiedArgs) + "\n");

        return Optional.of(returnValue);
    }

    public Optional<Node> show(Interpreter interpreter, java.util.List<Node> args) {
		java.util.List<String> stringifiedArgs = new ArrayList<>(interpreter.stringify(args));

        inform(String.join(" ", stringifiedArgs) + "\n");
        return Optional.of(Node.nil());
    }

    public Optional<Node> type(Interpreter interpreter, java.util.List<Node> args) {
		java.util.List<String> stringifiedArgs = new ArrayList<>(interpreter.stringify(args));

        inform(String.join(" ", stringifiedArgs));
        return Optional.of(Node.nil());
    }

    public Optional<Node> readword(Interpreter interpreter, java.util.List<Node> args) {
		return Optional.of(new QuotedWord(requestLine()));
    }

    public Optional<Node> readlist(Interpreter interpreter, java.util.List<Node> args) {

		String list = "[" +
				requestLine() +
				"]";

		return Optional.ofNullable((List) interpreter.read(list));
    }

    @Override
    public Interpreter registerProcedures(Interpreter it) {
        it.env().define(new Procedure("readword", this::readword));
        it.env().define(new Procedure("readlist", this::readlist));
        it.env().define(new Procedure("show", this::show, "__output__"));
        it.env().define(new Procedure("type", this::type, "__output__"));
        it.env().define(new Procedure("print", this::print, "__output__"));

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

    private String requestLine() {
        if (generator == null) {
            return "";
        }

        return generator.requestLine();
    }
}
