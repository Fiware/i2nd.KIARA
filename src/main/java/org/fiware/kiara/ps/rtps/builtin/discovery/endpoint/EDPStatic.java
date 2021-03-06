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
package org.fiware.kiara.ps.rtps.builtin.discovery.endpoint;

import java.util.concurrent.locks.Lock;

import static org.fiware.kiara.ps.rtps.common.TopicKind.NO_KEY;
import static org.fiware.kiara.ps.rtps.common.TopicKind.WITH_KEY;

import org.fiware.kiara.ps.rtps.attributes.BuiltinAttributes;
import org.fiware.kiara.ps.rtps.builtin.data.ParticipantProxyData;
import org.fiware.kiara.ps.rtps.builtin.data.ReaderProxyData;
import org.fiware.kiara.ps.rtps.builtin.data.WriterProxyData;
import org.fiware.kiara.ps.rtps.builtin.discovery.participant.PDPSimple;
import org.fiware.kiara.ps.rtps.messages.elements.EntityId;
import org.fiware.kiara.ps.rtps.messages.elements.GUID;
import org.fiware.kiara.ps.rtps.participant.RTPSParticipant;
import org.fiware.kiara.ps.rtps.reader.RTPSReader;
import org.fiware.kiara.ps.rtps.writer.RTPSWriter;
import org.fiware.kiara.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Rafael Lara {@literal <rafaellara@eprosima.com>}
 *
 */
public class EDPStatic extends EDP {

    /**
     * {@link EDPStaticXML} reference
     */
    private EDPStaticXML m_edpXML;
    
    /**
     * {@link BuiltinAttributes} for the static EDP
     */
    private final BuiltinAttributes m_attributes;

    /**
     * Logging object
     */
    private static final Logger logger = LoggerFactory.getLogger(EDPStatic.class);

    /**
     * Default {@link EDPStatic} constructor
     *
     * @param p Reference to the PDPSimple
     * @param part Reference to the RTPSParticipantImpl
     */
    public EDPStatic(PDPSimple p, RTPSParticipant part) {
        super(p, part);
        m_edpXML = null;
        m_attributes = new BuiltinAttributes();
    }

    /**
     * Abstract method to initialize the EDP.
     *
     * @param attributes DiscoveryAttributes structure.
     * @return True if correct.
     */
    @Override
    public boolean initEDP(BuiltinAttributes attributes) {
        logger.debug("Beginning STATIC Endpoint Discovery Protocol (SEDP)");
        m_attributes.copy(attributes);
        m_edpXML = new EDPStaticXML();
        String edpXml = attributes.getStaticEndpointXML();
        if (edpXml != null && !edpXml.isEmpty()) {
            return m_edpXML.loadXMLString(edpXml);
        }
        String filename = attributes.getStaticEndpointXMLFilename();
        return m_edpXML.loadXMLFile(filename);
    }

    /**
     * After a new local ReaderProxyData has been created some processing is
     * needed (depends on the implementation).
     *
     * @param rdata Reference to the ReaderProxyData object.
     * @return True if correct.
     */
    @Override
    public boolean processLocalReaderProxyData(ReaderProxyData rdata) {
        logger.debug("Local Reader proxy data {} in topic {}", rdata.getGUID().getEntityId(), rdata.getTopicName());
        //Add the property list entry to our local pdp
        ParticipantProxyData localpdata = m_PDP.getLocalParticipantProxyData();
        final Lock mutex = localpdata.getMutex();
        mutex.lock();
        try {
            localpdata.getProperties().getProperties().add(EDPStaticProperty.toProperty("Reader", "ALIVE", rdata.getUserDefinedId(), rdata.getGUID().getEntityId()));
            localpdata.setHasChanged(true);
            m_PDP.announceParticipantState(true);
        } finally {
            mutex.unlock();
        }
        return true;
    }

    /**
     * After a new local WriterProxyData has been created some processing is
     * needed (depends on the implementation).
     *
     * @param wdata Reference to the Writer ProxyData object.
     * @return True if correct.
     */
    @Override
    public boolean processLocalWriterProxyData(WriterProxyData wdata) {
        logger.debug("Local Writer proxy data {} in topic {}", wdata.getGUID().getEntityId(), wdata.getTopicName());
        //Add the property list entry to our local pdp
        ParticipantProxyData localpdata = m_PDP.getLocalParticipantProxyData();
        final Lock mutex = localpdata.getMutex();
        mutex.lock();
        try {
            localpdata.getProperties().getProperties().add(EDPStaticProperty.toProperty("Writer", "ALIVE",
                    wdata.getUserDefinedId(), wdata.getGUID().getEntityId()));
            localpdata.setHasChanged(true);
            m_PDP.announceParticipantState(true);
        } finally {
            mutex.unlock();
        }
        return true;
    }

