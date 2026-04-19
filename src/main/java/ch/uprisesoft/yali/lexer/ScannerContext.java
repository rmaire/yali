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

import java.util.ArrayList;
import java.util.List;

/**
 * Shared mutable scanner state used when switching scanner implementations.
 */
public class ScannerContext {

    protected final String source;
    protected final List<Token> tokens;

    protected int start = 0;
    protected int current = 0;
    protected int line = 1;
    protected int linePos = 0;

    protected int parenDepth = 0;
    protected int braceDepth = 0;

    protected Token funStart;
    protected Token funEnd;

    public ScannerContext(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
    }
}

