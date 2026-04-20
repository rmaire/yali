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
import ch.uprisesoft.yali.exception.NodeTypeException;
import ch.uprisesoft.yali.ast.node.NodeType;
import ch.uprisesoft.yali.ast.node.word.IntegerWord;

import ch.uprisesoft.yali.runtime.interpreter.Interpreter;
import ch.uprisesoft.yali.runtime.procedures.ProcedureProvider;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class MockTurtleManager implements ProcedureProvider {
    
    MockTurtle turtle = new MockTurtle();

    public Node fd(Interpreter interpreter, java.util.List<Node> args) {
        Node arg = args.get(0);
        IntegerWord resolvedArg = null;
        if (!arg.type().equals(NodeType.INTEGER)) {
            throw new NodeTypeException(arg, arg.type(), NodeType.INTEGER);
        }
        resolvedArg = args.get(0).toIntegerWord();
        
        turtle.fd(resolvedArg.getInteger());
        return turtlepos();
    }

    public Node bk(Interpreter interpreter, java.util.List<Node> args) {
        Node arg = args.get(0);
        IntegerWord resolvedArg = null;
        if (!arg.type().equals(NodeType.INTEGER)) {
            throw new NodeTypeException(arg, arg.type(), NodeType.INTEGER);
        }
        resolvedArg = args.get(0).toIntegerWord();
        
        turtle.bk(resolvedArg.getInteger());
        return turtlepos();
    }

    public Node lt(Interpreter interpreter, java.util.List<Node> args) {
        Node arg = args.get(0);
        IntegerWord resolvedArg = null;
        if (!arg.type().equals(NodeType.INTEGER)) {
            throw new NodeTypeException(arg, arg.type(), NodeType.INTEGER);
        }
        resolvedArg = args.get(0).toIntegerWord();
        
        turtle.lt(resolvedArg.getInteger());
        return turtlepos();
    }

    public Node rt(Interpreter interpreter, java.util.List<Node> args) {
        Node arg = args.get(0);
        IntegerWord resolvedArg = null;
        if (!arg.type().equals(NodeType.INTEGER)) {
            throw new NodeTypeException(arg, arg.type(), NodeType.INTEGER);
        }
        resolvedArg = args.get(0).toIntegerWord();
        
        turtle.rt(resolvedArg.getInteger());
        return turtlepos();
    }

    public Node turtlepos(Interpreter interpreter, java.util.List<Node> args) {
        return turtlepos();
    }
    
    private Node turtlepos() {
        TurtlePosition tp = turtle.getPosition();
        List pos = new List();
        pos.addChild(new IntegerWord(Math.round(tp.x)));
        pos.addChild(new IntegerWord(Math.round(tp.y)));
        return pos;
    }

    @Override
    public Interpreter registerProcedures(Interpreter it) {
        it.env().define(new Procedure("fd", (interpreter, val) -> this.fd(interpreter, val), (interpreter, val) -> false, "__dist__"));
        it.env().define(new Procedure("bk", (interpreter, val) -> this.bk(interpreter, val), (interpreter, val) -> false, "__dist__"));
        it.env().define(new Procedure("lt", (interpreter, val) -> this.lt(interpreter, val), (interpreter, val) -> false, "__angle__"));
        it.env().define(new Procedure("rt", (interpreter, val) -> this.rt(interpreter, val), (interpreter, val) -> false, "__angle__"));
        it.env().define(new Procedure("turtlepos", (interpreter, val) -> this.turtlepos(interpreter, val), (interpreter, val) -> false));

        return it;
    }
}
