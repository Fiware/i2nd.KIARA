/* KIARA - Middleware for efficient and QoS/Security-aware invocation of services and exchange of messages
 *
 * Copyright (C) 2015 Proyectos y Sistemas de Mantenimiento S.L. (eProsima)
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
 */
package org.fiware.kiara.ps.rtps.messages.elements;

import java.io.IOException;

import org.fiware.kiara.serialization.impl.BinaryInputStream;
import org.fiware.kiara.serialization.impl.BinaryOutputStream;
import org.fiware.kiara.serialization.impl.SerializerImpl;
import org.fiware.kiara.ps.rtps.messages.RTPSSubmessageElement;

/**
 * Class representing the field octetsToInlineQos contained 
 * in every RTPS DATA submessage. It holds the number of Bytes
 * remaining until the InlineQos submessage element.
 *
 * @author Rafael Lara {@literal <rafaellara@eprosima.com>}
 */
public class OctectsToInlineQos extends RTPSSubmessageElement {

    /**
     * The OctectsToInlineQos value
     */
    private short m_value;

    /**
     * Default constructor
     * 
     * @param value The value of the field
     */
    public OctectsToInlineQos(short value) {
        this.m_value = value;
    }

    /**
     * Get the value of the OctectsToInlineQos object
     * 
     * @return The number of octets to the InlineQos field
     */
    public short getValue() {
        return this.m_value;
    }

    /**
     * Get the OctectsToInlineQos serialized size
     */
    @Override
    public short getSerializedSize() {
        return 2;
    }

    /**
     * Serializes an OctectsToInlineQos object
     */
    @Override
    public void serialize(SerializerImpl impl, BinaryOutputStream message, String name) throws IOException {
        impl.serializeI16(message, "", this.m_value);
    }

    /**
     * Deserializes an OctectsToInlineQos object
     */
    @Override
    public void deserialize(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
        this.m_value = impl.deserializeI16(message, "");
    }

}
