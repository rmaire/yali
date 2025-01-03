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
package ch.uprisesoft.yali.repl;

import ch.uprisesoft.yali.ast.node.Call;
import ch.uprisesoft.yali.ast.node.Node;
import ch.uprisesoft.yali.runtime.interpreter.UnthreadedInterpreter;
import ch.uprisesoft.yali.runtime.interpreter.Tracer;
import ch.uprisesoft.yali.scope.Environment;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class PrintingTracer implements Tracer {
    
    private final UnthreadedInterpreter it;
    private final String prefix;

    public PrintingTracer(UnthreadedInterpreter it, String prefix) {
        this.it = it;
        this.prefix = prefix;
    }

    @Override
    public void callPrimitive(String name, List<Node> args, Environment env) {
        System.out.println(lpad() + "Calling primitive Procedure " + name + ": " + args.toString());
//        System.out.println("Trace:");
//        System.out.println(env.trace());
    }

    @Override
    public void call(String name, List<Node> args, Environment env) {
        System.out.println(lpad() + "Calling Procedure " + name + ": " + args.toString());
//        System.out.println("Trace:");
//        System.out.println(env.trace());
    }

    @Override
    public void make(String name, Node val, Environment env) {
        System.out.println(lpad() + "Defining Variable " + name + " in scope " + env.peek().name() + " with value " + val.toString());
    }

    @Override
    public void thing(String name, Node val, Environment env) {
        System.out.println(lpad() + "Resolving Variable " + name + " in scope " + env.peek().name() + " with value " + val.toString());
    }

    @Override
    public void local(String name, Environment env) {
        System.out.println(lpad() + "Local Variable " + name + " in scope " + env.peek().name());
    }

    @Override
    public void run(Node val) {
        System.out.println(lpad() + "Run: " + val.toString());
    }

    @Override
    public void tick(Node val) {
        System.out.println(lpad() + "tick: " + val.toString());
    }

    @Override
    public void pause(Node val) {
        System.out.println(lpad() + "Paused, current call: " + val.toString());
    }

    @Override
    public void resume(Node val) {
        if (val != null) {
            System.out.println(lpad() + "Resumed, current call: " + val.toString());
        } else {
            System.out.println(lpad() + "Resumed");
        }
    }

    @Override
    public void scope(String name, Environment env) {
        System.out.println(lpad() + "Open environment " + name);
    }

    @Override
    public void unscope(String name, Environment env) {
        System.out.println(lpad() + "Close environment " + name);
    }

    @Override
    public void arg(String name, Node val, Environment env) {
        System.out.println(lpad() + "Evaluating argument for Call " + name + " -> " + val);
    }

    @Override
    public void schedule(String name, Call call, Environment env) {
        System.out.println(lpad() + "Scheduling " + name + ": " + call);
    }

    @Override
    public void unschedule(String name, Call call, Environment env) {
        System.out.println(lpad() + "Unscheduling " + name + ": " + call);
    }

    @Override
    public void load(Node val) {
        System.out.println(lpad() + "Loading " + val);
    }
    
    private String lpad(){
        String lpad = prefix + String.join("", Collections.nCopies(it.env().size(), ">")) + " ";
        return lpad;        
    }
}