    /**
     * Abstract method that removes a local Reader from the discovery method
     *
     * @param R Reference to the Reader to remove.
     * @return True if correctly removed.
     */
    @Override
    public boolean removeLocalReader(RTPSReader R) {
        ParticipantProxyData localpdata = m_PDP.getLocalParticipantProxyData();
        final Lock mutex = localpdata.getMutex();
        mutex.lock();
        try {
            for (Pair<String, String> pit : localpdata.getProperties().getProperties()) {
                EDPStaticProperty staticproperty = new EDPStaticProperty();
                if (staticproperty.fromProperty(pit)) {
                    if (staticproperty.entityId.equals(R.getGuid().getEntityId())) {
                        pit.copy(EDPStaticProperty.toProperty("Reader", "ENDED", R.getAttributes().getUserDefinedID(),
                                R.getGuid().getEntityId()));
                    }
                }
            }
            return false;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Abstract method that removes a local Writer from the discovery method
     *
     * @param W Reference to the Writer to remove.
     * @return True if correctly removed.
     */
    @Override
    public boolean removeLocalWriter(RTPSWriter W) {
        ParticipantProxyData localpdata = m_PDP.getLocalParticipantProxyData();
        final Lock mutex = localpdata.getMutex();
        mutex.lock();
        try {
            for (Pair<String, String> pit : localpdata.getProperties().getProperties()) {
                EDPStaticProperty staticproperty = new EDPStaticProperty();
                if (staticproperty.fromProperty(pit)) {
                    if (staticproperty.entityId.equals(W.getGuid().getEntityId())) {
                        pit.copy(EDPStaticProperty.toProperty("Writer", "ENDED", W.getAttributes().getUserDefinedID(),
                                W.getGuid().getEntityId()));
                    }
                }
            }
            return false;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Abstract method that assigns remote endpoints when a new
     * RTPSParticipantProxyData is discovered.
     *
     * @param pdata Reference to the ParticipantProxyData.
     */
    @Override
    public void assignRemoteEndpoints(ParticipantProxyData pdata) {
        final Lock mutex = pdata.getMutex();
        mutex.lock();
        try {
            for (Pair<String, String> pit : pdata.getProperties().getProperties()) {
                //cout << "STATIC EDP READING PROPERTY " << pit->first << "// " << pit->second << endl;
                EDPStaticProperty staticproperty = new EDPStaticProperty();
                if (staticproperty.fromProperty(pit)) {
                    if ("Reader".equals(staticproperty.endpointType) && "ALIVE".equals(staticproperty.status)) {
                        GUID guid = new GUID(pdata.getGUID().getGUIDPrefix(), staticproperty.entityId);
                        ReaderProxyData rdata = m_PDP.lookupReaderProxyData(guid);

                        if (rdata == null) { //IF NOT FOUND, we CREATE AND PAIR IT
                            newRemoteReader(pdata, staticproperty.userId, staticproperty.entityId);
                        }
                    } else if ("Writer".equals(staticproperty.endpointType) && "ALIVE".equals(staticproperty.status)) {

                        GUID guid = new GUID(pdata.getGUID().getGUIDPrefix(), staticproperty.entityId);
                        WriterProxyData wdata = m_PDP.lookupWriterProxyData(guid);
                        if (wdata == null) { //IF NOT FOUND, we CREATE AND PAIR IT
                            newRemoteWriter(pdata, staticproperty.userId, staticproperty.entityId);
                        }
                    } else if ("Reader".equals(staticproperty.endpointType) && "ENDED".equals(staticproperty.status)) {
                        GUID guid = new GUID(pdata.getGUID().getGUIDPrefix(), staticproperty.entityId);
                        removeReaderProxy(guid);
                    } else if ("Writer".equals(staticproperty.endpointType) && "ENDED".equals(staticproperty.status)) {
                        GUID guid = new GUID(pdata.getGUID().getGUIDPrefix(), staticproperty.entityId);
                        removeWriterProxy(guid);
                    } else {
                        logger.warn("RTPS EDP: Property with type: {} and status {} not recognized", staticproperty.endpointType, staticproperty.status);
                    }
                } else {

                }
            }
        } finally {
            mutex.unlock();
        }
    }

    /**
     * New Remote Reader has been found and this method process it and calls the
     * pairing methods.
     *
     * @param pdata Reference to the RTPSParticipantProxyData object.
     * @param userId UserId.
     * @param entId EntityId.
     * @return true if correct.
     */
    public boolean newRemoteReader(ParticipantProxyData pdata, short userId, EntityId entId) {
        ReaderProxyData rpd = m_edpXML.lookforReader(pdata.getParticipantName(), userId);
        if (rpd != null) {
            logger.debug("Activating remote Reader {} in topic {}", rpd.getGUID().getEntityId(), rpd.getTopicName());
            ReaderProxyData newRPD = new ReaderProxyData();
            newRPD.copy(rpd);
            newRPD.getGUID().setGUIDPrefix(pdata.getGUID().getGUIDPrefix());
            if (!entId.equals(new EntityId())) {
                newRPD.getGUID().setEntityId(entId);
            }
            if (!checkEntityId(newRPD)) {
                logger.error("The provided entityId for Reader with ID: {} does not match the topic Kind", newRPD.getUserDefinedId());
                return false;
            }
            newRPD.setKey(newRPD.getGUID());
            newRPD.setRTPSParticipantKey(pdata.getGUID());
            if (m_PDP.addReaderProxyData(newRPD, false)) {
                //CHECK the locators:
                if (newRPD.getUnicastLocatorList().isEmpty() && newRPD.getMulticastLocatorList().isEmpty()) {
                    newRPD.setUnicastLocatorList(pdata.getDefaultUnicastLocatorList());
                    newRPD.setMulticastLocatorList(pdata.getDefaultMulticastLocatorList());
                }
                newRPD.setIsAlive(true);
                pairingReaderProxy(newRPD);
                return true;
            }
        }
        return false;
    }

    /**
     * New Remote Writer has been found and this method process it and calls the
     * pairing methods.
     *
     * @param pdata Reference to the RTPSParticipantProxyData object.
     * @param userId UserId.
     * @param entId EntityId.
     * @return True if correct.
     */
    public boolean newRemoteWriter(ParticipantProxyData pdata, short userId, EntityId entId) {
	WriterProxyData wpd = m_edpXML.lookforWriter(pdata.getParticipantName(), userId);
	if (wpd != null) {
		logger.debug("Activating remote Writer {} in topic {}", wpd.getGUID().getEntityId(), wpd.getTopicName());
		WriterProxyData newWPD = new WriterProxyData();
		newWPD.copy(wpd);
		newWPD.getGUID().setGUIDPrefix(pdata.getGUID().getGUIDPrefix());
		if(!entId.equals(new EntityId()))
			newWPD.getGUID().setEntityId(entId);
		if(!checkEntityId(newWPD))
		{
			logger.error("RTPS EDP: The provided entityId for Writer with User ID: {} does not match the topic Kind", newWPD.getUserDefinedId());
			return false;
		}
		newWPD.setKey(newWPD.getGUID());
		newWPD.setRTPSParticipantKey(pdata.getGUID());
		if (m_PDP.addWriterProxyData(newWPD, false)) {
			//CHECK the locators:
			if (newWPD.getUnicastLocatorList().isEmpty() && newWPD.getMulticastLocatorList().isEmpty()) {
				newWPD.setUnicastLocatorList(pdata.getDefaultUnicastLocatorList());
				newWPD.setMulticastLocatorList(pdata.getDefaultMulticastLocatorList());
			}
			newWPD.setIsAlive(true);
			pairingWriterProxy(newWPD);
			return true;
		}
	}
	return false;
    }

    /**
     * This method checks the provided entityId against the topic type to see if
     * it matches
     *
     * @param rdata Reference to the readerProxyData
     * @return True if its correct.
     *
     */
    public boolean checkEntityId(ReaderProxyData rdata) {
        if (rdata.getTopicKind() == WITH_KEY && rdata.getGUID().getEntityId().getValue(3) == 0x07) {
            return true;
        }
        if (rdata.getTopicKind() == NO_KEY && rdata.getGUID().getEntityId().getValue(3) == 0x04) {
            return true;
        }
        return false;
    }

    /**
     * This method checks the provided entityId against the topic type to see if
     * it matches
     *
     * @param wdata Reference to the writerProxyData
     * @return True if its correct.
     *
     */
    public boolean checkEntityId(WriterProxyData wdata) {
        if (wdata.getTopicKind() == WITH_KEY && wdata.getGUID().getEntityId().getValue(3) == 0x02) {
            return true;
        }
        if (wdata.getTopicKind() == NO_KEY && wdata.getGUID().getEntityId().getValue(3) == 0x03) {
            return true;
        }
        return false;
    }

}
