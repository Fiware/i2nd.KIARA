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
package org.fiware.kiara.ps.rtps.builtin.discovery.participant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.fiware.kiara.ps.qos.policies.LivelinessQosPolicyKind;
import org.fiware.kiara.ps.rtps.RTPSDomain;
import org.fiware.kiara.ps.rtps.attributes.BuiltinAttributes;
import org.fiware.kiara.ps.rtps.attributes.HistoryCacheAttributes;
import org.fiware.kiara.ps.rtps.attributes.ReaderAttributes;
import org.fiware.kiara.ps.rtps.attributes.RemoteReaderAttributes;
import org.fiware.kiara.ps.rtps.attributes.RemoteWriterAttributes;
import org.fiware.kiara.ps.rtps.attributes.WriterAttributes;
import org.fiware.kiara.ps.rtps.builtin.BuiltinProtocols;
import org.fiware.kiara.ps.rtps.builtin.data.ParticipantProxyData;
import org.fiware.kiara.ps.rtps.builtin.data.ReaderProxyData;
import org.fiware.kiara.ps.rtps.builtin.data.WriterProxyData;
import org.fiware.kiara.ps.rtps.builtin.discovery.endpoint.EDP;
import org.fiware.kiara.ps.rtps.builtin.discovery.endpoint.EDPSimple;
import org.fiware.kiara.ps.rtps.builtin.discovery.endpoint.EDPStatic;
import org.fiware.kiara.ps.rtps.builtin.discovery.participant.timedevent.ResendParticipantProxyDataPeriod;
import org.fiware.kiara.ps.rtps.common.DurabilityKind;
import org.fiware.kiara.ps.rtps.common.EncapsulationKind;
import org.fiware.kiara.ps.rtps.common.EndpointKind;
import org.fiware.kiara.ps.rtps.common.Locator;
import org.fiware.kiara.ps.rtps.common.ReliabilityKind;
import org.fiware.kiara.ps.rtps.common.TopicKind;
import org.fiware.kiara.ps.rtps.history.CacheChange;
import org.fiware.kiara.ps.rtps.history.ReaderHistoryCache;
import org.fiware.kiara.ps.rtps.history.WriterHistoryCache;
import org.fiware.kiara.ps.rtps.messages.common.types.ChangeKind;
import org.fiware.kiara.ps.rtps.messages.common.types.RTPSEndian;
import org.fiware.kiara.ps.rtps.messages.elements.EntityId;
import org.fiware.kiara.ps.rtps.messages.elements.GUID;
import org.fiware.kiara.ps.rtps.messages.elements.GUIDPrefix;
import org.fiware.kiara.ps.rtps.messages.elements.ParameterList;
import org.fiware.kiara.ps.rtps.messages.elements.EntityId.EntityIdEnum;
import org.fiware.kiara.ps.rtps.participant.RTPSParticipant;
import org.fiware.kiara.ps.rtps.reader.RTPSReader;
import org.fiware.kiara.ps.rtps.reader.StatefulReader;
import org.fiware.kiara.ps.rtps.reader.StatelessReader;
import org.fiware.kiara.ps.rtps.reader.WriterProxy;
import org.fiware.kiara.ps.rtps.utils.InfoEndianness;
import org.fiware.kiara.ps.rtps.writer.RTPSWriter;
import org.fiware.kiara.ps.rtps.writer.StatelessWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class PDPSimple that implements the SimpleRTPSParticipantDiscoveryProtocol as
 * defined in the RTPS specification.
 *
 * @author Rafael Lara {@literal <rafaellara@eprosima.com>}
 */
public class PDPSimple {

    private final BuiltinProtocols m_builtin;

    /**
     * Reference to the local RTPSParticipant.
     */
    private RTPSParticipant m_RTPSParticipant;

    /**
     * Discovery attributes.
     */
    private BuiltinAttributes m_discovery;

    /**
     * Reference to the SPDPWriter.
     */
    private StatelessWriter m_SPDPWriter;

    /**
     * Reference to the SPDPReader.
     */
    private StatelessReader m_SPDPReader;

    /**
     * Reference to the EDP object.
     */
    private EDP m_EDP;

    /**
     * Registered RTPSParticipants (including the local one, that is the first
     * one.)
     */
    private final List<ParticipantProxyData> m_participantProxies;

