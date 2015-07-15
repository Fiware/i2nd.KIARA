package org.fiware.kiara.ps.qos;

import java.io.IOException;
import java.util.List;

import org.fiware.kiara.ps.qos.parameter.ParameterId;
import org.fiware.kiara.ps.rtps.common.Locator;
import org.fiware.kiara.ps.rtps.messages.elements.Count;
import org.fiware.kiara.ps.rtps.messages.elements.EntityId;
import org.fiware.kiara.ps.rtps.messages.elements.GUID;
import org.fiware.kiara.ps.rtps.messages.elements.Parameter;
import org.fiware.kiara.ps.rtps.messages.elements.ParameterList;
import org.fiware.kiara.ps.rtps.messages.elements.ProtocolVersion;
import org.fiware.kiara.ps.rtps.messages.elements.Timestamp;
import org.fiware.kiara.ps.rtps.messages.elements.VendorId;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterBool;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterBuilder;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterBuiltinEndpointSet;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterCount;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterEntityId;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterGuid;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterIPv4Address;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterLocator;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterPort;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterPropertyList;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterProtocolVersion;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterString;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterTime;
import org.fiware.kiara.ps.rtps.messages.elements.parameters.ParameterVendorId;
import org.fiware.kiara.serialization.impl.BinaryInputStream;
import org.fiware.kiara.serialization.impl.SerializerImpl;
import org.fiware.kiara.util.Pair;

public class QosList {

    private ParameterList m_allQos;

    private ParameterList m_inlineQos;

    public QosList() {
        this.m_allQos = new ParameterList();
        this.m_inlineQos = new ParameterList();
    }

    public boolean addQos(ParameterId pid, String string) {
        if (string.length() == 0) {
            return false;
        }
        if (pid == ParameterId.PID_TOPIC_NAME || pid == ParameterId.PID_TYPE_NAME || pid == ParameterId.PID_ENTITY_NAME) {
            ParameterString param = (ParameterString) ParameterBuilder.createParameter(pid, (short) 0);
            param.setContent(string);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            if (pid == ParameterId.PID_TOPIC_NAME) {
                this.m_inlineQos.addParameter(param);
                this.m_inlineQos.setHasChanged(true);
            }
            return true;
        }
        return false;
    }

    public boolean addQos(ParameterId pid, Locator loc) {
        if (pid == ParameterId.PID_UNICAST_LOCATOR || pid == ParameterId.PID_MULTICAST_LOCATOR ||
                pid == ParameterId.PID_DEFAULT_UNICAST_LOCATOR || pid == ParameterId.PID_DEFAULT_MULTICAST_LOCATOR ||
                pid == ParameterId.PID_METATRAFFIC_UNICAST_LOCATOR || pid == ParameterId.PID_METATRAFFIC_MULTICAST_LOCATOR)
        {
            ParameterLocator param = (ParameterLocator) ParameterBuilder.createParameter(pid, (short) 0);
            param.setLocator(loc);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }

        return false;
    }

    public boolean addQos(ParameterId pid, int uint) {
        if(pid == ParameterId.PID_DEFAULT_UNICAST_PORT || pid == ParameterId.PID_METATRAFFIC_UNICAST_PORT ||
                pid == ParameterId.PID_METATRAFFIC_MULTICAST_PORT)
        {
            ParameterPort param = (ParameterPort) ParameterBuilder.createParameter(pid, (short) 0);
            param.setPort(uint);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        } else if(pid == ParameterId.PID_BUILTIN_ENDPOINT_SET) {
            ParameterBuiltinEndpointSet param = (ParameterBuiltinEndpointSet) ParameterBuilder.createParameter(pid, (short) 0);
            param.setBuiltinEndpointSet(uint);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        } else if(pid == ParameterId.PID_PARTICIPANT_MANUAL_LIVELINESS_COUNT) {
            ParameterCount param = (ParameterCount) ParameterBuilder.createParameter(pid, (short) 0);
            param.setCount(new Count(uint));
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }

        return false;
    }

