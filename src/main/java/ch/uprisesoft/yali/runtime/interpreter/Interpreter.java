/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ch.uprisesoft.yali.runtime.interpreter;

import ch.uprisesoft.yali.ast.node.Call;
import ch.uprisesoft.yali.ast.node.List;
import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import ch.uprisesoft.yali.scope.Environment;

/**
 *
 * @author roman
 */
public interface Interpreter extends OutputObserver {

    public void addTracer(Tracer tracer);

    /**
     * Returns the environment at this moment with all defined scopes,
     * procedures and variables
     *
     * @return the environment
     */
    public Environment env();

    /**
     * Observer and helper methods
     */
    public void inform(String output);

    /**
     * Loads a list of procedure calls or other nodes into the interpreter for
     * further evaluation. Only lists or calls are scheduled for evaluation. The
     * value of a reference is stored in the last result, every other type is
     * stored directly in the last result.
     *
     * @param node A node which is expected to be of list type.
     */
    public void load(Node node);

    public Interpreter loadStdLib();

    public Interpreter loadStdLib(OutputObserver oo, InputGenerator ig);

    public void output(Node node);

    /**
     * Pauses the interpreter. This method is intended to be used by native
     * calls only. Use at your own risk. Resume with the resume() Method.
     */
    public void pause();

    /**
     * Used to check if the interpreter is paused. Useful e.g. for REPLs
     *
     * @return Pause status of the interpreter. Returns true if paused
     */
    public boolean paused();

    /**
     * Parses an input string to a executable syntax tree
     *
     * @param Yali source code
     * @return the parsed syntax tree, top element is always a list of procedure
     * calls
     */
    public Node read(String source);

    /**
     * Parses an input list to a executable syntax tree. This is useful e.g. for
     * control structures or other constructs which run a list as procedure
     * calls
     *
     * @param Yali source code
     * @return the parsed syntax tree, top element is always a list of procedure
     * calls
     */
    public Node read(List list);

    public void reset();

    /**
     * Resumes evaluation after a pause call.
     *
     * @return Returns the last evaluated result
     */
    public Node resume();

    /**
     * Runs a list of procedure calls
     *
     * @param A node which is expected to be of list type. All children will be
     * run first-to-last
     * @return Returns the last evaluated result
     */
    public Node run(Node node);

    /**
     * Runs a single procedure calls
     *
     * @param A valid call object
     * @return Returns the last evaluated result
     */
    public Node run(Call call);

    /**
     * Runs a previously preloaded interpreter. Can be loaded with load()
     *
     * @return Returns the last evaluated result
     */
    public Node run();

    /**
     * Puts a Procedure call on the stack to be evaluated next. Also, loads a
     * new Scope into the Environment
     * 
     * @param a Procedure call 
     */

    public void schedule(Call call);

    public java.util.List<String> stringify(java.util.List<Node> args);

    /**
     * This is the main worker method of the interpreter. It does one atomic
     * step per call. The interpreter primarily is a stack machine, but because
     * there can be multiple top-level commands scheduled, there is also a list
     * for all loaded procedure calls. If the stack is empty, it moves the first
     * Call from the program list to the execution stack. If there is a call on
     * the stack, it checks if it's already evaluated. If yes, it is unscheduled
     * from the stack, the result is bubbled up and then returns. If no, it first
     * checks if there are more arguments to evaluate. If yes, it schedules the
     * next one and returns. If no, the environment of the call is loaded with
     * the arguments and continues to procedure evaluation. Evaluation differs
     * for native calls and user defined calls. Native calls are handled with
     * the BiFunctions defined in call definition, user defined calls have their
     * children evaluated instead.
     *
     * @return true if there is more to do and tick() can be called once more,
     * false otherwise.
     */
    public boolean tick();

    public java.util.List<Tracer> tracers();

    public Node lastResult();

    public boolean finished();
    
}