    /**
     * Variable to indicate if any parameter has changed.
     */
    private boolean m_hasChangedLocalPDP;

    /**
     * TimedEvent to periodically resend the local RTPSParticipant information.
     */
    private ResendParticipantProxyDataPeriod m_resendParticipantTimer;

    /**
     * Listener for the SPDP messages.
     */
    private PDPSimpleListener m_listener;

    /**
     * Writer History Cache
     */
    private WriterHistoryCache m_SPDPWriterHistory;

    /**
     * Reader History Cache
     */
    private ReaderHistoryCache m_SPDPReaderHistory;

    /**
     * Mutex
     */
    private final Lock m_mutex = new ReentrantLock(true);

    /**
     * Logging object
     */
    private static final Logger logger = LoggerFactory.getLogger(PDPSimple.class);

    /**
     * Guard Mutex
     */
    private final Lock m_guardMutex = new ReentrantLock(true);

    /**
     * Default {@link PDPSimple} constructor
     *
     * @param builtinProtocols Reference to the BuiltinProcols object.
     */
    public PDPSimple(BuiltinProtocols builtinProtocols) {
        this.m_builtin = builtinProtocols;
        this.m_hasChangedLocalPDP = true;
        this.m_participantProxies = new ArrayList<>();
    }

    /**
     * Deletes the {@link PDPSimple} associated entities
     */
    public void destroy() {
        this.m_mutex.lock();
        try {

            if (this.m_EDP != null) {
                this.m_EDP.destroy();
            }

            if (this.m_resendParticipantTimer != null) {
                this.m_resendParticipantTimer.delete();
            }

            if (this.m_SPDPReader != null) {
                RTPSDomain.removeRTPSReader(this.m_SPDPReader);
            }

            if (this.m_SPDPWriter != null) {
                RTPSDomain.removeRTPSWriter(this.m_SPDPWriter);
            }

            while (this.m_participantProxies.size() > 0) {
                this.m_participantProxies.get(0).destroy();
                this.m_participantProxies.remove(0);
            }
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * Initialize the PDP.
     *
     * @param participant Reference to the RTPSParticipant.
     * @return True on success
     */
    public boolean initPDP(RTPSParticipant participant) {
        logger.debug("Starting Participant Discovery Protocol (PDP)");
        this.m_RTPSParticipant = participant;
        this.m_discovery = this.m_RTPSParticipant.getAttributes().builtinAtt;
        this.m_mutex.lock();

        try {

            if (!createSPDPEndpoints()) {
                return false;
            }

            this.m_builtin.updateMetatrafficLocators(this.m_SPDPReader.getAttributes().unicastLocatorList);
            this.m_participantProxies.add(new ParticipantProxyData());
            this.m_participantProxies.get(0).initializeData(this.m_RTPSParticipant, this);

            // Init EDP
            if (this.m_discovery.useStaticEDP) {
                this.m_EDP = new EDPStatic(this, this.m_RTPSParticipant);
                if (!this.m_EDP.initEDP(this.m_discovery)) {
                    return false;
                }
            } else if (this.m_discovery.useSimpleEDP) {
                this.m_EDP = new EDPSimple(this, this.m_RTPSParticipant);
                this.m_EDP.initEDP(this.m_discovery);
            } else {
                logger.warn("No EndpointDiscoveryProtocol has been defined");
                destroy();
                return false;
            }

            //this.m_resendParticipantTimer = new ResendParticipantProxyDataPeriod(this, this.m_discovery.leaseDurationAnnouncementPeriod.toMilliSecondsDouble());
            return true;

        } finally {
            this.m_mutex.unlock();
        }

    }

    /**
     * Stop the RTPSParticipantAnnouncement (only used in tests).
     */
    public void stopParticipantAnnouncement() {
        if (this.m_resendParticipantTimer != null) {
            this.m_resendParticipantTimer.stopTimer();
        }
    }

    /**
     * Reset the RTPSParticipantAnnouncement (only used in tests).
     */
    public void resetParticipantAnnouncement() {
        //this.m_resendParticipantTimer.restartTimer();
        this.m_resendParticipantTimer = new ResendParticipantProxyDataPeriod(this, this.m_discovery.leaseDurationAnnouncementPeriod.toMilliSecondsDouble());
    }

    /**
     * Get a reference to the local RTPSParticipant RTPSParticipantProxyData
     * object.
     *
     * @return Reference to the local RTPSParticipant RTPSParticipantProxyData
     * object.
     */
    public ParticipantProxyData getLocalParticipantProxyData() {
        return this.m_participantProxies.get(0);
    }

    /**
     * Force the sending of our local DPD to all remote RTPSParticipants and
     * multicast Locators.
     *
     * @param newChange If true a new change (with new seqNum) is created and
     * sent; if false the last change is re-sent
     */
    public void announceParticipantState(boolean newChange) {
        logger.debug("Announcing RTPSParticipant State (new change: {})", newChange);
        CacheChange change = null;

        if (newChange || this.m_hasChangedLocalPDP) {
            this.getLocalParticipantProxyData().increaseManualLivelinessCount();
            if (this.m_SPDPWriterHistory.getHistorySize() > 0) {
                this.m_SPDPWriterHistory.removeMinChange();
            }
            change = this.m_SPDPWriter.newChange(ChangeKind.ALIVE, getLocalParticipantProxyData().getKey());
            ParticipantProxyData proxyData = getLocalParticipantProxyData();
            proxyData.setHasChanged(true);
            ParameterList paramList = proxyData.toParameterList();
            //ParameterList paramList = getLocalParticipantProxyData().toParameterList();
            if (paramList != null) {
                change.getSerializedPayload().setEncapsulationKind(InfoEndianness.checkMachineEndianness() == RTPSEndian.BIG_ENDIAN ? EncapsulationKind.PL_CDR_BE : EncapsulationKind.PL_CDR_LE);
                change.getSerializedPayload().deleteParameters();
                change.getSerializedPayload().addParameters(paramList);
                this.m_SPDPWriterHistory.addChange(change);
            }
            this.m_hasChangedLocalPDP = false;
        } else {
            this.m_SPDPWriter.unsentChangesReset();
        }
    
    }

    /**
     * This method returns a reference to a ReaderProxyData object if it is
     * found among the registered RTPSParticipants (including the local
     * RTPSParticipant).
     *
     * @param reader GUID of the reader we are looking for.
     * @return Reference to the ReaderProxyData object if found.
     */
    public ReaderProxyData lookupReaderProxyData(GUID reader) {
        this.m_mutex.lock();
        logger.debug("Lookup ReaderProxyData: " + reader);
        try {
            for (ParticipantProxyData pit : this.m_participantProxies) {
                pit.getMutex().lock();
                try {
                    for (ReaderProxyData rit : pit.getReaders()) {
                        if (rit.getGUID().equals(reader)) {
                            return rit;
                        }
                    }
                } finally {
                    pit.getMutex().unlock();
                }
            }
            return null;
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * This method returns a reference to a WriterProxyData object if it is
     * found among the registered RTPSParticipants (including the local
     * RTPSParticipant).
     *
     * @param writer GUID of the writer we are looking for.
     * @return Reference to the WriterProxyData object.
     */
    public WriterProxyData lookupWriterProxyData(GUID writer) {
        this.m_mutex.lock();
        logger.debug("Lookup WriterProxyData " + writer);
        try {
            for (ParticipantProxyData pit : this.m_participantProxies) {
                pit.getMutex().lock();
                try {
                    for (WriterProxyData wit : pit.getWriters()) {
                        if (wit.getGUID().equals(writer)) {
                            return wit;
                        }
                    }
                } finally {
                    pit.getMutex().unlock();
                }
            }
            return null;
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * This method removes and deletes a ReaderProxyData object from its
     * corresponding RTPSParticipant.
     *
     * @param rdata Reference to the ReaderProxyData object.
     * @return true if found and deleted.
     */
    public boolean removeReaderProxyData(ReaderProxyData rdata) {
        this.m_mutex.lock();
        logger.debug("Removing ReaderProxyData " + rdata.getGUID());
        try {
            for (ParticipantProxyData pit : this.m_participantProxies) {
                pit.getMutex().lock();
                try {
                    for (ReaderProxyData rit : pit.getReaders()) {
                        if (rit.getGUID().equals(rdata.getGUID())) {
                            return pit.getReaders().remove(rdata);
                        }
                    }
                } finally {
                    pit.getMutex().unlock();
                }
            }
            return false;
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * This method removes and deletes a WriterProxyData object from its
     * corresponding RTPSParticipant.
     *
     * @param wdata Reference to the WriterProxyData object.
     * @return true if found and deleted.
     */
    public boolean removeWriterProxyData(WriterProxyData wdata) {
        this.m_mutex.lock();
        logger.debug("Removing WriterProxyData " + wdata.getGUID());
        try {
            for (ParticipantProxyData pit : this.m_participantProxies) {
                pit.getMutex().lock();
                try {
                    for (WriterProxyData wit : pit.getWriters()) {
                        if (wit.getGUID().equals(wdata.getGUID())) {
                            return pit.getWriters().remove(wdata);
                        }
                    }
                } finally {
                    pit.getMutex().unlock();
                }
            }
            return false;
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * This method returns a reference to a RTPSParticipantProxyData object if
     * it is found among the registered RTPSParticipants.
     *
     * @param pguid GUID of the RTPSParticipant we are looking for.
     * @return RTPSParticipantProxyData object.
     */
    public ParticipantProxyData lookupParticipantProxyData(GUID pguid) {
        logger.debug("Lookup ParticipantProxyData " + pguid);
        this.m_mutex.lock();
        try {
            for (ParticipantProxyData pit : this.m_participantProxies) {
                if (pit.getGUID().equals(pguid)) {
                    return pit;
                }
            }
            return null;
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * Create the SPDP Writer and Reader
     *
     * @return True if correct.
     */
    private boolean createSPDPEndpoints() {
        logger.debug("Beginning PDP Builtin Endpoints creation");

        HistoryCacheAttributes whatt = new HistoryCacheAttributes();
        whatt.payloadMaxSize = ParticipantProxyData.DISCOVERY_PARTICIPANT_DATA_MAX_SIZE;
        whatt.initialReservedCaches = 20;
        whatt.maximumReservedCaches = 100;

        this.m_SPDPWriterHistory = new WriterHistoryCache(whatt);

        WriterAttributes watt = new WriterAttributes();
        watt.endpointAtt.endpointKind = EndpointKind.WRITER;
        watt.endpointAtt.durabilityKind = DurabilityKind.TRANSIENT_LOCAL;
        watt.endpointAtt.reliabilityKind = ReliabilityKind.BEST_EFFORT;
        watt.endpointAtt.topicKind = TopicKind.WITH_KEY;

        RTPSWriter wout = this.m_RTPSParticipant.createWriter(watt, this.m_SPDPWriterHistory, null, new EntityId(EntityIdEnum.ENTITYID_SPDP_BUILTIN_RTPSPARTICIPANT_WRITER), true);
        if (wout != null) {
            this.m_SPDPWriter = (StatelessWriter) wout;
            RemoteReaderAttributes ratt = new RemoteReaderAttributes();
            for (Locator lit : this.m_builtin.getMetatrafficMulticastLocatorList().getLocators()) {
                this.m_SPDPWriter.addLocator(ratt, lit);
            }
            if (this.m_builtin.getUseMandatory()) {
                this.m_SPDPWriter.addLocator(ratt, this.m_builtin.getMandatoryMulticastLocator());
            }
            logger.debug("Simple PDP Builtin Writer created with GUID {}", this.m_SPDPWriter.getGuid());
        } else {
            logger.error("Simple PDP Builtin Writer creation failed");
            this.m_SPDPWriterHistory = null;
            return false;
        }

        HistoryCacheAttributes rhatt = new HistoryCacheAttributes();
        rhatt.payloadMaxSize = ParticipantProxyData.DISCOVERY_PARTICIPANT_DATA_MAX_SIZE;
        rhatt.initialReservedCaches = 250;
        rhatt.maximumReservedCaches = 5000;

        this.m_SPDPReaderHistory = new ReaderHistoryCache(rhatt);

        ReaderAttributes ratt = new ReaderAttributes();
        ratt.endpointAtt.multicastLocatorList.copy(this.m_builtin.getMetatrafficMulticastLocatorList());
        ratt.endpointAtt.unicastLocatorList.copy(this.m_builtin.getMetatrafficUnicastLocatorList());
        //this.m_builtin.getMetatrafficMulticastLocatorList().getLocators().get(0).setPort(5555);
        ratt.endpointAtt.topicKind = TopicKind.WITH_KEY;
        ratt.endpointAtt.durabilityKind = DurabilityKind.TRANSIENT_LOCAL;
        ratt.endpointAtt.reliabilityKind = ReliabilityKind.BEST_EFFORT;

        this.m_listener = new PDPSimpleListener(this);

        RTPSReader rout = this.m_RTPSParticipant.createReader(ratt, this.m_SPDPReaderHistory, this.m_listener, new EntityId(EntityIdEnum.ENTITYID_SPDP_BUILTIN_RTPSPARTICIPANT_READER), true);
        if (rout != null) {
            this.m_SPDPReader = (StatelessReader) rout;
            logger.debug("Simple PDP Builtin Reader created with GUID {}", this.m_SPDPReader.getGuid());
        } else {
            logger.error("Simple PDP builtin Reader creation failed");
            this.m_SPDPReaderHistory = null;
            this.m_listener = null;
            return false;
        }

        logger.debug("SPDP Builtin Endpoints creation finished");
        return true;
    }

    /**
     * Add a ReaderProxyData to the correct ParticipantProxyData.
     *
     * @param rdata Reference to the ReaderProxyData object to add.
     * @return True if correct.
     */
    public boolean addReaderProxyData(ReaderProxyData rdata) {
        return addReaderProxyData(rdata, false, null, null);
    }

    public boolean addReaderProxyData(ReaderProxyData rdata, boolean copyData) {
        return addReaderProxyData(rdata, copyData, null, null);
    }

    /**
     * Add a ReaderProxyData to the correct ParticipantProxyData.
     *
     * @param rdata Reference to the ReaderProxyData object to add.
     * @param copyData Boolean variable indicating the need to copy the passed
     * object.
     * @param returnReaderProxyData Reference to reference in case you wanted
     * the data copied.
     * @param pdata Reference to the associated ParticipantProxyData.
     * @return True if correct.
     */
    public boolean addReaderProxyData(ReaderProxyData rdata, boolean copyData, ReaderProxyData returnReaderProxyData, ParticipantProxyData pdata) {
        logger.debug("Adding ReaderProxyData: " + rdata.getGUID());
        this.m_mutex.lock();
        try {
            for (ParticipantProxyData pit : this.m_participantProxies) {
                pit.getMutex().lock();
                try {
                    if (pit.getGUID().getGUIDPrefix().equals(rdata.getGUID().getGUIDPrefix())) {
                        // Check that it is not already
                        for (ReaderProxyData rit : pit.getReaders()) {
                            if (rit.getGUID().getEntityId().equals(rdata.getGUID().getEntityId())) {
                                if (copyData) {
                                    returnReaderProxyData.copy(rit);
                                    pdata.copy(pit);
                                }
                                return false;
                            }
                        }
                        if (copyData) {
                            ReaderProxyData newRPD = new ReaderProxyData();
                            newRPD.copy(rdata);
                            pit.getReaders().add(newRPD);
                            returnReaderProxyData.copy(newRPD);
                            pdata.copy(pit);
                        } else {
                            pit.getReaders().add(rdata);
                        }
                        return true;
                    }
                } finally {
                    pit.getMutex().unlock();
                }
            }
            return false;
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * Add a WriterProxyData to the correct ParticipantProxyData.
     *
     * @param wdata Reference to the WriterProxyData object to add.
     * @return True if correct.
     */
    public boolean addWriterProxyData(WriterProxyData wdata) {
        return addWriterProxyData(wdata, false, null, null);
    }

    /**
     * Add a WriterProxyData to the correct ParticipantProxyData.
     *
     * @param wdata Reference to the WriterProxyData object to add.
     * @param copyData Boolean variable indicating the need to copy the passed
     * object.
     * @return True if correct.
     */
    public boolean addWriterProxyData(WriterProxyData wdata, boolean copyData) {
        return addWriterProxyData(wdata, copyData, null, null);
    }

    /**
     * Add a WriterProxyData to the correct ParticipantProxyData.
     *
     * @param wdata Reference to the WriterProxyData object to add.
     * @param copyData Boolean variable indicating the need to copy the passed
     * object.
     * @param returnWriterProxyData Reference to reference in case you wanted
     * the data copied.
     * @param pdata Reference to the associated ParticipantProxyData.
     * @return True if correct.
     */
    public boolean addWriterProxyData(WriterProxyData wdata, boolean copyData, WriterProxyData returnWriterProxyData, ParticipantProxyData pdata) {
        logger.debug("Adding WriterProxyData: " + wdata.getGUID());
        this.m_mutex.lock();
        try {
            for (ParticipantProxyData pit : this.m_participantProxies) {
                pit.getMutex().lock();
                try {
                    if (pit.getGUID().getGUIDPrefix().equals(wdata.getGUID().getGUIDPrefix())) {
                        // Check that it is not already
                        for (WriterProxyData rit : pit.getWriters()) {
                            if (rit.getGUID().getEntityId().equals(wdata.getGUID().getEntityId())) {
                                if (copyData) {
                                    returnWriterProxyData.copy(rit);
                                    pdata.copy(pit);
                                }
                                return false;
                            }
                        }
                        if (copyData) {
                            WriterProxyData newWPD = new WriterProxyData();
                            newWPD.copy(wdata);
                            pit.getWriters().add(newWPD);
                            returnWriterProxyData.copy(newWPD);
                            pdata.copy(pit);
                        } else {
                            pit.getWriters().add(wdata);
                        }
                        return true;
                    }
                } finally {
                    pit.getMutex().unlock();
                }
            }
            return false;
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * This method assigns remote endpoints to the builtin endpoints defined in
     * this protocol. It also calls the corresponding methods in EDP and WLP.
     *
     * @param pdata Reference to the RTPSParticipantProxyData object.
     */
    public void assignRemoteEndpoints(ParticipantProxyData pdata) {
        logger.debug("Assign remote Endpoints for RTPSParticipant {}", pdata.getGUID().getGUIDPrefix());
        int endp = pdata.getAvailableBuiltinEndpoints();
        pdata.getMutex().lock();
        try {
            int auxEndp = endp;
            auxEndp &= ParticipantProxyData.DISC_BUILTIN_ENDPOINT_PARTICIPANT_ANNOUNCER;
            if (auxEndp != 0) {
                RemoteWriterAttributes watt = new RemoteWriterAttributes();
                watt.guid.setGUIDPrefix(pdata.getGUID().getGUIDPrefix());
                watt.guid.setEntityId(new EntityId(EntityIdEnum.ENTITYID_SPDP_BUILTIN_RTPSPARTICIPANT_WRITER));
                watt.endpoint.unicastLocatorList.copy(pdata.getMetatrafficUnicastLocatorList());
                watt.endpoint.multicastLocatorList.copy(pdata.getMetatrafficMulticastLocatorList());
                watt.endpoint.reliabilityKind = ReliabilityKind.BEST_EFFORT;
                watt.endpoint.durabilityKind = DurabilityKind.TRANSIENT_LOCAL;
                pdata.getBuiltinWriters().add(watt);
                this.m_SPDPReader.matchedWriterAdd(watt);
            }
            auxEndp = endp;
            auxEndp &= ParticipantProxyData.DISC_BUILTIN_ENDPOINT_PARTICIPANT_DETECTOR;
            if (auxEndp != 0) {
                RemoteReaderAttributes ratt = new RemoteReaderAttributes();
                ratt.expectsInlineQos = false;
                ratt.guid.setGUIDPrefix(pdata.getGUID().getGUIDPrefix());
                ratt.guid.setEntityId(new EntityId(EntityIdEnum.ENTITYID_SPDP_BUILTIN_RTPSPARTICIPANT_READER));
                ratt.endpoint.unicastLocatorList.copy(pdata.getMetatrafficUnicastLocatorList());
                ratt.endpoint.multicastLocatorList.copy(pdata.getMetatrafficMulticastLocatorList());
                ratt.endpoint.reliabilityKind = ReliabilityKind.BEST_EFFORT;
                ratt.endpoint.durabilityKind = DurabilityKind.TRANSIENT_LOCAL;
                pdata.getBuiltinReaders().add(ratt);
                this.m_SPDPWriter.matchedReaderAdd(ratt);
            }

            //Inform EDP of new RTPSParticipant data:
            if (this.m_EDP != null) {
                this.m_EDP.assignRemoteEndpoints(pdata);
            }

            if (this.m_builtin.getWLP() != null) {
                this.m_builtin.getWLP().assignRemoteEndpoints(pdata);
            }
        } finally {
            pdata.getMutex().unlock();
        }
    }

    /**
     * Remove remote endpoints from the participant discovery protocol
     *
     * @param pdata Reference to the ParticipantProxyData to remove
     */
    public void removeRemoteEndpoints(ParticipantProxyData pdata) {
        logger.debug("For RTPSParticipant: " + pdata.getGUID());
        pdata.getMutex().lock();
        try {
            for (RemoteReaderAttributes it : pdata.getBuiltinReaders()) {
                if (it.guid.getEntityId().equals(new EntityId(EntityIdEnum.ENTITYID_SPDP_BUILTIN_RTPSPARTICIPANT_READER)) && this.m_SPDPWriter != null) {
                    this.m_SPDPWriter.matchedReaderRemove(it);
                }
            }
            for (RemoteWriterAttributes it : pdata.getBuiltinWriters()) {
                if (it.guid.getEntityId().equals(new EntityId(EntityIdEnum.ENTITYID_SPDP_BUILTIN_RTPSPARTICIPANT_WRITER)) && this.m_SPDPReader != null) {
                    this.m_SPDPReader.matchedWriterRemove(it);
                }
            }
        } finally {
            pdata.getMutex().unlock();
        }
    }

    /**
     * This method removes a remote RTPSParticipant and all its writers and
     * readers.
     *
     * @param partGUID GUID of the remote RTPSParticipant.
     * @return true if correct.
     */
    public boolean removeRemoteParticipant(GUID partGUID) {
        logger.debug("Removing RemoteParticipant: " + partGUID);
        this.m_SPDPWriter.getMutex().lock();
        try {
            this.m_SPDPReader.getMutex().lock();
            try {
                ParticipantProxyData pdata = null;
                this.m_mutex.lock();
                try {
                    for (ParticipantProxyData pit : this.m_participantProxies) {
                        synchronized (this.m_guardMutex) {
                            if (pit.getGUID().equals(partGUID)) {
                                pdata = pit;
                                this.m_participantProxies.remove(pit);
                                break;
                            }
                        }
                    }
                    if (pdata != null) {
                        pdata.getMutex().lock();
                        try {
                            if (this.m_EDP != null) {
                                for (ReaderProxyData rit : pdata.getReaders()) {
                                    this.m_EDP.unpairReaderProxy(rit);
                                }
                                for (WriterProxyData wit : pdata.getWriters()) {
                                    this.m_EDP.unpairWriterProxy(wit);
                                }
                            }
                            if (this.m_builtin.getWLP() != null) {
                                this.m_builtin.getWLP().removeRemoteEndpoints(pdata);
                            }
                            this.m_EDP.removeRemoteEndpoints(pdata);
                            this.removeRemoteEndpoints(pdata);
                            for (CacheChange it : this.m_SPDPReaderHistory.getChanges()) {
                                if (it.getInstanceHandle().equals(pdata.getKey())) {
                                    this.m_SPDPReaderHistory.removeChange(it);
                                    break;
                                }
                            }
                            logger.info("Removed remote Participant {}", partGUID);
                            return true;
                        } finally {
                            pdata.getMutex().unlock();
                        }
                    }
                } finally {
                    this.m_mutex.unlock();
                }
            } finally {
                this.m_SPDPReader.getMutex().unlock();
            }
        } finally {
            this.m_SPDPWriter.getMutex().unlock();
        }
        return false;
    }

    /**
     * Assert the liveliness of a Remote Participant.
     *
     * @param guidPrefix GuidPrefix of the participant whose liveliness is being
     * asserted.
     */
    public void assertRemoteParticipantLiveliness(GUIDPrefix guidPrefix) {
        this.m_mutex.lock();
        try {
            for (ParticipantProxyData it : this.m_participantProxies) {
                it.getMutex().lock();
                try {
                    if (it.getGUID().getGUIDPrefix().equals(guidPrefix)) {
                        logger.debug("RTPSParticipant " + it.getGUID() + " is Alive");
                        it.setIsAlive(true);
                    }
                } finally {
                    it.getMutex().unlock();
                }
            }
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * Assert the liveliness of a Local Writer.
     *
     * @param kind LivilinessQosPolicyKind to be asserted.
     */
    public void assertLocalWritersLiveliness(LivelinessQosPolicyKind kind) {
        logger.debug("Asserting liveliness of type " + (kind == LivelinessQosPolicyKind.AUTOMATIC_LIVELINESS_QOS ? "AUTOMATIC" : "")
                + (kind == LivelinessQosPolicyKind.MANUAL_BY_PARTICIPANT_LIVELINESS_QOS ? "MANUAL_BY_PARTICIPANT" : ""));
        this.m_mutex.lock();
        try {
            this.m_participantProxies.get(0).getMutex().lock();
            try {
                for (WriterProxyData wit : this.m_participantProxies.get(0).getWriters()) {
                    if (wit.getQos().liveliness.kind == kind) {
                        logger.debug("Local writer " + wit.getGUID().getEntityId() + " marked as ALIVE");
                        wit.setIsAlive(true);
                    }
                }
            } finally {
                this.m_participantProxies.get(0).getMutex().unlock();
            }
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * Assert the liveliness of remote writers.
     *
     * @param guidP GuidPrefix of the participant whose writers liveliness is
     * begin asserted.
     * @param kind LivelinessQosPolicyKind of the writers.
     */
    public void assertRemoteWritersLiveliness(GUIDPrefix guidP, LivelinessQosPolicyKind kind) {
        this.m_mutex.lock();
        try {
            logger.debug("Asserting liveliness of type " + (kind == LivelinessQosPolicyKind.AUTOMATIC_LIVELINESS_QOS ? "AUTOMATIC" : "")
                    + (kind == LivelinessQosPolicyKind.MANUAL_BY_PARTICIPANT_LIVELINESS_QOS ? "MANUAL_BY_PARTICIPANT" : ""));
            for (ParticipantProxyData pit : this.m_participantProxies) {
                pit.getMutex().lock();
                try {
                    for (WriterProxyData wit : pit.getWriters()) {
                        if (wit.getQos().liveliness.kind == kind) {
                            wit.setIsAlive(true);
                            this.m_RTPSParticipant.getParticipantMutex().lock();
                            try {
                                for (RTPSReader rit : this.m_RTPSParticipant.getUserReaders()) {
                                    if (rit.getAttributes().reliabilityKind == ReliabilityKind.RELIABLE) {
                                        StatefulReader sfr = (StatefulReader) rit;
                                        WriterProxy wp = sfr.matchedWriterLookup(wit.getGUID());
                                        if (wp != null) {
                                            wp.assertLiveliness();
                                            continue;
                                        }
                                    }
                                }
                            } finally {
                                this.m_RTPSParticipant.getParticipantMutex().unlock();
                            }
                        }
                        break;
                    }
                } finally {
                    pit.getMutex().unlock();
                }
            }
        } finally {
            this.m_mutex.unlock();
        }
    }

    /**
     * Activate a new Remote Endpoint that has been statically discovered.
     *
     * @param pguid GUID of the participant.
     * @param userDefinedId User Defined ID.
     * @param kind Kind of endpoint.
     * @return true if successful
     */
    public boolean newRemoteEndpointStaticallyDiscovered(GUID pguid, short userDefinedId, EndpointKind kind) {
        ParticipantProxyData pdata = lookupParticipantProxyData(pguid);
        if (pdata != null) {
            if (kind == EndpointKind.WRITER) {
                ((EDPStatic) this.m_EDP).newRemoteWriter(pdata, userDefinedId, new EntityId());
            } else {
                ((EDPStatic) this.m_EDP).newRemoteReader(pdata, userDefinedId, new EntityId());
            }
        }
        return false;
    }

    /**
     * Get a reference to the EDP object.
     *
     * @return reference to the EDP object.
     */
    public EDP getEDP() {
        return this.m_EDP;
    }

    /**
     * Get builtin protocols object
     *
     * @return builtin protocols object
     */
    public BuiltinProtocols getBuiltinProtocols() {
        return this.m_builtin;
    }

    /**
     * Get mutex
     *
     * @return mutex
     */
    public Lock getMutex() {
        return m_mutex;
    }

    /**
     * Get participant proxies
     *
     * @return participant proxies
     */
    public List<ParticipantProxyData> getParticipantProxies() {
        return m_participantProxies;
    }

    /**
     * Get SPDP reader history
     *
     * @return SPDP reader history
     */
    public ReaderHistoryCache getSPDPReaderHistory() {
        return this.m_SPDPReaderHistory;
    }

    /**
     * Get RTPS participant
     *
     * @return RTPS participant
     */
    public RTPSParticipant getRTPSParticipant() {
        return this.m_RTPSParticipant;
    }

    /**
     * Get discovery attributes
     *
     * @return discovery attributes
     */
    public BuiltinAttributes getDiscovery() {
        return this.m_discovery;
    }

}
