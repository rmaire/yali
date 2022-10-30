/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
public class ThreadedInterpreter implements Interpreter {
    
    private final Interpreter interpreter;

    private Thread interpreterRunner;
    

    public ThreadedInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public synchronized void addTracer(Tracer tracer) {
        interpreter.addTracer(tracer);
    }

    @Override
    public synchronized Environment env() {
        return interpreter.env();
    }

    @Override
    public synchronized void inform(String output) {
        interpreter.inform(output);
    }

    @Override
    public synchronized void load(Node node) {
        interpreter.load(node);
    }

    @Override
    public synchronized Interpreter loadStdLib() {
        return interpreter.loadStdLib();
    }

    @Override
    public synchronized Interpreter loadStdLib(OutputObserver oo, InputGenerator ig) {
        return interpreter.loadStdLib(oo, ig);
    }

    @Override
    public synchronized void output(Node node) {
        interpreter.output(node);
    }

    @Override
    public synchronized void pause() {
        interpreter.pause();
    }

    @Override
    public synchronized boolean paused() {
        return interpreter.paused();
    }

    @Override
    public synchronized Node read(String source) {
        return interpreter.read(source);
    }

    @Override
    public synchronized Node read(List list) {
        return interpreter.read(list);
    }

    @Override
    public synchronized void reset() {
        interpreter.reset();
    }

    @Override
    public synchronized Node resume() {
        return interpreter.resume();
    }

    @Override
    public synchronized Node run(Node node) {
        interpreterRunner = new Thread(() -> interpreter.run(node));
        interpreterRunner.start();
        return Node.none();
    }

    @Override
    public synchronized Node run(Call call) {
        interpreterRunner = new Thread(() -> interpreter.run(call));
        interpreterRunner.start();
        return Node.none();
    }

    @Override
    public synchronized Node run() {
        interpreterRunner = new Thread(() -> interpreter.run());
        interpreterRunner.start();
        return Node.none();
    }

    @Override
    public synchronized void schedule(Call call) {
        interpreter.schedule(call);
    }

    @Override
    public synchronized java.util.List<String> stringify(java.util.List<Node> args) {
        return interpreter.stringify(args);
    }

    @Override
    public synchronized boolean tick() {
        return interpreter.tick();
    }

    @Override
    public synchronized java.util.List<Tracer> tracers() {
        return interpreter.tracers();
    }

    @Override
    public Node lastResult() {
        return interpreter.lastResult();
    }

    @Override
    public boolean finished() {
        return interpreter.finished() && !interpreterRunner.isAlive();
    }
}
