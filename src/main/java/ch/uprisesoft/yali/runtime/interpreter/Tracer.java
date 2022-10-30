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
import ch.uprisesoft.yali.lexer.Token;
import ch.uprisesoft.yali.scope.Environment;
import java.util.List;

/**
 * Use a Tracer if you want to see what's happening while interpreting a yali
 * AST. In every relevant action in the Interpreter, the registered Tracer
 * methods are called. This can be used to build e.g. stack traces or debuggers.
 * 
 * @author uprisesoft@gmail.com
 */
public interface Tracer {
    public void callPrimitive(String name, List<Node> args, Environment env); // OK
    public void call(String name, List<Node> args, Environment env); // OK
    public void schedule(String name, Call call, Environment env); // OK
    public void unschedule(String name, Call call, Environment env); // OK
    public void arg(String name, Node val, Environment env); // OK
    public void make(String name, Node val, Environment env); // OK
    public void thing(String name, Node val, Environment env); // OK
    public void local(String name, Environment env); // OK
    public void scope(String name, Environment env); // OK
    public void unscope(String name, Environment env); // OK
    public void run(Node val); // OK
    public void load(Node val); // OK
    public void tick(Node val);
    public void pause(Node val); // OK
    public void resume(Node val); // OK
}
