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

import ch.uprisesoft.yali.ast.node.Call;
import ch.uprisesoft.yali.ast.node.Procedure;
import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.exception.NodeTypeException;
import ch.uprisesoft.yali.ast.node.NodeType;
import ch.uprisesoft.yali.runtime.interpreter.Interpreter;
import ch.uprisesoft.yali.runtime.procedures.ProcedureProvider;
import ch.uprisesoft.yali.scope.VariableNotFoundException;

import java.util.ArrayList;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class Control implements ProcedureProvider {

	// Only here so we don't have to strip it out as edge cases in the Reader. Does nothing.
	public Node alias(Interpreter interpreter, java.util.List<Node> args) {
		return Node.nil();
	}

	public Node thing(Interpreter interpreter, java.util.List<Node> args) {

		final String name;
		switch (args.get(0).type()) {
			case SYMBOL:
				name = args.get(0).toSymbolWord().getSymbol();
				break;
			case QUOTE:
				name = args.get(0).toQuotedWord().getQuote();
				break;
			default:
				throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.SYMBOL, NodeType.QUOTE);
		}

		if (!interpreter.env().thingable(name)) {
			throw new VariableNotFoundException(name);
		}

		return interpreter.env().thing(name);
	}

	public Node local(Interpreter interpreter, java.util.List<Node> args) {
		final String name;

		switch (args.get(0).type()) {
			case SYMBOL:
				name = args.get(0).toSymbolWord().getSymbol();
				break;
			case QUOTE:
				name = args.get(0).toQuotedWord().getQuote();
				break;
			default:
				throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.SYMBOL, NodeType.QUOTE);
		}

		interpreter.tracers().forEach(t -> t.local(name, interpreter.env()));
		interpreter.env().local(name);

		return Node.nil();
	}

	public Node make(Interpreter interpreter, java.util.List<Node> args) {
		final Node newVar;
		final String name;

		switch (args.get(0).type()) {
			case SYMBOL:
				name = args.get(0).toSymbolWord().getSymbol();
				break;
			case QUOTE:
				name = args.get(0).toQuotedWord().getQuote();
				break;
			default:
				throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.SYMBOL, NodeType.QUOTE);
		}

		newVar = args.get(1);

		interpreter.env().make(name, newVar);

		return newVar;
	}

	public Node localmake(Interpreter interpreter, java.util.List<Node> args) {
		Node newVar;
		String name;

		switch (args.get(0).type()) {
			case SYMBOL:
				name = args.get(0).toSymbolWord().getSymbol();
				break;
			case QUOTE:
				name = args.get(0).toQuotedWord().getQuote();
				break;
			default:
				throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.SYMBOL, NodeType.QUOTE);
		}

		newVar = args.get(1);
		interpreter.env().local(name);
		interpreter.env().make(name, newVar);

		return newVar;
	}

	private java.util.List<Node> ifexprsToRun;

	public Node ifexpr(Interpreter interpreter, java.util.List<Node> args) {
		Node result = Node.none();

		if (ifexprsToRun == null) {

			ifexprsToRun = new ArrayList<>();

			Node condition = args.get(0);
			Node iftrue = args.get(1);

			if (!condition.type().equals(NodeType.BOOLEAN)) {
				throw new NodeTypeException(condition, condition.type(), NodeType.BOOLEAN);
			}

			if (condition.toBooleanWord().getBoolean()) {
				Node ast = interpreter.read(iftrue.toList());
				ifexprsToRun.addAll(ast.getChildren());
			} else {
//                result = it.output(Node.nil());
				return Node.nil();
			}

		}

		if (!ifexprsToRun.isEmpty()) {
			Call next = ifexprsToRun.remove(0).toProcedureCall();
			interpreter.schedule(next);
		}

		return result;
	}

	private Boolean ifexprFinished() {
		if (ifexprsToRun.isEmpty()) {
			ifexprsToRun = null;
			return false;
		} else {
			return true;
		}
	}

	private java.util.List<Node> ifelseexprsToRun;

	public Node ifelseexpr(Interpreter interpreter, java.util.List<Node> args) {
		Node result = Node.none();

		if (ifelseexprsToRun == null) {
			ifelseexprsToRun = new ArrayList<>();

			Node condition = args.get(0);
			Node iftrue = args.get(1);
			Node iffalse = args.get(2);

			if (!condition.type().equals(NodeType.BOOLEAN)) {
				throw new NodeTypeException(condition, condition.type(), NodeType.BOOLEAN);
			}

			if (condition.toBooleanWord().getBoolean()) {
				Node ast = interpreter.read(iftrue.toList());
				ifelseexprsToRun.addAll(ast.getChildren());
			} else {
				Node ast = interpreter.read(iffalse.toList());
				ifelseexprsToRun.addAll(ast.getChildren());
			}
		}

		if (!ifelseexprsToRun.isEmpty()) {
			Call next = ifelseexprsToRun.remove(0).toProcedureCall();
			interpreter.schedule(next);
		}

		return result;
	}

	private Boolean ifelseexprFinished() {
		if (ifelseexprsToRun.isEmpty()) {
			ifelseexprsToRun = null;
			return false;
		} else {
			return true;
		}
	}

	private java.util.List<Node> repeatexprToRun;

	public Node repeat(Interpreter interpreter, java.util.List<Node> args) {
		Node result = Node.none();

		if (repeatexprToRun == null) {
			repeatexprToRun = new ArrayList<>();

			Node control = args.get(0);
			Node block = args.get(1);

			if (!control.type().equals(NodeType.INTEGER)) {
				throw new NodeTypeException(control, control.type(), NodeType.INTEGER);
			}

			if (!block.type().equals(NodeType.LIST)) {
				throw new NodeTypeException(block, block.type(), NodeType.LIST);
			}

			Integer idx = control.toIntegerWord().getInteger();
			result = Node.nil();

			for (int i = 0; i < idx; i++) {
				Node ast = interpreter.read(block.toList());
				repeatexprToRun.addAll(ast.getChildren());
//                result = run(scope, block.toList());
			}
		}

		if (!repeatexprToRun.isEmpty()) {
			Call next = repeatexprToRun.remove(0).toProcedureCall();
			interpreter.schedule(next);
//            it.schedule(it.read("print \"onemore").getChildren().get(0).toProcedureCall());
		}

		return result;
	}

	private Boolean repeatexprFinished() {
		if (repeatexprToRun.isEmpty()) {
			repeatexprToRun = null;
			return false;
		} else {
			return true;
		}
	}

	private java.util.List<Node> proceduresToRun;

	public Node run(Interpreter interpreter, java.util.List<Node> args) {

		Node result = Node.none();

		if (proceduresToRun == null) {
			proceduresToRun = new ArrayList<>();
			Node ast = interpreter.read(args.get(0).toList());
			proceduresToRun.addAll(ast.getChildren());
		}

		Call next = proceduresToRun.remove(0).toProcedureCall();
		interpreter.schedule(next);

		return result;
	}

	private Boolean runFinished() {
		if (proceduresToRun.isEmpty()) {
			proceduresToRun = null;
			return false;
		} else {
			return true;
		}
	}

	public Node output(Interpreter interpreter, java.util.List<Node> args) {
		return args.get(0);
	}

	public Node pause(Interpreter interpreter, java.util.List<Node> args) {
		interpreter.pause();
		return Node.nil();
	}

	@Override
	public Interpreter registerProcedures(Interpreter it) {
		it.env().define(new Procedure("alias",  this::alias, () -> false, "__original__", "__alias__"));
		it.env().define(new Procedure("thing", this::thing, () -> false, "__name__").macro());
		it.env().define(new Procedure("make", this::make, () -> false, "__name__", "__value__").macro());
		it.env().define(new Procedure("local", this::local, () -> false, "__name__").macro());
		it.env().define(new Procedure("localmake", this::localmake, () -> false, "__name__", "__value__").macro());
		it.env().define(new Procedure("repeat", this::repeat, this::repeatexprFinished, "__control__", "__block__").macro());
		it.env().define(new Procedure("run", this::run, this::runFinished, "__block__").macro());
		it.env().define(new Procedure("output", this::output, () -> false, "__block__"));
		it.env().define(new Procedure("stop", this::output, () -> false));
		it.env().define(new Procedure("ifelse", this::ifelseexpr, this::ifelseexprFinished, "__condition__", "__iftrue__", "__iffalse__").macro());
		it.env().define(new Procedure("if", this::ifexpr, this::ifexprFinished, "__condition__", "__iftrue__").macro());
		it.env().define(new Procedure("pause", this::pause, () -> false).macro());

		return it;
	}
}
