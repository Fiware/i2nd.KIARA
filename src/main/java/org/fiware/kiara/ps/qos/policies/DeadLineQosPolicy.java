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

import java.io.IOException;

import org.fiware.kiara.ps.qos.parameter.ParameterId;
import org.fiware.kiara.ps.rtps.messages.elements.Parameter;
import org.fiware.kiara.ps.rtps.messages.elements.Timestamp;
import org.fiware.kiara.serialization.impl.BinaryInputStream;
import org.fiware.kiara.serialization.impl.BinaryOutputStream;
import org.fiware.kiara.serialization.impl.SerializerImpl;

/**
 * Class DeadlineQosPolicy, to indicate the Deadline of the samples. This
 * QosPolicy can be defined and is transmitted to the rest of the network but is
 * not implemented in this version. period: Default value c_TimeInifinite.
 *
 * @author Rafael Lara {@literal <rafaellara@eprosima.com>}
 */
public class DeadLineQosPolicy extends Parameter {

    /**
     * {@link QosPolicy} acting as a parent class
     */
    public QosPolicy parent;

    /**
     * {@link Timestamp} object containing the Deadline period
     */
    public Timestamp period;

    /**
     * Default {@link DeadLineQosPolicy} constructor
     */
    public DeadLineQosPolicy() {
        super(ParameterId.PID_DEADLINE, Parameter.PARAMETER_DEADLINE_QOS_LENGTH);
        this.parent = new QosPolicy(true);
        this.period = new Timestamp().timeInfinite();
    }

    /**
     * This method copies two instnces of {@link DeadLineQosPolicy}
     * @param value The {@link DeadLineQosPolicy} to be copied
     */
    public void copy(DeadLineQosPolicy value) {
        parent.copy(value.parent);
        period.copy(value.period);
    }

    /**
     * Serializes a {@link DeadLineQosPolicy}
     */
    @Override
    public void serialize(SerializerImpl impl, BinaryOutputStream message, String name) throws IOException {
        super.serialize(impl, message, name);
        this.period.serialize(impl, message, name);
    }

    /**
     * Deserializes a {@link DeadLineQosPolicy} and its parent {@link QosPolicy}
     */
    @Override
    public void deserialize(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
        super.deserialize(impl, message, name);
        this.period.deserialize(impl, message, name);
    }

    /**
     * Deserializes only the contents of a {@link DeadLineQosPolicy} (not the {@link QosPolicy} contents)
     */
    @Override
    public void deserializeContent(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
        this.period.deserialize(impl, message, name);
    }

}
