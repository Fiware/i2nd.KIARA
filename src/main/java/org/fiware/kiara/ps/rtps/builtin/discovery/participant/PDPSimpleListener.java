package org.fiware.kiara.ps.rtps.builtin.discovery.participant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

import org.fiware.kiara.ps.qos.parameter.ParameterId;
import org.fiware.kiara.ps.rtps.builtin.data.ParticipantProxyData;
import org.fiware.kiara.ps.rtps.builtin.discovery.participant.timedevent.RemoteParticipantLeaseDuration;
import org.fiware.kiara.ps.rtps.common.MatchingInfo;
import org.fiware.kiara.ps.rtps.history.CacheChange;
import org.fiware.kiara.ps.rtps.messages.common.types.ChangeKind;
import org.fiware.kiara.ps.rtps.messages.elements.GUID;
import org.fiware.kiara.ps.rtps.messages.elements.InstanceHandle;
import org.fiware.kiara.ps.rtps.messages.elements.SerializedPayload;
import org.fiware.kiara.ps.rtps.participant.DiscoveryStatus;
import org.fiware.kiara.ps.rtps.participant.RTPSParticipantDiscoveryInfo;
import org.fiware.kiara.ps.rtps.reader.RTPSReader;
import org.fiware.kiara.ps.rtps.reader.ReaderListener;
import org.fiware.kiara.serialization.impl.BinaryInputStream;
import org.fiware.kiara.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class PDPSimpleListener, specification of SubscriberListener used by the SPDP
 * to perform the History check when a new message is received. This class is
 * implemented in order to use the same structure than with any other
 * RTPSReader.
 */
public class PDPSimpleListener extends ReaderListener {

    /**
     * Pointer to the associated m_SPDP;
     */
    private final PDPSimple m_SPDP;

    /**
     * {@link ParticipantProxyData} associated to the {@link PDPSimpleListener}
     */
    private final ParticipantProxyData m_participantProxyData;

    /**
     * Logging object
     */
    private static final Logger logger = LoggerFactory.getLogger(PDPSimpleListener.class);

    /**
     * Main Constructor
     *
     * @param pdpSimple PDPSimple object
     */
    public PDPSimpleListener(PDPSimple pdpSimple) {
        this.m_SPDP = pdpSimple;
        this.m_participantProxyData = new ParticipantProxyData();
    }

    /**
     * Method to be executed when a new {@link RTPSReader} has matched
     */
    @Override
    public void onReaderMatched(RTPSReader reader, MatchingInfo info) {
        // Do Nothing
    }

