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
import ch.uprisesoft.yali.ast.node.word.BooleanWord;
import ch.uprisesoft.yali.ast.node.word.QuotedWord;
import ch.uprisesoft.yali.ast.node.word.SymbolWord;
import ch.uprisesoft.yali.ast.node.word.Word;
import java.util.ArrayList;
import ch.uprisesoft.yali.runtime.interpreter.Interpreter;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import ch.uprisesoft.yali.runtime.procedures.ProcedureProvider;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class Data implements ProcedureProvider {

    // Constructors
    public Optional<Node> reverse(Interpreter interpreter, java.util.List<Node> args) {
        if (args.get(0).type().equals(NodeType.LIST)) {
            List resultList = new List();
            java.util.List<Node> forwardList = args.get(0).getChildren();
            Collections.reverse(forwardList);
            resultList.addChildren(forwardList);
            return Optional.of(resultList);
        } else if (args.get(0).type().equals(NodeType.QUOTE)) {
            StringBuilder resultString = new StringBuilder(args.get(0).toQuotedWord().getQuote());
            return Optional.of(new QuotedWord(resultString.reverse().toString()));
        } else if (args.get(0).type().equals(NodeType.SYMBOL)) {
            StringBuilder resultString = new StringBuilder(args.get(0).toSymbolWord().getSymbol());
            return Optional.of(new SymbolWord(resultString.reverse().toString()));
        } else {
            throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.LIST, NodeType.QUOTE, NodeType.SYMBOL);
        }
    }

    public Optional<Node> fput(Interpreter interpreter, java.util.List<Node> args) {
        if (args.get(1).type().equals(NodeType.LIST)) {
            List resultList = new List();
            resultList.addChild(args.get(0));
            resultList.addChildren(args.get(1).getChildren());
            return Optional.of(resultList);
        } else if (args.get(1).type().equals(NodeType.QUOTE)) {
			String resultString = args.get(0).toQuotedWord().getQuote() + args.get(1).toQuotedWord().getQuote();
            return Optional.of(new QuotedWord(resultString));
        } else if (args.get(1).type().equals(NodeType.SYMBOL)) {
			String resultString = args.get(0).toSymbolWord().getSymbol() + args.get(1).toSymbolWord().getSymbol();
            return Optional.of(new SymbolWord(resultString));
        } else {
            throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.LIST, NodeType.QUOTE, NodeType.SYMBOL);
        }
    }

    public Optional<Node> lput(Interpreter interpreter, java.util.List<Node> args) {
        if (args.get(1).type().equals(NodeType.LIST)) {
            List resultList = new List();
            resultList.addChildren(args.get(1).getChildren());
            resultList.addChild(args.get(0));
            return Optional.of(resultList);
        } else if (args.get(1).type().equals(NodeType.QUOTE)) {
			String resultString = args.get(1).toQuotedWord().getQuote() + args.get(0).toQuotedWord().getQuote();
            return Optional.of(new QuotedWord(resultString));
        } else if (args.get(1).type().equals(NodeType.SYMBOL)) {
			String resultString = args.get(1).toSymbolWord().getSymbol() + args.get(0).toSymbolWord().getSymbol();
            return Optional.of(new SymbolWord(resultString));
        } else {
            throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.LIST, NodeType.QUOTE, NodeType.SYMBOL);
        }
    }

    public Optional<Node> word(Interpreter interpreter, java.util.List<Node> args) {
        StringBuilder concatenated = new StringBuilder();

        for (Node arg : args) {
            concatenated.append(stringifyNode(arg));
        }

        return Optional.of(new SymbolWord(concatenated.toString()));
    }

    public String stringifyNode(Node node) {
        String concatenated = "";

        switch (node.type()) {
            case BOOLEAN:
            case FLOAT:
            case INTEGER:
            case QUOTE:
                concatenated += node.toString();
                break;
            case NIL:
                break;
            case PROCCALL:
                concatenated += stringifyNode(node);
            case REFERENCE:
                concatenated += stringifyNode(node);
            case LIST:
                throw new NodeTypeException(node, NodeType.SYMBOL, NodeType.LIST);
            case NONE:
                throw new NodeTypeException(node, NodeType.SYMBOL, NodeType.NONE);
        }

        return concatenated;
    }

    public Optional<Node> sentence(Interpreter interpreter, java.util.List<Node> args) {
        List list = new List();
        list.addChildren(flatten(args).get());
        return Optional.of(list);
    }

    private Optional<java.util.List<Node>> flatten(java.util.List<Node> list) {
        java.util.List<Node> flattened = new ArrayList<>();

        for (Node n : list) {
            if (n.type().equals(NodeType.LIST)) {
                flattened.addAll(flatten(n.getChildren()).get());
            } else {
                flattened.add(n);
            }
        }

        return Optional.of(flattened);
    }

    public Optional<Node> list(Interpreter interpreter, java.util.List<Node> args) {
        List list = new List();

        for (Node n : args) {
            if(n.type().equals(NodeType.LIST)) {
            list.addChild(n);
            } else {
                list.addChild(Node.symbol(n.toString()));
            }
        }

        return Optional.of(list);
    }

    public Optional<Node> gensym(Interpreter interpreter, java.util.List<Node> args) {
        return Optional.of(new SymbolWord(UUID.randomUUID().toString().replace("-", "")));
    }

    // Selectors
    public Optional<Node> first(Interpreter interpreter, java.util.List<Node> args) {
        Node first = args.get(0);

        switch (args.get(0).type()) {
            case LIST:
                first = first.getChildren().get(0);
                break;
            case QUOTE:
                first = new QuotedWord(first.toQuotedWord().toString().substring(0, 1));
                break;
            case SYMBOL:
                first = new SymbolWord(first.toSymbolWord().getSymbol().substring(0, 1));
                break;
            default:
                throw new NodeTypeException(first, args.get(0).type(), NodeType.LIST);
        }

        return Optional.ofNullable(first);
    }

    public Optional<Node> last(Interpreter interpreter, java.util.List<Node> args) {

        Node last = args.get(0);

        switch (args.get(0).type()) {
            case LIST:
                last = last.getChildren().get(last.getChildren().size() - 1);
                break;
            case QUOTE:
                last = new QuotedWord(last.toQuotedWord().toString().substring(
                        last.toQuotedWord().toString().length() - 1
				));
                break;
            case SYMBOL:
                last = new QuotedWord(args.get(0).toSymbolWord().getSymbol().substring(
                        last.toSymbolWord().getSymbol().length() - 1,
                        last.toSymbolWord().getSymbol().length()));
                break;
            default:
                throw new NodeTypeException(last, args.get(0).type(), NodeType.LIST);
        }

        return Optional.ofNullable(last);
    }

    public Optional<Node> butfirst(Interpreter interpreter, java.util.List<Node> args) {

	    Node butfirst;

        switch (args.get(0).type()) {
            case LIST:
                butfirst = new List();
                butfirst.addChildren(args.get(0).getChildren().subList(1, args.get(0).getChildren().size()));
                break;
            case QUOTE:
                butfirst = new QuotedWord(args.get(0).toQuotedWord().toString().substring(1));
                break;
            case SYMBOL:
                butfirst = new QuotedWord(args.get(0).toSymbolWord().getSymbol().substring(1));
                break;
            default:
                throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.LIST);
        }

        return Optional.of(butfirst);
    }

    public Optional<Node> butlast(Interpreter interpreter, java.util.List<Node> args) {

        Node butlast;

        switch (args.get(0).type()) {
            case LIST:
                butlast = new List();
                butlast.addChildren(args.get(0).getChildren().subList(0, args.get(0).getChildren().size() - 1));
                break;
            case QUOTE:
                butlast = new QuotedWord(args.get(0).toQuotedWord().toString().substring(
                        0,
                        args.get(0).toQuotedWord().toString().length() - 1));
                break;
            case SYMBOL:
                butlast = new QuotedWord(args.get(0).toSymbolWord().getSymbol().substring(
                        0,
                        args.get(0).toSymbolWord().getSymbol().length() - 1));
                break;
            default:
                throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.LIST);
        }

        return Optional.of(butlast);
    }

    public Optional<Node> item(Interpreter interpreter, java.util.List<Node> args) {

        Node item;
        Node index = args.get(0);

        if (!index.type().equals(NodeType.INTEGER)) {
            throw new NodeTypeException(index, index.type(), NodeType.INTEGER);
        }

        switch (args.get(1).type()) {
            case LIST:
                item = args.get(1).getChildren().get(index.toIntegerWord().getInteger() - 1);
                break;
            case QUOTE:
                item = new QuotedWord(String.valueOf(
                        args.get(1).toQuotedWord().getQuote().charAt(
                                index.toIntegerWord().getInteger() - 1)));
                break;
            case SYMBOL:
                item = new SymbolWord(String.valueOf(
                        args.get(1).toSymbolWord().getSymbol().charAt(
                                index.toIntegerWord().getInteger() - 1)));
                break;
            default:
                throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.LIST);
        }

        return Optional.ofNullable(item);
    }

    // Mutators
    public Optional<Node> setitem(Interpreter interpreter, java.util.List<Node> args) {

//        it.apply(args.get(0));
        Node index = args.get(0);
        Node list = args.get(1);
        Node newVal = args.get(2);

        if (!index.type().equals(NodeType.INTEGER)) {
            throw new NodeTypeException(index, index.type(), NodeType.INTEGER);
        }

        if (!list.type().equals(NodeType.LIST)) {
            throw new NodeTypeException(list, list.type(), NodeType.LIST);
        }

        list.getChildren().set(index.toIntegerWord().getInteger() - 1, newVal);

        return Optional.of(list);
    }

    // Predicates
    public Optional<Node> emptyp(Interpreter interpreter, java.util.List<Node> args) {

        Node list = args.get(0);
        Node empty = new BooleanWord(Boolean.FALSE);

        if (list.type().equals(NodeType.LIST) && list.getChildren().isEmpty()) {
            empty = new BooleanWord(Boolean.TRUE);
        }

        return Optional.of(empty);
    }

    public Optional<Node> wordp(Interpreter interpreter, java.util.List<Node> args) {

        Node word = args.get(0);
        Node wordp = new BooleanWord(Boolean.FALSE);

        if (word.type().equals(NodeType.QUOTE) || word.type().equals(NodeType.SYMBOL) || word.type().equals(NodeType.INTEGER) || word.type().equals(NodeType.FLOAT)) {
            wordp = new BooleanWord(Boolean.TRUE);
        }

        return Optional.of(wordp);
    }

    public Optional<Node> numberp(Interpreter interpreter, java.util.List<Node> args) {

        Node word = args.get(0);
        Node wordp = new BooleanWord(Boolean.FALSE);

        if (word.type().equals(NodeType.INTEGER) || word.type().equals(NodeType.FLOAT)) {
            wordp = new BooleanWord(Boolean.TRUE);
        }

        return Optional.of(wordp);
    }

    public Optional<Node> listp(Interpreter interpreter, java.util.List<Node> args) {

        Node list = args.get(0);
        Node listp = new BooleanWord(Boolean.FALSE);

        if (list.type().equals(NodeType.LIST)) {
            listp = new BooleanWord(Boolean.TRUE);
        }

        return Optional.of(listp);
    }

    public Optional<Node> equalp(Interpreter interpreter, java.util.List<Node> args) {

        Node fst = args.get(0);
        Node snd = args.get(1);

		return Optional.of(new BooleanWord(fst.equals(snd)));
    }

    public Optional<Node> memberp(Interpreter interpreter, java.util.List<Node> args) {

        Node member = args.get(0);
        Node list = args.get(1);

        Node result = Word.bool(false);

        if (!list.type().equals(NodeType.LIST)) {
            throw new NodeTypeException(list, list.type(), NodeType.LIST);
        }

        for (Node lm : list.getChildren()) {
            if (lm.hashCode() == member.hashCode()) {
                java.util.List<Node> subArgs = new ArrayList<>();
                subArgs.add(member);
                subArgs.add(lm);
                result = equalp(interpreter, subArgs).get();
            }
        }

        return Optional.ofNullable(result.toBooleanWord());
    }

    // Queries
    public Optional<Node> count(Interpreter interpreter, java.util.List<Node> args) {

        Node element = args.get(0);

        if (element.type().equals(NodeType.LIST)) {
            return Optional.of(Node.integer(element.getChildren().size()));
        }

        if (element.type().equals(NodeType.QUOTE)) {
            return Optional.of(Node.integer(element.toQuotedWord().getQuote().length()));
        }

        if (element.type().equals(NodeType.SYMBOL)) {
            return Optional.of(Node.integer(element.toSymbolWord().getSymbol().length()));
        }

        return Optional.of(Node.integer(0));
    }

    public Optional<Node> lowercase(Interpreter interpreter, java.util.List<Node> args) {

        Node element = args.get(0);

        if (element.type().equals(NodeType.LIST)) {
            List result = new List();
            for (Node lm : element.getChildren()) {
                java.util.List<Node> subArgs = new ArrayList<>();
                subArgs.add(lm);
                result.addChild(lowercase(interpreter, subArgs).get());
            }
            return Optional.of(result);
        }

        if (element.type().equals(NodeType.QUOTE)) {
            return Optional.of(Node.quote(element.toQuotedWord().getQuote().toLowerCase()));
        }

        if (element.type().equals(NodeType.SYMBOL)) {
            return Optional.of(Node.quote(element.toSymbolWord().getSymbol().toLowerCase()));
        }

        return Optional.of(element);
    }

    public Optional<Node> uppercase(Interpreter interpreter, java.util.List<Node> args) {

        Node element = args.get(0);

        if (element.type().equals(NodeType.LIST)) {
            List result = new List();
            for (Node lm : element.getChildren()) {
                java.util.List<Node> subArgs = new ArrayList<>();
                subArgs.add(lm);
                result.addChild(uppercase(interpreter, subArgs).get());
            }
            return Optional.of(result);
        }

        if (element.type().equals(NodeType.QUOTE)) {
            return Optional.of(Node.quote(element.toQuotedWord().getQuote().toUpperCase()));
        }

        if (element.type().equals(NodeType.SYMBOL)) {
            return Optional.of(Node.quote(element.toSymbolWord().getSymbol().toUpperCase()));
        }

        return Optional.of(element);
    }

    @Override
    public Interpreter registerProcedures(Interpreter it) {
        it.env().define(new Procedure("uppercase", this::uppercase, "__element__"));
        it.env().define(new Procedure("lowercase", this::lowercase, "__element__"));
        it.env().define(new Procedure("count", this::count, "__element__"));
        it.env().define(new Procedure("equal?", this::equalp, "__fst", "__snd__"));
        it.env().define(new Procedure("member?", this::memberp, "__fst__", "__snd__"));
        it.env().define(new Procedure("list?", this::listp, "__list__"));
        it.env().define(new Procedure("number?", this::numberp, "__number__"));
        it.env().define(new Procedure("word?", this::wordp, "__word__"));
        it.env().define(new Procedure("empty?", this::emptyp, "__list__"));
        it.env().define(new Procedure("setitem", this::setitem, "__index__", "__list__", "__newval__"));
        it.env().define(new Procedure("item", this::item, "__index__", "__listorword__"));
        it.env().define(new Procedure("butlast", this::butlast, "__listorword__"));
        it.env().define(new Procedure("butfirst", this::butfirst, "__listorword__"));
        it.env().define(new Procedure("last", this::last, "__listorword__"));
        it.env().define(new Procedure("first", this::first, "__listorword__"));
        it.env().define(new Procedure("reverse", this::reverse, "__list__"));
        it.env().define(new Procedure("fput", this::fput, "__fst__", "__snd__"));
        it.env().define(new Procedure("lput", this::lput, "__fst__", "__snd__"));
        it.env().define(new Procedure("word", this::word, "__fst__", "__snd__"));
        it.env().define(new Procedure("list", this::list, "__fst__", "__snd__"));
        it.env().define(new Procedure("sentence", this::sentence, "__fst__", "__snd__"));
        it.env().define(new Procedure("gensym", this::gensym));

        return it;
    }
}
