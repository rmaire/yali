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

import ch.uprisesoft.yali.lexer.TokenType;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class UnexpectedCharacterException  extends RuntimeException {
    
    private final String character;
    private final int line;
    private final int pos;
    
    public UnexpectedCharacterException(String character, int line, int pos) {
        super("Unexpected character " + character + " at " + pos + "/" + line);
        this.character = character;
        this.line = line;
        this.pos = pos;
    }

    public String getCharacter() {
        return character;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    
}