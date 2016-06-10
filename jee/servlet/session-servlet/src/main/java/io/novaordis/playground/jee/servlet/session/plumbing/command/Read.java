/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.playground.jee.servlet.session.plumbing.command;

import io.novaordis.playground.jee.servlet.session.plumbing.Console;
import io.novaordis.playground.jee.servlet.session.plumbing.Context;
import io.novaordis.playground.jee.servlet.session.plumbing.HttpException;

import javax.servlet.http.HttpSession;
import java.util.StringTokenizer;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/9/16
 */
public class Read extends CommandBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String attributeName;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param argumentPath the path that follows after the "/read"
     */
    public Read(Context context, String argumentPath) throws HttpException {

        super(context);
        extractAttributeName(argumentPath);
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void execute() throws Exception {

        HttpSession session = getContext().getSession();
        Console console = getContext().getConsole();

        if (session == null) {

            console.warn("no active session, can't read");
            return;
        }

        Object value = session.getAttribute(attributeName);

        String msg = "" + value;

        console.info(msg);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void extractAttributeName(String path) throws HttpException {

        StringTokenizer st = new StringTokenizer(path, "/");

        if (!st.hasMoreTokens()) {
            throw new HttpException(400, "invalid read URL: the name of the session attribute must follow /read/");
        }

        this.attributeName = st.nextToken();

    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
