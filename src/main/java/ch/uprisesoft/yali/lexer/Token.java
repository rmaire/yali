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
package ch.uprisesoft.yali.lexer;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class Token {

    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int pos;
    private final int absolute;

    public Token(TokenType type, String lexeme, int line, int pos, int absolute) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.pos = pos;
        this.absolute = absolute;
    }

    public Token copy(){
        return new Token(this.type, this.lexeme, this.line, this.pos, this.absolute);
    }

    public TokenType type() {
        return this.type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    public int getAbsolute() {
        return absolute;
    }

    public String toString() {
        return type + "[" + lexeme + ";" + pos + ";" + line + "]" ;
    }
}
