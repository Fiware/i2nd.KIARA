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
import org.fiware.kiara.serialization.impl.BinaryInputStream;
import org.fiware.kiara.serialization.impl.BinaryOutputStream;
import org.fiware.kiara.serialization.impl.Serializable;
import org.fiware.kiara.serialization.impl.SerializerImpl;

/**
 * Class ResourceLimitsQosPolicy, defines the ResourceLimits for the Writer or
 * the Reader. max_samples: Default value 5000. max_instances: Default value 10.
 * max_samples_per_instance: Default value 400. allocated_samples: Default value
 * 3000.
 *
 * @author Rafael Lara {@literal <rafaellara@eprosima.com>}
 */
public class ResourceLimitsQosPolicy extends Parameter implements Serializable {

    /**
     * {@link QosPolicy} acting as a parent class
     */
    public QosPolicy parent;

    /**
     * Maximum number of samples of an instance
     */
    public int maxSamples;

    /**
     * Maximum number of different instances
     */
    public int maxInstances;

    /**
     * Maximum number of sample of the same instance
     */
    public int maxSamplesPerInstance;

    /**
     * Total number of allocated samples
     */
    public int allocatedSamples;

    /**
     * Default {@link ResourceLimitsQosPolicy} constructor
     */
    public ResourceLimitsQosPolicy() {
        super(ParameterId.PID_RESOURCE_LIMITS, (short) (Parameter.PARAMETER_KIND_LENGTH + 4 + 4));
        this.parent = new QosPolicy(false);
        this.maxSamples = 5000;
        this.maxInstances = 10;
        this.maxSamplesPerInstance = 400;
        this.allocatedSamples = 3000;
    }

    /**
     * Serializes a {@link ResourceLimitsQosPolicy}
     */
    @Override
    public void serialize(SerializerImpl impl, BinaryOutputStream message, String name) throws IOException {
        super.serialize(impl, message, name);
        impl.serializeI32(message, name, this.maxSamples);
        impl.serializeI32(message, name, this.maxInstances);
        impl.serializeI32(message, name, this.maxSamplesPerInstance);
    }

    /**
     * Deserializes a {@link ResourceLimitsQosPolicy} and its parent {@link QosPolicy}
     */
    @Override
    public void deserialize(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
        super.deserialize(impl, message, name);
        this.maxSamples = impl.deserializeI32(message, name);
        this.maxInstances = impl.deserializeI32(message, name);
        this.maxSamplesPerInstance = impl.deserializeI32(message, name);
    }

    /**
     * Deserializes only the contents of a {@link ResourceLimitsQosPolicy} (not the {@link QosPolicy} contents)
     */
    @Override
    public void deserializeContent(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
        this.maxSamples = impl.deserializeI32(message, name);
        this.maxInstances = impl.deserializeI32(message, name);
        this.maxSamplesPerInstance = impl.deserializeI32(message, name);
    }

}