    /**
     * New added cache
     * @param reader The {@link RTPSReader} who receives the {@link CacheChange}
     * @param change_in The received {@link CacheChange}
     */
    @Override
    public void onNewCacheChangeAdded(RTPSReader reader, CacheChange change_in) {
        try {
            CacheChange change = change_in;
            logger.debug("PDP Message Received");

            Lock guard = reader.getMutex();
            guard.lock();
            try {

                if (change.getInstanceHandle().equals(new InstanceHandle())) {
                    if (!this.getKey(change)) {
                        logger.warn("Problem getting the key of the change, removing.");
                        this.m_SPDP.getSPDPReaderHistory().removeChange(change);
                        return;
                    }
                }

                if (change.getKind() == ChangeKind.ALIVE) {
                    // Load information in temporal RTPSParticipant PROXY DATA
                    this.m_participantProxyData.clear();
                    if (this.m_participantProxyData.readFromCDRMessage(change)) {
                        // If the message was correctly deserialized
                        // Check if same RTPSParticipant
                        change.setInstanceHandle(this.m_participantProxyData.getKey());
                        if (this.m_participantProxyData.getGUID().equals(this.m_SPDP.getRTPSParticipant().getGUID())) {
                            logger.debug("Message from own RTPSParticipant, removing");
                            this.m_SPDP.getSPDPReaderHistory().removeChange(change);
                            return;
                        }

                        //this.m_lock.lock();
                        this.m_SPDP.getSPDPReaderHistory().m_mutex.lock();
                        try {
                            for (CacheChange chit : this.m_SPDP.getSPDPReaderHistory().getChanges()) {
                                if (chit.getInstanceHandle().equals(change.getInstanceHandle())
                                        && chit.getSequenceNumber().isLowerThan(change.getSequenceNumber())) {
                                    this.m_SPDP.getSPDPReaderHistory().removeChange(chit);
                                    break;
                                }
                            }
                        } finally {
                            this.m_SPDP.getSPDPReaderHistory().m_mutex.unlock();
                        }

                        // Look if is an updated information
                        ParticipantProxyData pData = new ParticipantProxyData();
                        boolean found = false;
                        this.m_SPDP.getMutex().lock();
                        try {
                            for (ParticipantProxyData it : this.m_SPDP.getParticipantProxies()) {
                                if (this.m_participantProxyData.getKey().equals(it.getKey())) {
                                    found = true;
                                    pData = it;
                                    break;
                                }
                            }

                            RTPSParticipantDiscoveryInfo info = new RTPSParticipantDiscoveryInfo();
                            info.guid = this.m_participantProxyData.getGUID();
                            info.RTPSParticipantName = this.m_participantProxyData.getParticipantName();
                            info.propertyList = new ArrayList<Pair<String, String>>(this.m_participantProxyData.getProperties().getProperties());
                            info.userData = new ArrayList<Byte>(this.m_participantProxyData.getUserData());

                            if (!found) {
                                info.status = DiscoveryStatus.DISCOVERED_PARTICIPANT;
                                // If proxyData is not found, a new one has been created
                                pData.getMutex().lock();
                                try {
                                    pData.copy(this.m_participantProxyData);
                                    pData.setIsAlive(true);
                                    this.m_SPDP.getParticipantProxies().add(pData);
                                    //pData.setLeaseDurationTimer(new RemoteParticipantLeaseDuration(this.m_SPDP, pData, pData.getLeaseDuration().toMilliSecondsDouble()));
                                    // pData.getLeaseDurationTimer().restartTimer();
                                    
                                    pData.setLeaseDurationTimer(new RemoteParticipantLeaseDuration(this.m_SPDP, pData, pData.getLeaseDuration().toMilliSecondsDouble()));
                                    
                                    
                                    this.m_SPDP.assignRemoteEndpoints(pData);
                                    this.m_SPDP.announceParticipantState(false);
                                } finally {
                                    pData.getMutex().unlock();
                                }
                            } else {
                                info.status = DiscoveryStatus.CHANGED_QOS_RTPSPARTICIPANT;
                                pData.getMutex().lock();
                                try {
                                    pData.updateData(this.m_participantProxyData);
                                    if (this.m_SPDP.getDiscovery().useStaticEDP) {
                                        if (this.m_SPDP.getEDP() != null) { // TODO This should not be necessary
                                            this.m_SPDP.getEDP().assignRemoteEndpoints(this.m_participantProxyData);
                                        }
                                    }
                                } finally {
                                    pData.getMutex().unlock();
                                }
                            }

                            if (this.m_SPDP.getRTPSParticipant().getListener() != null) {
                                this.m_SPDP.getRTPSParticipant().getListener().onRTPSParticipantDiscovery(this.m_SPDP.getRTPSParticipant(), info);
                            }
                            pData.setIsAlive(true);

                        } finally {
                            this.m_SPDP.getMutex().unlock();
                        }

                    }

                } else {
                    GUID guid = new GUID();
                    guid = change.getInstanceHandle().toGUID();
                    this.m_SPDP.removeRemoteParticipant(guid);
                    RTPSParticipantDiscoveryInfo info = new RTPSParticipantDiscoveryInfo();
                    info.status = DiscoveryStatus.REMOVED_PARTICIPANT;
                    info.guid = guid;
                    if (this.m_SPDP.getRTPSParticipant().getListener() != null) {
                        this.m_SPDP.getRTPSParticipant().getListener().onRTPSParticipantDiscovery(this.m_SPDP.getRTPSParticipant(), info);
                    }
                }
                //            } 
                //                finally {
                //                    mutex.unlock();
                //                }

            } finally {
                guard.unlock();
            }

            return;
        } catch (java.util.concurrent.RejectedExecutionException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    /**
     * Get the key of a CacheChange_t
     * @param change Pointer to the CacheChange_t
     * @return True on success
     */
    private boolean getKey(CacheChange change) {
        SerializedPayload pl = change.getSerializedPayload();
        pl.updateSerializer();
        BinaryInputStream bis = new BinaryInputStream(pl.getBuffer());
        short pid;
        short plength;
        try {
            while (bis.getPosition() < pl.getBuffer().length) {
                pid = pl.getSerializer().deserializeUI16(bis, "");
                plength = pl.getSerializer().deserializeUI16(bis, "");
                if (pid == ParameterId.PID_SENTINEL.getValue()) {
                    break;
                }
                if (pid == ParameterId.PID_PARTICIPANT_GUID.getValue()) {
                    GUID guid = new GUID();
                    guid.deserialize(pl.getSerializer(), bis, "");
                    return true;
                }
                if (pid == ParameterId.PID_KEY_HASH.getValue()) {

                    return true;
                }
                bis.skipBytes(plength);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

}
