/*
 * Copyright (c) 2017 Nova Ordis LLC
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

package io.novaordis.playground.http.server.rhandler;

import io.novaordis.playground.http.server.http.HttpMethod;
import io.novaordis.playground.http.server.http.HttpRequest;
import io.novaordis.playground.http.server.http.HttpResponse;
import io.novaordis.playground.http.server.http.HttpStatusCode;
import io.novaordis.playground.http.server.http.header.HttpEntityHeader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/5/17
 */
public class EchoRequestHandlerTest extends RequestHandlerTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(EchoRequestHandlerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void constructor() throws Exception {

        EchoRequestHandler h = new EchoRequestHandler(50L);
        assertEquals(50L, h.getDelay().longValue());
    }

    // end-to-end ------------------------------------------------------------------------------------------------------

    @Test
    public void defaultBehavior() throws Exception {

        EchoRequestHandler h = getRequestHandlerToTest();

        assertNull(h.getDelay());

        HttpRequest request = new HttpRequest(HttpMethod.GET, "/something");
        assertNull(request.getQueryParameter("delay"));

        assertTrue(h.accepts(request));

        HttpResponse response = h.processRequest(request);

        assertEquals(HttpStatusCode.OK, response.getStatusCode());
        assertEquals("SYNTHETIC 200 OK\n", new String(response.getBody()));
        assertEquals("" + "SYNTHETIC 200 OK\n".length(),
                response.getHeader(HttpEntityHeader.CONTENT_LENGTH).get(0).getFieldBody());
    }

    // delay -----------------------------------------------------------------------------------------------------------

    @Test
    public void delay_DefaultValue_RequestDoesNotContainOverride() throws Exception {

        EchoRequestHandler h = getRequestHandlerToTest();

        long delayMs = 200L;

        h.setDelay(delayMs);

        HttpRequest request = new HttpRequest(HttpMethod.GET, "/something");
        assertNull(request.getQueryParameter("delay"));

        long t0 = System.currentTimeMillis();
        HttpResponse response = h.processRequest(request);
        long t1 = System.currentTimeMillis();

        assertTrue(t1 - t0 >= delayMs);

        assertEquals(HttpStatusCode.OK, response.getStatusCode());
    }

    @Test
    public void delay_DefaultValue_RequestContainsOverride() throws Exception {

        EchoRequestHandler h = getRequestHandlerToTest();

        log.info("#");
        log.info("# OKRequestHandlerTest.delay_DefaultValue_RequestContainsOverride() will block the test suite if not overridden by the request");
        log.info("#");

        long defaultDelayMs = 1000000L;
        h.setDelay(defaultDelayMs);
        assertEquals(defaultDelayMs, h.getDelay().longValue());

        long delayMs = 200;
        HttpRequest request = new HttpRequest(HttpMethod.GET, "/something?delay=" + delayMs);
        assertEquals("200", request.getQueryParameter("delay"));

        long t0 = System.currentTimeMillis();
        HttpResponse response = h.processRequest(request);
        long t1 = System.currentTimeMillis();

        assertTrue(t1 - t0 >= delayMs);

        assertEquals(HttpStatusCode.OK, response.getStatusCode());
    }

    @Test
    public void delay_NoDefaultValue_RequestContainsDelay() throws Exception {

        EchoRequestHandler h = getRequestHandlerToTest();

        assertNull(h.getDelay());

        long delayMs = 200L;

        HttpRequest request = new HttpRequest(HttpMethod.GET, "/something?delay=" + delayMs);

        long t0 = System.currentTimeMillis();
        HttpResponse response = h.processRequest(request);
        long t1 = System.currentTimeMillis();

        assertTrue(t1 - t0 >= delayMs);

        assertEquals(HttpStatusCode.OK, response.getStatusCode());
    }

    @Test
    public void delay_RequestContainsDelayInvalidDelayValue() throws Exception {

        EchoRequestHandler h = getRequestHandlerToTest();

        HttpRequest request = new HttpRequest(HttpMethod.GET, "/something?delay=blah");

        HttpResponse response = h.processRequest(request);

        assertEquals(HttpStatusCode.BAD_REQUEST, response.getStatusCode());
        String body = new String(response.getBody());
        assertEquals("invalid delay parameter value \"blah\"", body);
    }

    // response code ---------------------------------------------------------------------------------------------------

    @Test
    public void customResponseCode() throws Exception {

        EchoRequestHandler h = getRequestHandlerToTest();

        HttpRequest request = new HttpRequest(HttpMethod.GET, "/something?code=500");

        HttpResponse response = h.processRequest(request);

        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("SYNTHETIC 500 Internal Server Error\n", new String(response.getBody()));
    }

    @Test
    public void invalidCustomResponseCode() throws Exception {

        EchoRequestHandler h = getRequestHandlerToTest();

        HttpRequest request = new HttpRequest(HttpMethod.GET, "/something?code=something");

        HttpResponse response = h.processRequest(request);

        assertEquals(HttpStatusCode.BAD_REQUEST, response.getStatusCode());
        assertEquals("invalid HTTP status code \"something\"", new String(response.getBody()));
    }

    // body length -----------------------------------------------------------------------------------------------------

    @Test
    public void customBodyLength() throws Exception {

        EchoRequestHandler h = getRequestHandlerToTest();

        HttpRequest request = new HttpRequest(HttpMethod.GET, "/something?length=50");

        HttpResponse response = h.processRequest(request);

        assertEquals(HttpStatusCode.OK, response.getStatusCode());
        byte[] body = response.getBody();
        assertEquals(50, body.length);
        assertEquals('\n', body[49]);

        log.info(new String(body));
    }

    @Test
    public void invalidCustomBodyLength() throws Exception {

        EchoRequestHandler h = getRequestHandlerToTest();

        HttpRequest request = new HttpRequest(HttpMethod.GET, "/something?length=blah");

        HttpResponse response = h.processRequest(request);

        assertEquals(HttpStatusCode.BAD_REQUEST, response.getStatusCode());
        assertEquals("invalid body length \"blah\"", new String(response.getBody()));
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected EchoRequestHandler getRequestHandlerToTest() {

        return new EchoRequestHandler();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
