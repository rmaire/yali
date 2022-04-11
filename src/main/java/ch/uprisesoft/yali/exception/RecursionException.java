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
package ch.uprisesoft.yali.exception;

import ch.uprisesoft.yali.ast.node.Node;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class RecursionException extends RuntimeException {
    
    private String function;
    private Node node;

    public RecursionException(String function, Node node) {
        super("Too many recursions in procedure " + function);
        this.function = function;
        this.node = node;
    }

    public String getFunction() {
        return function;
    }
    
    public Node getNode() {
        return node;
    }
    
}
