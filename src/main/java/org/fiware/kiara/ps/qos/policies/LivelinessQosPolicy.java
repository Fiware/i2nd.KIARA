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
import org.fiware.kiara.serialization.impl.SerializerImpl;

/**
 * Class LivelinessQosPolicy, to indicate the Liveliness of the Writers. This
 * QosPolicy can be defined for the Subscribers and is transmitted but only the
 * Writer Liveliness protocol is implemented in this version. The user should
 * set the lease_duration and the announcement_period with values that differ in
 * at least 30%. Values too close to each other may cause the failure of the
 * writer liveliness assertion in networks with high latency or with lots of
 * communication errors. kind: Default value AUTOMATIC_LIVELINESS_QOS
 * lease_duration: Default value c_TimeInfinite. announcement_period: Default
 * value c_TimeInfinite (must be < lease_duration).
 *
 * @author Rafael Lara {@literal <rafaellara@eprosima.com>}
 */
public class LivelinessQosPolicy extends Parameter {

    public QosPolicy parent;

    public LivelinessQosPolicyKind kind;

    public Timestamp leaseDuration;

    public Timestamp announcementPeriod;

    public LivelinessQosPolicy() {
        super(ParameterId.PID_LIVELINESS, (short) (Parameter.PARAMETER_KIND_LENGTH + Parameter.PARAMETER_KIND_LENGTH));
        this.parent = new QosPolicy(true);
        this.kind = LivelinessQosPolicyKind.AUTOMATIC_LIVELINESS_QOS;
        this.leaseDuration = new Timestamp().timeInfinite();
        this.announcementPeriod = new Timestamp().timeInfinite();
    }

    public void copy(LivelinessQosPolicy value) {
        parent.copy(value.parent);
        kind = value.kind;
        leaseDuration.copy(value.leaseDuration);
        announcementPeriod.copy(value.announcementPeriod);
    }

    // TODO
    @Override
    public void deserializeContent(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
        // Do nothing
    }
}
