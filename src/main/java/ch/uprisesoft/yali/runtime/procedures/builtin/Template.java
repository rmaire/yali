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
import ch.uprisesoft.yali.ast.node.List;
import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.exception.NodeTypeException;
import ch.uprisesoft.yali.ast.node.NodeType;
import ch.uprisesoft.yali.ast.node.word.QuotedWord;
import ch.uprisesoft.yali.scope.Scope;
import ch.uprisesoft.yali.runtime.interpreter.Interpreter;
import java.util.ArrayList;
import ch.uprisesoft.yali.runtime.procedures.ProcedureProvider;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class Template implements ProcedureProvider {

    private Interpreter it;

    // TODO
    private Node template = Node.none();
    private java.util.List<Node> mapResults = new ArrayList<>();
    private java.util.List<Node> mapValues = new ArrayList<>();
    private boolean mapIsList = false;
    private boolean mapRunning = false;

    public Node map(Scope scope, java.util.List<Node> args) {
        if (scope.thingable("__last_map_result__")) {
            Node res = scope.thing("__last_map_result__");
            mapResults.add(res);
            scope.unmake("__last_map_result__");
        }

        if (mapRunning && mapValues.isEmpty()) {
            mapRunning = false;
            return returnMapResult();
        }

        if (!mapRunning) {
            if (args.get(0).type().equals(NodeType.LIST)) {
                template = args.get(0).toList();
            } else {
                throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.LIST);
            }

            if (args.get(1).type().equals(NodeType.LIST)) {
                mapValues = args.get(1).toList().getChildren();
                mapIsList = true;
            } else if (args.get(1).type().equals(NodeType.QUOTE)) {
                String valueChars = args.get(1).toQuotedWord().getQuote();
                for (int i = 0; i < valueChars.length(); i++) {
                    mapValues.add(Node.symbol(String.valueOf(valueChars.charAt(i))));
                }
                mapIsList = false;
            } else {
                throw new NodeTypeException(args.get(1), args.get(1).type(), NodeType.LIST, NodeType.QUOTE);
            }
            mapRunning = true;
        }

        Node val = mapValues.remove(0);
        java.util.List<Node> realizedValues = new ArrayList<>();
        for (Node n : template.getChildren()) {
            if (n.type().equals(NodeType.SYMBOL) && n.toSymbolWord().getSymbol().equals("?")) {
                realizedValues.add(val);
            } else {
                realizedValues.add(n);
            }
        }

        String realizedString = Node.list(realizedValues).toString().substring(1, Node.list(realizedValues).toString().length() - 1);
        Call c = it.read("make \"__last_map_result__ " + realizedString).getChildren().get(0).toProcedureCall();
        it.schedule(c);

        return returnMapResult();
    }

    public Node mapFinished(Scope scope, Node result) {
        if (!mapRunning) {
            mapResults.clear();
            template = Node.none();
            mapValues = new ArrayList<>();
            return Node.bool(false);
        }
        return Node.bool(true);
    }

    private Node returnMapResult() {
        if (mapIsList) {
            return Node.list(mapResults);
        } else {
            StringBuilder result = new StringBuilder();

            for (Node r : mapResults) {
                result.append(r.toString());
            }
            it.output(Node.quote(result.toString()));
            return Node.quote(result.toString());
        }
    }

    // TODO
    public Node filter(Scope scope, java.util.List<Node> args) {

        Node template = Node.none();

        if (args.get(0).type().equals(NodeType.LIST)) {
            template = args.get(0).toList();
        } else {
            throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.LIST);
        }

        if (args.get(1).type().equals(NodeType.LIST)) {
            Node values = args.get(1).toList();

            java.util.List<Node> results = new ArrayList<>();

            for (int i = 0; i < values.getChildren().size(); i++) {
                Node val = values.getChildren().get(i);
                java.util.List<Node> realizedValues = new ArrayList<>();
                for (Node n : template.getChildren()) {
                    if (n.type().equals(NodeType.SYMBOL) && n.toSymbolWord().getSymbol().equals("?")) {
                        realizedValues.add(val);
                    } else {
                        realizedValues.add(n);
                    }
                }

                List l = new List(realizedValues);
                String runCommand = l.toString().substring(1, l.toString().length() - 1);
                Node result = it.runBounded(it.read(runCommand));

                if (!result.type().equals(NodeType.BOOLEAN)) {
                    throw new NodeTypeException(template, result.type(), NodeType.BOOLEAN);
                }

                if (result.toBooleanWord().getBoolean()) {
                    results.add(val);
                }
            }
            return new List(results);

        } else if (args.get(1).type().equals(NodeType.QUOTE)) {
            String values = args.get(1).toQuotedWord().getQuote();

            String results = "";

            for (int i = 0; i < values.length(); i++) {
                Character val = values.charAt(i);
                java.util.List<Node> realizedValues = new ArrayList<>();
                for (Node n : template.getChildren()) {
                    if (n.type().equals(NodeType.SYMBOL) && n.toSymbolWord().getSymbol().equals("?")) {
                        realizedValues.add(Node.symbol("\"" + String.valueOf(val)));
                    } else {
                        realizedValues.add(n);
                    }
                }

                List l = new List(realizedValues);
                String runCommand = l.toString().substring(1, l.toString().length() - 1);
                Node result = it.runBounded(it.read(runCommand));

                if (!result.type().equals(NodeType.BOOLEAN)) {
                    throw new NodeTypeException(template, result.type(), NodeType.BOOLEAN);
                }

                if (result.toBooleanWord().getBoolean()) {
                    results += val;
                }
            }
            return new QuotedWord(results);

        } else {
            throw new NodeTypeException(args.get(1), args.get(1).type(), NodeType.LIST, NodeType.QUOTE);
        }
    }

    // TODO
    public Node find(Scope scope, java.util.List<Node> args) {
        Node template = Node.none();

        if (args.get(0).type().equals(NodeType.LIST)) {
            template = args.get(0).toList();
        } else {
            throw new NodeTypeException(args.get(0), args.get(0).type(), NodeType.LIST);
        }

        if (args.get(1).type().equals(NodeType.LIST)) {
            Node values = args.get(1).toList();

            for (int i = 0; i < values.getChildren().size(); i++) {
                Node val = values.getChildren().get(i);
                java.util.List<Node> realizedValues = new ArrayList<>();
                for (Node n : template.getChildren()) {
                    if (n.type().equals(NodeType.SYMBOL) && n.toSymbolWord().getSymbol().equals("?")) {
                        realizedValues.add(val);
                    } else {
                        realizedValues.add(n);
                    }
                }
                List l = new List(realizedValues);
                String runCommand = l.toString().substring(1, l.toString().length() - 1);
                Node result = it.runBounded(it.read(runCommand));

                if (!result.type().equals(NodeType.BOOLEAN)) {
                    throw new NodeTypeException(template, result.type(), NodeType.BOOLEAN);
                }

                if (result.toBooleanWord().getBoolean()) {
                    return val;
                }
            }

        } else if (args.get(1).type().equals(NodeType.QUOTE)) {
            String values = args.get(1).toQuotedWord().getQuote();

            for (int i = 0; i < values.length(); i++) {
                Character val = values.charAt(i);
                java.util.List<Node> realizedValues = new ArrayList<>();
                for (Node n : template.getChildren()) {
                    if (n.type().equals(NodeType.SYMBOL) && n.toSymbolWord().getSymbol().equals("?")) {
                        realizedValues.add(Node.symbol("\"" + String.valueOf(val)));
                    } else {
                        realizedValues.add(n);
                    }
                }
                List l = new List(realizedValues);
                String runCommand = l.toString().substring(1, l.toString().length() - 1);
                Node result = it.runBounded(it.read(runCommand));

                if (!result.type().equals(NodeType.BOOLEAN)) {
                    throw new NodeTypeException(template, result.type(), NodeType.BOOLEAN);
                }

                if (result.toBooleanWord().getBoolean()) {
                    return Node.quote(String.valueOf(val));
                }
            }

        } else {
            throw new NodeTypeException(args.get(1), args.get(1).type(), NodeType.LIST, NodeType.QUOTE);
        }

        return Node.nil();
    }

    @Override
    public Interpreter registerProcedures(Interpreter interpreter) {
        this.it = interpreter;

        it.env().define(new Procedure("map", (scope, val) -> this.map(scope, val), (scope, val) -> this.mapFinished(scope, val), "__template__", "__values__").macro());
        it.env().define(new Procedure("filter", (scope, val) -> this.filter(scope, val), (scope, val) -> Node.none(), "__template__", "__values__"));
        it.env().define(new Procedure("find", (scope, val) -> this.find(scope, val), (scope, val) -> Node.none(), "__template__", "__values__"));

        return it;
    }
}
