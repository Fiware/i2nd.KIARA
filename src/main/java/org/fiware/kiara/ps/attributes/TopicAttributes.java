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
package org.fiware.kiara.ps.attributes;

import org.fiware.kiara.ps.qos.policies.HistoryQosPolicy;
import org.fiware.kiara.ps.qos.policies.HistoryQosPolicyKind;
import org.fiware.kiara.ps.qos.policies.ResourceLimitsQosPolicy;
import org.fiware.kiara.ps.rtps.common.TopicKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class TopicAttributes, used by the user to define the attributes of the topic
 * associated with a Publisher or Subscriber.
 *
 * @author Rafael Lara {@literal <rafaellara@eprosima.com>}
 */
public class TopicAttributes {

    /**
     * TopicKind, default value NO_KEY.
     */
    public TopicKind topicKind;

    /**
     * Topic Name.
     */
    public String topicName;

    /**
     * Topic Data Type Name.
     */
    public String topicDataTypeName;

    /**
     * QOS Regarding the History to be saved.
     */
    public HistoryQosPolicy historyQos;

    /**
     * QOS Regarding the resources to allocate.
     */
    public ResourceLimitsQosPolicy resourceLimitQos;

    /**
     * Logging object
     */
    private static final Logger logger = LoggerFactory.getLogger(TopicAttributes.class);

    /**
     * Main Constructor
     */
    public TopicAttributes() {
        this.topicKind = TopicKind.NO_KEY;
        this.topicName = "UNDEF";
        this.topicDataTypeName = "UNDEF";
        this.historyQos = new HistoryQosPolicy();
        this.resourceLimitQos = new ResourceLimitsQosPolicy();
    }

    /**
     * Constructor, you need to provide the topic name and the topic data type.
     *
     * @param name The name of the topic
     * @param dataType The name of the data type
     * @param kind The TopicKind of the topic
     */
    public TopicAttributes(String name, String dataType, TopicKind kind) {
        this.topicKind = kind;
        this.topicName = name;
        this.topicDataTypeName = dataType;
        this.historyQos = new HistoryQosPolicy();
        this.resourceLimitQos = new ResourceLimitsQosPolicy();
    }

    /**
     * Get the topic data type
     *
     * @return Topic data type
     */
    public String getTopicDataType() {
        return topicDataTypeName;
    }

    /**
     * Get the topic kind
     *
     * @return Topic kind
     */
    public TopicKind getTopicKind() {
        return topicKind;
    }

    /**
     * Get the topic name
     *
     * @return Topic name
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * Method to check whether the defined QOS are correct.
     *
     * @return True if they are valid.
     */
    public boolean checkQos() {
        if (resourceLimitQos.maxSamplesPerInstance > resourceLimitQos.maxSamples && this.topicKind == TopicKind.WITH_KEY) {
            logger.error("INCORRECT TOPIC QOS: Max samples per instance must be less or equal than max samples");
            return false;
        }

        if (resourceLimitQos.maxSamplesPerInstance * resourceLimitQos.maxInstances > resourceLimitQos.maxSamples && topicKind == TopicKind.WITH_KEY) {
            logger.warn("TOPIC QOS: maxSamples < maxSamplesPerInstance * maxInstances");
        }

        if (historyQos.kind == HistoryQosPolicyKind.KEEP_LAST_HISTORY_QOS) {
            if (historyQos.depth > resourceLimitQos.maxSamples) {
                logger.error("INCORRECT TOPIC QOS: depth must be <= max_samples");
                return false;
            }
            if (historyQos.depth > resourceLimitQos.maxSamplesPerInstance && topicKind == TopicKind.WITH_KEY) {
                logger.error("INCORRECT TOPIC QOS: depth must be <= max_samples_per_instance");
                return false;
            }
            if (historyQos.depth <= 0) {
                logger.error("INCORRECT TOPIC QOS: depth must be > 0");
                return false;
            }
        }

        return true;

    }

    /**
     * Compares two TopicAttributes objects
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof TopicAttributes) {
            TopicAttributes t2 = (TopicAttributes) other;
            if (this.topicKind != t2.topicKind) {
                return false;
            }
            if (!this.topicName.equals(t2.topicName)) {
                return false;
            }
            if (!this.topicDataTypeName.equals(t2.topicDataTypeName)) {
                return false;
            }
            if (this.historyQos.kind != t2.historyQos.kind) {
                return false;
            }
            if (this.historyQos.kind == HistoryQosPolicyKind.KEEP_LAST_HISTORY_QOS && this.historyQos.depth != t2.historyQos.depth) {
                return false;
            }
            return true;
        }
        return false;
    }

}