    public boolean addQos(ParameterId pid, boolean inBool) {
        if(pid == ParameterId.PID_EXPECTS_INLINE_QOS) {
            ParameterBool param = (ParameterBool) ParameterBuilder.createParameter(pid, (short) 0);
            param.setBool(inBool);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }

        return false;
    }

    public boolean addQos(ParameterId pid, GUID guid) {
        if(pid == ParameterId.PID_PARTICIPANT_GUID || pid == ParameterId.PID_GROUP_GUID) {
            ParameterGuid param = (ParameterGuid) ParameterBuilder.createParameter(pid, (short) 0);
            param.setGUID(guid);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }
        return false;
    }

    public boolean addQos(ParameterId pid, ProtocolVersion protocolVersion) {
        if(pid == ParameterId.PID_PROTOCOL_VERSION) {
            ParameterProtocolVersion param = (ParameterProtocolVersion) ParameterBuilder.createParameter(pid, (short) 0);
            param.setProtocolVersion(protocolVersion);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }
        return false;
    }

    public boolean addQos(ParameterId pid, VendorId vendorId) {
        if(pid == ParameterId.PID_VENDORID) {
            ParameterVendorId param = (ParameterVendorId) ParameterBuilder.createParameter(pid, (short) 0);
            param.setVendorId(vendorId);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }
        return false;
    }

    public boolean addQos(ParameterId pid, byte o1, byte o2, byte o3, byte o4) {
        if(pid == ParameterId.PID_METATRAFFIC_MULTICAST_IPADDRESS || pid == ParameterId.PID_DEFAULT_UNICAST_IPADDRESS ||
                pid == ParameterId.PID_METATRAFFIC_UNICAST_IPADDRESS || pid == ParameterId.PID_MULTICAST_IPADDRESS)
        {
            ParameterIPv4Address param = (ParameterIPv4Address) ParameterBuilder.createParameter(pid, (short) 0);
            param.setIpV4Address(o1, o2, o3, o4);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }
        return false;
    }

    public boolean addQos(ParameterId pid, String str1, String str2) {
        if(pid == ParameterId.PID_PROPERTY_LIST) {
            ParameterPropertyList param = null;
            boolean found = false;
            for (Parameter it : this.m_allQos.getParameters()) {
                if (it.getParameterId() == ParameterId.PID_PROPERTY_LIST) {
                    param = (ParameterPropertyList) it;
                    found = true;
                    break;
                }
            }
            if(!found) {
                param = (ParameterPropertyList) ParameterBuilder.createParameter(pid, (short) 0);
            }
            param.getProperties().add(new Pair<String, String>(str1, str2));
            this.m_allQos.setHasChanged(true);
            if(!found) {
                this.m_allQos.addParameter(param);
            }
            return true;
        }
        return false;
    }

    public boolean addQos(ParameterId pid, ParameterPropertyList list) {
        if(pid == ParameterId.PID_PROPERTY_LIST) {
            ParameterPropertyList param = (ParameterPropertyList) ParameterBuilder.createParameter(pid, (short) 0);
            for (Pair<String,String> pair : list.getProperties()) {
                param.addProperty(pair);
            }
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }
        return false;
    }

    public boolean addQos(ParameterId pid, EntityId entityId) {
        if(pid == ParameterId.PID_GROUP_ENTITYID) {
            ParameterEntityId param = (ParameterEntityId) ParameterBuilder.createParameter(pid, (short) 0);
            param.setEntityId(entityId);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }
        return false;
    }

    public boolean addQos(ParameterId pid, Timestamp timestamp) {
        if(pid == ParameterId.PID_PARTICIPANT_LEASE_DURATION)
        {
            ParameterTime param = (ParameterTime) ParameterBuilder.createParameter(pid, (short) 0);
            param.setTimestamp(timestamp);
            this.m_allQos.addParameter(param);
            this.m_allQos.setHasChanged(true);
            return true;
        }
        return false;
    }

    public boolean addQos(ParameterId pid, List<Byte> ocVec) {
        // TODO Implement
        return true;
    }

    public ParameterList getAllQos() {
        return this.m_allQos;
    }

    public ParameterList getInlineQos() {
        return this.m_inlineQos;
    }

}
