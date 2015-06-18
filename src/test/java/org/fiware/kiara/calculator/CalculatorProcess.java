 /* KIARA - Middleware for efficient and QoS/Security-aware invocation of services and exchange of messages
 *
 * Copyright (C) 2014 Proyectos y Sistemas de Mantenimiento S.L. (eProsima)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * @file CalculatorProcess.java
 * This file contains the main asynchronous interface for the defined operations.
 *
 * This file was generated by using the tool Kiaragen.
 *
 */
 
 
package org.fiware.kiara.calculator;

import org.fiware.kiara.transport.impl.TransportMessage;
import org.fiware.kiara.client.AsyncCallback;
import org.fiware.kiara.serialization.impl.BinaryInputStream;
import org.fiware.kiara.serialization.impl.SerializerImpl;

/**
 * Interface containing the process utilities.
 *
 * @author Kiaragen tool.
 *
 */
public class CalculatorProcess {

	public static void add_processAsync(final TransportMessage message, final SerializerImpl ser, final AsyncCallback<Integer> callback) {
	    try {
	        final BinaryInputStream bis = BinaryInputStream.fromByteBuffer(message.getPayload());
	        // Deserialize message ID
	        final Object messageId = ser.deserializeMessageId(bis);
	        // Deserialize return code (0 = OK, anything else = WRONG)
	        int retCode = ser.deserializeUI32(bis, "");
	        if (retCode == 0) { // Function execution was OK.
	            int result = ser.deserializeI32(bis, "");
	            callback.onSuccess(result);
	        }
	    } catch (Exception ex) {
	        callback.onFailure(ex);
	    }
	}

	public static void subtract_processAsync(final TransportMessage message, final SerializerImpl ser, final AsyncCallback<Integer> callback) {
	    try {
	        final BinaryInputStream bis = BinaryInputStream.fromByteBuffer(message.getPayload());
	        // Deserialize message ID
	        final Object messageId = ser.deserializeMessageId(bis);
	        // Deserialize return code (0 = OK, anything else = WRONG)
	        int retCode = ser.deserializeUI32(bis, "");
	        if (retCode == 0) { // Function execution was OK.
	            int result = ser.deserializeI32(bis, "");
	            callback.onSuccess(result);
	        }
	    } catch (Exception ex) {
	        callback.onFailure(ex);
	    }
	}


}