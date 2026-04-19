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

import static ch.uprisesoft.yali.lexer.TokenType.EOF;
import static ch.uprisesoft.yali.lexer.TokenType.NUMBER;
import java.util.List;

/**
 *
 * @author uprisesoft@gmail.com
 */
public abstract class Scanner {

    protected final Lexer lexer;
    protected final ScannerContext context;

    public Scanner(Lexer lexer, String source) {
        this(lexer, new ScannerContext(source));
    }

    protected Scanner(Lexer lexer, ScannerContext context) {
        this.lexer = lexer;
        this.context = context;
    }

    public List<Token> getTokens() {
        return context.tokens;
    }

    protected ScannerContext getContext() {
        return context;
    }

    public boolean isAtEnd() {
        context.start = context.current;
        boolean end = testEnd();
        if (end == true) {
            context.tokens.add(new Token(EOF, "", context.line, context.linePos, context.current));
        }
        return end;
    }

    public abstract void scanToken();

    protected boolean testEnd() {
        return context.current >= context.source.length();
    }

    protected void string(TokenType type) {
        while (!isNextSpecialChar() && !testEnd()) {
            if (peek() == '\n') {
                context.line++;
            }
            advance();
        }
        addToken(type);
    }

    protected abstract boolean isNextSpecialChar();

    protected boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    protected abstract void symbol();

    protected boolean isAlpha(char c) {
        return Character.isAlphabetic(c) || String.valueOf(c).equals("_") || String.valueOf(c).equals("-") || String.valueOf(c).equals("!") || String.valueOf(c).equals("?");
    }

    protected boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    protected void number() {
        while (isDigit(peek())) {
            advance();
        }

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(NUMBER);
    }

    protected char advance() {
        context.current++;
        context.linePos++;
        return context.source.charAt(context.current - 1);
    }

    protected boolean match(char expected) {
        if (testEnd()) {
            return false;
        }
        if (context.source.charAt(context.current) != expected) {
            return false;
        }

        context.current++;
        return true;
    }

    protected char peek() {
        if (testEnd()) {
            return '\0';
        }
        return context.source.charAt(context.current);
    }

    protected char peekNext() {
        if (context.current + 1 >= context.source.length()) {
            return '\0';
        }
        return context.source.charAt(context.current + 1);
    }

    protected void newLine() {
        addToken(TokenType.NEWLINE);
        context.line++;
        context.linePos = 0;
    }

    protected void addToken(TokenType type) {
        String text = context.source.substring(context.start, context.current);
        context.tokens.add(new Token(type, text, context.line, context.linePos - text.length(), context.current - text.length()));
    }

    protected void addTo(TokenType type) {
        String text = context.source.substring(context.start, context.current);
        context.funStart = new Token(type, text, context.line, context.linePos - text.length(), context.current - text.length());

        context.tokens.add(context.funStart);
    }

    protected void addEnd(TokenType type) {
        String text = context.source.substring(context.funStart.getAbsolute(), context.current);
        int absolutePos = context.current - context.source.substring(context.start, context.current).length();

        context.funEnd = new Token(type, text, context.line, context.linePos - text.length(), absolutePos);

        context.tokens.add(context.funEnd);

        context.funStart = null;
        context.funEnd = null;
    }
}
