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
package ch.uprisesoft.yali.helper;

import ch.uprisesoft.yali.lexer.Lexer;
import ch.uprisesoft.yali.parser.Parser;
import ch.uprisesoft.yali.runtime.procedures.builtin.MockTurtleManager;
import ch.uprisesoft.yali.runtime.interpreter.UnthreadedInterpreter;
import ch.uprisesoft.yali.runtime.io.InputGenerator;
import ch.uprisesoft.yali.runtime.io.OutputObserver;
import ch.uprisesoft.yali.scope.Scope;

/**
 *
 * @author uprisesoft@gmail.com
 */
public class ObjectMother {

    public UnthreadedInterpreter getInterpreter() {
		OutputObserver oo = new OutputObserver() {

			@Override
			public void inform(String output) {
				System.out.println(output);
			}
		};

		InputGenerator ig = new InputGenerator() {
			@Override
			public String request() {
				return "requestedinput";
			}

			@Override
			public String requestLine() {
				return "requestedinputline";
			}
		};

		UnthreadedInterpreter i = new UnthreadedInterpreter();
		i.loadStdLib(oo, ig);

		MockTurtleManager mtm = new MockTurtleManager();
		mtm.registerProcedures(i);

		Parser p = new Parser(i);

		return i;
    }

	public UnthreadedInterpreter getInterpreter(OutputObserver oo ,InputGenerator ig) {
		UnthreadedInterpreter i = new UnthreadedInterpreter();
		i.loadStdLib(oo, ig);

		MockTurtleManager mtm = new MockTurtleManager();
		mtm.registerProcedures(i);

		Parser p = new Parser(i);

		return i;
	}

	public UnthreadedInterpreter fork(UnthreadedInterpreter original, OutputObserver oo ,InputGenerator ig) {
		UnthreadedInterpreter fork = new UnthreadedInterpreter();

		fork.env().first(original.env().first());
		//fork.loadStdLib(oo, ig);

		//MockTurtleManager mtm = new MockTurtleManager();
		//mtm.registerProcedures(fork);

		Parser p = new Parser(fork);

		return fork;
	}
}
