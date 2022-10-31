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

import static ch.uprisesoft.yali.lexer.TokenType.LEFT_BRACKET;
import static ch.uprisesoft.yali.lexer.TokenType.RIGHT_BRACKET;
import java.util.List;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class ListScanner extends Scanner {

    private int listDepth = 1;
    

    public ListScanner(Lexer context, String source) {
        super(context, source);
    }

    protected ListScanner(Lexer context, String source, List<Token> tokens, int start, int current, int line, int linePos, Token funStart, Token funEnd, int parenDepth,int braceDepth) {
        super(context, source, tokens, start, current, line, linePos, funStart, funEnd, parenDepth, braceDepth);
    }

    @Override
    public void scanToken() {
        char c = advance();
        switch (c) {
            case '[':
                addToken(LEFT_BRACKET);
                listDepth++;
                break;
            case ']':
                addToken(RIGHT_BRACKET);
                listDepth--;
                if (listDepth < 1) {
                    context.setScanner(new BaseScanner(context, source, tokens, start, current, line, linePos, funStart, funEnd, parenDepth, braceDepth));
                }
                break;
            case ' ':
                break;
            default:
                symbol();
                break;
        }
    }

    @Override
    protected boolean isNextSpecialChar() {
        return peek() == ' '
                || peek() == ']'
                || testEnd();
    }

    @Override
    protected void symbol() {
        while (!isNextSpecialChar()) {
            advance();
        }
        TokenType type = TokenType.SYMBOL;
        addToken(type);
    }
}
