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
package ch.uprisesoft.yali.scope;

import ch.uprisesoft.yali.ast.node.Node;
import java.util.HashMap;
import java.util.Map;

/**
 * A scope contains all variables of a program part. Scopes are nested and local 
 * to a procedure call. This enables dynamic scoping. there are occurrences where a
 * procedure call is evaluated in the scope of it's caller. Those procedures
 * are called macros. Macro handling is done at interpreter level, the scope
 * itself is not aware of the procedure call it closes over.
 * 
 * A scope is normally named after the procedure it closes over.
 * 
 * @author uprisesoft@gmail.com
 */
public class Scope {

    private String scopeName = "";
    private Map<String, Node> members = new HashMap<>();

    public Scope(String scopeName) {
        this.scopeName = scopeName;
    }

    public String name() {
        return scopeName;
    }

    /**
     * Gets the value defined with this name. In yali code this is equivalent to
     * thing "name or :name. Always check with thingable() first if the variable 
     * is defined. 
     * @param name the name of the variable
     * @return the value defined with the variable name
     */
    public Node thing(String name) {
        if (members.containsKey(name.toLowerCase())) {
            return members.get(name.toLowerCase());
        } 

        // Shouldn't happen
        return Node.none();
    }

    /**
     * Bind a variable name to a value in this scope.
     * @param name the name of the variable
     * @param value the value of the variable
     */
    public void make(String name, Node value) {
        members.put(name.toLowerCase(), value);
    }
    
    public void unmake(String name) {
        members.remove(name.toLowerCase());
    }

    /**
     * Reserve a variable name in this scope. This ensures that the variable isn't 
     * defined in a higher scope.
     * @param name the name of the variable
     */
    public void local(String name) {
        members.put(name.toLowerCase(), Node.none());
    }

    /**
     * Check if a variable is defined in this scope.
     * @param name the variable name to check
     * @return true if defined, false otherwise
     */
    public boolean thingable(String name) {
        return members.containsKey(name);
    }

}
