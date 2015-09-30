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
package org.fiware.kiara.ps;

import java.util.ArrayList;
import java.util.List;

import org.fiware.kiara.ps.attributes.ParticipantAttributes;
import org.fiware.kiara.ps.attributes.PublisherAttributes;
import org.fiware.kiara.ps.attributes.SubscriberAttributes;
import org.fiware.kiara.ps.participant.Participant;
import org.fiware.kiara.ps.participant.ParticipantListener;
import org.fiware.kiara.ps.publisher.Publisher;
import org.fiware.kiara.ps.publisher.PublisherListener;
import org.fiware.kiara.ps.rtps.RTPSDomain;
import org.fiware.kiara.ps.rtps.participant.RTPSParticipant;
import org.fiware.kiara.ps.subscriber.Subscriber;
import org.fiware.kiara.ps.subscriber.SubscriberListener;
import org.fiware.kiara.ps.topic.TopicDataType;
import org.fiware.kiara.serialization.impl.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Domain, use to interact with the Publisher Subscriber API of the Fast
 * RTPS implementation.
 *
 * @author Rafael Lara {@literal <rafaellara@eprosima.com>}
 */
public class Domain {

    private static final List<Participant> m_participants = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(Domain.class);

    /**
     * Stop and remove all participants and publishers and subscribers in this
     * Domain.
     */
    public static synchronized void stopAll() {
        while (m_participants.size() > 0) {
            Domain.removeParticipant(m_participants.get(0));
        }
    }

    /**
     * Remove a Participant and all associated publishers and subscribers.
     *
     * @param part reference to the participant.
     * @return true if correctly removed.
     */
    public static synchronized boolean removeParticipant(Participant part) {
        if (part != null) {
            for (Participant current : m_participants) {
                if (current.getGuid().equals(part.getGuid())) {
                    // Found
                    current.destroy();
                    m_participants.remove(current);
                    logger.info("Participant successfully removed"); // TODO Not in C++ implementation. Log every removal.
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove a Publisher.
     *
     * @param pub reference to the {@link Publisher}.
     * @return true if correctly removed.
     */
    public static synchronized boolean removePublisher(Publisher<?> pub) {
        if (pub != null) {
            for (Participant it : m_participants) {
                if (it.getGuid().getGUIDPrefix().equals(pub.getGuid().getGUIDPrefix())) {
                    // Found
                    return it.removePublisher(pub);
                }
            }
        }
        return false;
    }

    /**
     * Remove a Subscriber.
     *
     * @param sub reference to the {@link Subscriber}.
     * @return true if correctly removed.
     */
    public static synchronized boolean removeSubscriber(Subscriber<?> sub) {
        if (sub != null) {
            for (Participant it : m_participants) {
                if (it.getGuid().getGUIDPrefix().equals(sub.getGuid().getGUIDPrefix())) {
                    // Found
                    return it.removeSubscriber(sub);
                }
            }
        }
        return false;
    }

    /**
     * Create a Participant.
     *
     * @param att Participant attributes.
     * @param listener reference to {@link ParticipantListener}.
     * @return reference to {@link Participant}. (null if not created.)
     */
    public static synchronized Participant createParticipant(ParticipantAttributes att, ParticipantListener listener) {
        Participant pubSubParticipant = new Participant(att, listener);

        RTPSParticipant part = RTPSDomain.createParticipant(att.rtps, pubSubParticipant.getListener());

        if (part == null) {
            logger.error("Problem creating RTPSParticipant");
            return null;
        }

        pubSubParticipant.setRTPSParticipant(part);
        m_participants.add(pubSubParticipant);
        return pubSubParticipant;
    }

    /**
     * Create a Publisher in a Participant.
     *
     * @param <T>
     * @param part reference to the participant where you want to create the
     * {@link Publisher}.
     * @param att PublisherAttributes.
     * @param listener reference to the {@link PublisherListener}.
     * @return reference to the created {@link Publisher} (null if not created).
     */
    public static synchronized <T extends Serializable> Publisher<T> createPublisher(Participant part, PublisherAttributes att, PublisherListener listener) {
        for (Participant it : m_participants) {
            if (it.getGuid().equals(part.getGuid())) {
                return part.createPublisher(att, listener);
            }
        }
        return null;
    }

    /**
     * Create a Subscriber in a Participant.
     *
     * @param <T>
     * @param part reference to the participant where you want to create the
     * {@link Publisher}.
     * @param att subscriber attributes.
     * @param listener reference to the {@link SubscriberListener}.
     * @return reference to the created {@link Subscriber} (null if not created).
     */
    public static synchronized <T extends Serializable> Subscriber<T> createSubscriber(Participant part, SubscriberAttributes att, SubscriberListener listener) {
        for (Participant it : m_participants) {
            if (it.getGuid().equals(part.getGuid())) {
                return part.createSubscriber(att, listener);
            }
        }
        return null;
    }

    /**
     * Register a type in a participant.
     *
     * @param part reference to the {@link Participant}.
     * @param type reference to the {@link TopicDataType}.
     * @return true if correctly registered.
     */
    public static synchronized boolean registerType(Participant part, TopicDataType<?> type) {
        for (Participant it : m_participants) {
            if (it.getGuid().equals(part.getGuid())) {
                return part.registerType(type);
            }
        }
        return false;
    }

}