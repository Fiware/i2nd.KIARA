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
package org.fiware.kiara.ps.qos.policies;

/**
 * Enum OwnershipQosPolicyKind, different kinds of destination order for
 * DestinationOrderQosPolicy.
 *
 * @author Rafael Lara {@literal <rafaellara@eprosima.com>}
 */
public enum DestinationOrderQosPolicyKind {

    /**
     * By Reception Timestamp, default value.
     */
    BY_RECEPTION_TIMESTAMP_DESTINATIONORDER_QOS((byte) 0),
    /**
     * By Source Timestamp.
     */
    BY_SOURCE_TIMESTAMP_DESTINATIONORDER_QOS((byte) 1);

    /**
     * Enumeration value
     */
    private byte m_value;
    
    /**
     * Provate constructor
     * 
     * @param value Enumeratio value
     */
    private DestinationOrderQosPolicyKind(byte value) {
        this.m_value = value;
    }

    /**
     * Get the enumeration value
     * 
     * @return The enumeration value
     */
    public byte getValue() {
        return this.m_value;
    }
    
    /**
     * Creates a new {@link DestinationOrderQosPolicyKind} from its value
     * 
     * @param value The enumeration value
     * @return A new {@link DestinationOrderQosPolicyKind} with the introduced parameter value set
     */
    public static DestinationOrderQosPolicyKind createFromValue(int value) {
        switch(value) {
        case 0:
            return DestinationOrderQosPolicyKind.BY_RECEPTION_TIMESTAMP_DESTINATIONORDER_QOS;
        case 1:
            return DestinationOrderQosPolicyKind.BY_SOURCE_TIMESTAMP_DESTINATIONORDER_QOS;
        default:
            return DestinationOrderQosPolicyKind.BY_RECEPTION_TIMESTAMP_DESTINATIONORDER_QOS;  
        }
    }
}
