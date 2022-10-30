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
package ch.uprisesoft.yali.runtime.interpreter;

import ch.uprisesoft.yali.ast.node.Call;
import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.exception.NodeTypeException;
import ch.uprisesoft.yali.ast.node.NodeType;
import ch.uprisesoft.yali.parser.Parser;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import ch.uprisesoft.yali.runtime.procedures.FunctionNotFoundException;
import ch.uprisesoft.yali.runtime.procedures.builtin.Arithmetic;
import ch.uprisesoft.yali.runtime.procedures.builtin.Control;
import ch.uprisesoft.yali.runtime.procedures.builtin.Data;
import ch.uprisesoft.yali.runtime.procedures.builtin.IO;
import ch.uprisesoft.yali.runtime.procedures.builtin.Logic;
import ch.uprisesoft.yali.runtime.procedures.builtin.Template;
import ch.uprisesoft.yali.scope.Environment;
import ch.uprisesoft.yali.scope.Scope;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class UnthreadedInterpreter implements Interpreter {

    private List<Tracer> tracers = new ArrayList<>();

    private Environment env = new Environment();
    private boolean paused = false;

    private java.util.Stack<Call> stack = new java.util.Stack<>();
    private java.util.List<Call> program = new ArrayList<>();

    private Node lastResult;

    public UnthreadedInterpreter() {
        env.push(new Scope("global"));
    }

    @Override
    public void addTracer(Tracer tracer) {
        tracers.add(tracer);
        env.addTracer(tracer);
    }

    @Override
    public List<Tracer> tracers() {
        return tracers;
    }

    @Override
    public Node lastResult() {
        return lastResult;
    }

    @Override
    public boolean finished() {
        return stack.empty() && program.isEmpty();
    }

    /**
     * Runs a list of procedure calls
     *
     * @param A node which is expected to be of list type. All children will be
     * run first-to-last
     * @return Returns the last evaluated result
     */
    @Override
    public Node run(Node node) {
        tracers.forEach(t -> t.run(node));

        if (!node.type().equals(NodeType.LIST)) {
            throw new NodeTypeException(node, node.type(), NodeType.LIST);
        }

        for (Node n : node.getChildren()) {
            Call call = n.toProcedureCall();
            program.add(call);
        }

        return run();
    }

    /**
     * Runs a single procedure calls
     *
     * @param A valid call object
     * @return Returns the last evaluated result
     */
    @Override
    public Node run(Call call) {
        tracers.forEach(t -> t.run(call));

        load(call);

        return run();
    }

    /**
     * Runs a previously preloaded interpreter. Can be loaded with load()
     *
     * @return Returns the last evaluated result
     */
    @Override
    public Node run() {
        while (tick()) {
        }

        return lastResult;
    }

    @Override
    public void reset() {
        program.clear();
        stack.clear();
    }

    /**
     * Loads a list of procedure calls or other nodes into the interpreter for
     * further evaluation. Only lists or calls are scheduled for evaluation. The
     * value of a reference is stored in the last result, every other type is
     * stored directly in the last result.
     *
     * @param node A preparsed Yali fragment as Node.
     */
    @Override
    public void load(Node node) {
        tracers.forEach(t -> t.load(node));

        switch (node.type()) {
            case LIST:
                for (Node n : node.getChildren()) {
                    Call call = n.toProcedureCall();
                    program.add(call);
                }
                break;
            case PROCCALL:
                stack.push(node.toProcedureCall());
                break;
            case REFERENCE:
                lastResult = env.thing(node.toReferenceWord().getReference());
                break;
            default:
                lastResult = node;
                break;
        }
    }

    /**
     * Resumes evaluation after a pause call.
     *
     * @return Returns the last evaluated result
     */
    @Override
    public Node resume() {
        tracers.forEach(t -> t.resume(stack.peek()));
        paused = false;

        while (tick()) {
        }

        return lastResult;
    }

    /**
     * Pauses the interpreter. This method is intended to be used by native
     * calls only. Use at your own risk. Resume with the resume() Method.
     */
    @Override
    public void pause() {
        tracers.forEach(t -> t.pause(stack.peek()));
        paused = true;
    }

    /**
     * Used to check if the interpreter is paused. Useful e.g. for REPLs
     *
     * @return Pause status of the interpreter. Returns true if paused
     */
    @Override
    public boolean paused() {
        return paused;
    }

    /**
     * Parses an input string to a executable syntax tree
     *
     * @param Yali source code
     * @return the parsed syntax tree, top element is always a list of procedure
     * calls
     */
    @Override
    public Node read(String source) {
        return new Parser(this).read(source);
    }

    /**
     * Parses an input list to a executable syntax tree. This is useful e.g. for
     * control structures or other constructs which run a list as procedure
     * calls
     *
     * @param Yali source code
     * @return the parsed syntax tree, top element is always a list of procedure
     * calls
     */
    @Override
    public Node read(ch.uprisesoft.yali.ast.node.List list) {
        return new Parser(this).read(list);
    }

    /**
     * Returns the environment at this moment with all defined scopes,
     * procedures and variables
     *
     * @return the environment
     */
    @Override
    public Environment env() {
        return env;
    }

    @Override
    public void output(Node node) {
        lastResult = node;
    }

    /**
     * This is the main worker method of the interpreter. It does one atomic
     * step per call. The interpreter primarily is a stack machine, but because
     * there can be multiple top-level commands scheduled, there is also a list
     * for all loaded procedure calls. If the stack is empty, it moves the first
     * Call from the program list to the execution stack. If there is a call on
     * the stack, it checks if it's already evaluated. If yes, it is unscheduled
     * from the stack, the result is bubbled up and then returns. If no, it
     * first checks if there are more arguments to evaluate. If yes, it
     * schedules the next one and returns. If no, the environment of the call is
     * loaded with the arguments and continues to procedure evaluation.
     * Evaluation differs for native calls and user defined calls. Native calls
     * are handled with the BiFunctions defined in call definition, user defined
     * calls have their children evaluated instead.
     *
     * @return true if there is more to do and tick() can be called once more,
     * false otherwise.
     */
    @Override
    public boolean tick() {
        if (stack.isEmpty()) {
            tracers.forEach(t -> t.tick(Node.none()));
        } else {
            tracers.forEach(t -> t.tick(stack.peek()));
        }
        /*
        Global Program state
         */

        if (paused) {
            return false;
        }

        // If the stack is empty, check for more program lines to evaluate
        if (stack.empty()) {
            // If both program and stack are empty, execution is finished or no
            // program was loaded in the first place
            if (program.isEmpty()) {
                return false;
            } else {
                schedule(program.remove(0));
                return true;
            }
        }

        /*
        Result handling
         */
        // Check for finished procedures. A procedure is finished when evaluated()
        // returns true. If stack is 1 and program empty, this
        // is the result. Else, deschedule the call and set the result to the
        // previous call. Has to be done before argument handling.
        if (stack.peek().evaluated()) {
            unschedule();
            if (stack.empty() && !program.isEmpty()) {
                return true;
            } else if (stack.empty() && program.isEmpty()) {
                return false;
            } else if (stack.size() == 1 && stack.peek().evaluated() && program.isEmpty()) {
                return false;
            } else if (stack.peek().hasMoreParameters()) {
                stack.peek().arg(lastResult);
                return true;
            } else {
                return true;
            }
        }

        /*
        Arguments evaluation
         */
        // Arguments are evaluated first. If a call does not have it's argument
        // evaluated, schedule the next argument to be evaluated
        if (stack.peek().hasMoreParameters()) {
            Node nextParam = stack.peek().nextParameter();
            tracers.forEach(t -> t.arg(stack.peek().getName(), nextParam, env));

            // If it's not a procedure call, no evaluation is necessary. Add to
            // arguments as-is.
            if (!nextParam.type().equals(NodeType.PROCCALL)) {
                stack.peek().arg(nextParam);
                return true;
            } else {
                schedule(nextParam.toProcedureCall());
                return true;
            }
        }

        /*
        Procedure evaluation
         */
        Call call = stack.peek();

        // Prepare env
        for (int i = 0; i < call.definition().getArity(); i++) {
            env.local(call.definition().getArgs().get(i));
            env.make(
                    call.definition().getArgs().get(i),
                    call.args().get(i)
            );
        }

        if (call.definition().isNative()) {
            tracers.forEach(t -> t.callPrimitive(call.getName(), call.args(), env));

            // With a native call, the first BiFunction is applied. It should 
            // return a result if it's a pure procedure or Node.boolean(true) if
            // there are more steps necessary
            Node result = call.definition().getNativeCall().apply(env.peek(), call.args());

            // If the BiFunction callback for checking for more work returns true,
            // the procedure will stay scheduled. As soon as it returns false, the
            // call is marked as finished and descheduled in the next tick()
            if (!nodeIsTrue(call.definition().getHasMoreCallback().apply(env.peek(), result))) {
                call.result(result, env.peek());
                call.evaluated(true);
            }
            return true;
        } else {
            // Handling of user-defined procedure calls. If the procedure has more 
            // calls in it's children list, the next one is scheduled. 
            tracers.forEach(t -> t.call(call.getName(), call.args(), env));
            if (call.hasMoreCalls()) {
                schedule(call.nextCall());
            } else {
                // A user derfined procedure call is evaluated as soon as it has no 
                // more procedure calls in it's children list. 
                call.evaluated(true);
                call.result(lastResult, env.peek());
            }
            return true;
        }
    }

    private boolean nodeIsTrue(Node node) {
        return node.type().equals(NodeType.BOOLEAN) && node.toBooleanWord().getBoolean();
    }

    private Call unschedule() {
        Call call = stack.pop();

        if (!call.definition().isMacro()) {
            tracers.forEach(t -> t.unscope(env.peek().name(), env));
            env.pop();
        }
        lastResult = call.result();
        tracers.forEach(t -> t.unschedule(call.getName(), call, env));
        return call;
    }

    @Override
    public void schedule(Call call) {
        tracers.forEach(t -> t.schedule(call.getName(), call, env));

        if (!env.defined(call.getName())) {
            throw new FunctionNotFoundException(call.getName());
        }

        call.definition(env.procedure(call.getName()));
        call.reset();
        stack.push(call);
        if (!call.definition().isMacro()) {
            env.push(new Scope(call.getName()));
            tracers.forEach(t -> t.scope(env.peek().name(), env));
        }
    }

    @Override
    public Interpreter loadStdLib() {

        Logic logic = new Logic();
        logic.registerProcedures(this);

        Control control = new Control();
        control.registerProcedures(this);

        Arithmetic arithmetic = new Arithmetic();
        arithmetic.registerProcedures(this);

        Template template = new Template();
        template.registerProcedures(this);

        Data data = new Data();
        data.registerProcedures(this);

        return this;
    }

    @Override
    public Interpreter loadStdLib(OutputObserver oo, InputGenerator ig) {
        IO com = new IO();
        com.register(oo);
        com.register(ig);
        com.registerProcedures(this);

        return loadStdLib();
    }

    @Override
    public java.util.List<String> stringify(java.util.List<Node> args) {
        java.util.List<String> stringifiedArgs = new ArrayList<>();
        for (Node arg : args) {
            if (arg.type().equals(NodeType.LIST)) {
                stringifiedArgs.addAll(stringify(arg.getChildren()));
            } else {
                stringifiedArgs.add(arg.toString());
            }
        }
        return stringifiedArgs;
    }

    /**
     * Observer and helper methods
     */
    @Override
    public void inform(String output) {
    }
}
