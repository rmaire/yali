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
import ch.uprisesoft.yali.scope.Environment;
import java.util.List;

/**
 * Use a Tracer if you want to see what's happening while interpreting a yali
 * AST. In every relevant action in the Interpreter, the registered Tracer
 * methods are called. This can be used to build e.g. stack tracers or debuggers.
 * 
 * @author uprisesoft@gmail.com
 */
public interface Tracer {
    public void parse(String source);
    public void start(Node node);
    public void callPrimitive(String name, List<Node> args, Environment env);
    public void call(String name, List<Node> args, Environment env);
    public void schedule(String name, Call call, Environment env);
    public void unschedule(String name, Call call, Environment env);
    public void arg(String name, Node val, Environment env);
    public void make(String name, Node val, Environment env);
    public void thing(String name, Node val, Environment env);
    public void local(String name, Environment env);
    public void scope(String name, Environment env);
    public void unscope(String name, Environment env);
    public void run(Node val);
    public void load(Node val);
    public void tick(Node val);
    public void returnTick(Node val, String pos);
    public void apply(Node val);
    public void pause(Node val);
    public void resume(Node val);
}
