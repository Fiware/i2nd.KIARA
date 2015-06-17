package org.fiware.kiara.ps.rtps.participant;

import java.lang.management.LockInfo;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.fiware.kiara.ps.attributes.TopicAttributes;
import org.fiware.kiara.ps.qos.ReaderQos;
import org.fiware.kiara.ps.qos.WriterQos;
import org.fiware.kiara.ps.rtps.Endpoint;
import org.fiware.kiara.ps.rtps.RTPSDomain;
import org.fiware.kiara.ps.rtps.attributes.BuiltinAttributes;
import org.fiware.kiara.ps.rtps.attributes.RTPSParticipantAttributes;
import org.fiware.kiara.ps.rtps.attributes.ReaderAttributes;
import org.fiware.kiara.ps.rtps.attributes.WriterAttributes;
import org.fiware.kiara.ps.rtps.builtin.BuiltinProtocols;
import org.fiware.kiara.ps.rtps.common.EndpointKind;
import org.fiware.kiara.ps.rtps.common.Locator;
import org.fiware.kiara.ps.rtps.common.LocatorKind;
import org.fiware.kiara.ps.rtps.common.LocatorList;
import org.fiware.kiara.ps.rtps.common.ReliabilityKind;
import org.fiware.kiara.ps.rtps.common.TopicKind;
import org.fiware.kiara.ps.rtps.history.ReaderHistoryCache;
import org.fiware.kiara.ps.rtps.history.WriterHistoryCache;
import org.fiware.kiara.ps.rtps.messages.RTPSMessage;
import org.fiware.kiara.ps.rtps.messages.elements.EntityId;
import org.fiware.kiara.ps.rtps.messages.elements.GUID;
import org.fiware.kiara.ps.rtps.messages.elements.GUIDPrefix;
import org.fiware.kiara.ps.rtps.messages.elements.EntityId.EntityIdEnum;
import org.fiware.kiara.ps.rtps.reader.RTPSReader;
import org.fiware.kiara.ps.rtps.reader.ReaderListener;
import org.fiware.kiara.ps.rtps.reader.StatefulReader;
import org.fiware.kiara.ps.rtps.reader.StatelessReader;
import org.fiware.kiara.ps.rtps.resources.ListenResource;
import org.fiware.kiara.ps.rtps.resources.SendResource;
import org.fiware.kiara.ps.rtps.resources.EventResource;
import org.fiware.kiara.ps.rtps.writer.RTPSWriter;
import org.fiware.kiara.ps.rtps.writer.StatefulWriter;
import org.fiware.kiara.ps.rtps.writer.StatelessWriter;
import org.fiware.kiara.ps.rtps.writer.WriterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTPSParticipant {
    
    private static final Logger logger = LoggerFactory.getLogger(RTPSParticipant.class);
    
    private GUID m_guid;
    
    private RTPSParticipantAttributes m_att;
    
    private int idCounter;
    
    private BuiltinProtocols m_builtinProtocols;
    
    private SendResource m_sendResource;
    
    private EventResource m_eventResource;
    
    private List<RTPSWriter> m_userWriterList;
    
    private List<RTPSReader> m_userReaderList;
    
    private List<RTPSWriter> m_allWriterList;
    
    private List<RTPSReader> m_allReaderList;
    
    private List<ListenResource> m_listenResourceList;
    
    private final Lock m_mutex;
    
    private final Semaphore m_resourceSemaphore;
    //private final Object m_resourceSemaphore;
    
    private int m_threadID;
    
    public RTPSParticipant(
            RTPSParticipantAttributes participantAtt, 
            GUIDPrefix guidPrefix,
            RTPSParticipantListener partListener
            ) {

        this.m_guid = new GUID(guidPrefix, new EntityId(EntityIdEnum.ENTITYID_RTPSPARTICIPANT));
        
        this.idCounter = 0;
        this.m_threadID = 0;
        this.m_userWriterList = new ArrayList<RTPSWriter>();
        this.m_userReaderList = new ArrayList<RTPSReader>();
        
        this.m_allWriterList = new ArrayList<RTPSWriter>();
        this.m_allReaderList = new ArrayList<RTPSReader>();
        
        this.m_listenResourceList = new ArrayList<ListenResource>();
        
        //this.m_resourceSemaphore = new Semaphore(0, true);
        this.m_resourceSemaphore = new Semaphore(0, false);
        this.m_mutex = new ReentrantLock(true);
        
        this.m_mutex.lock();
        try {
            
            this.m_att = participantAtt;
            Locator loc = new Locator();
            loc.setPort(participantAtt.defaultSendPort);
            
            this.m_sendResource = new SendResource();
            this.m_sendResource.initSend(this, loc, this.m_att.sendSocketBufferSize, this.m_att.useIPv4ToSend, this.m_att.useIPv6ToSend);
            this.m_eventResource = new EventResource();
            this.m_eventResource.initThread(this);
            boolean hasLocatorsDefined = true;
            if (this.m_att.defaultUnicastLocatorList.isEmpty() && this.m_att.defaultMulticastLocatorList.isEmpty()) {
                hasLocatorsDefined = false;
                Locator newloc = new Locator();
                newloc.setPort(
                    this.m_att.portParameters.portBase + 
                    this.m_att.portParameters.domainIDGain * participantAtt.builtinAtt.domainID + 
                    this.m_att.portParameters.offsetd3 + 
                    this.m_att.portParameters.participantIDGain * this.m_att.participantID
                );
                newloc.setKind(LocatorKind.LOCATOR_KIND_UDPv4);
                this.m_att.defaultUnicastLocatorList.pushBack(newloc);
            }
            
            LocatorList defCopy = new LocatorList(this.m_att.defaultUnicastLocatorList);
            this.m_att.defaultUnicastLocatorList.clear();
            for (Locator lit : defCopy.getLocators()) {
                ListenResource lr = new ListenResource(this, ++this.m_threadID, true);
                if (lr.initThread(this,  lit,  this.m_att.listenSocketBufferSize, false, false)) {
                    this.m_att.defaultUnicastLocatorList = lr.getListenLocators();
                    this.m_listenResourceList.add(lr);
                } 
            }
            
            if (!hasLocatorsDefined) {
                logger.warn(this.m_att.getName() + " Created with NO default Unicast Locator List, adding Locators:");
            }
            
            defCopy = new LocatorList(this.m_att.defaultMulticastLocatorList);
            this.m_att.defaultMulticastLocatorList.clear();
            for (Locator lit : defCopy.getLocators()) {
                ListenResource lr = new ListenResource(this,  ++this.m_threadID, true);
                if (lr.initThread(this, lit, this.m_att.listenSocketBufferSize, true, false)) {
                    this.m_att.defaultMulticastLocatorList = lr.getListenLocators();
                    this.m_listenResourceList.add(lr);
                }
            }
            
            logger.info("RTPSParticipant with guidPrefix " + this.m_guid.getGUIDPrefix());
            this.m_builtinProtocols = new BuiltinProtocols();
            if (!this.m_builtinProtocols.initBuiltinProtocols(this, this.m_att.builtinAtt)) {
                logger.warn("The builtin protocols were not corecctly initialized");
            }
            
        } finally {
            this.m_mutex.unlock();
        }
        
    }
    
    public void destroy() {
        logger.info("Removing RTPSParticipant: " + this.getGUID().toString());
        
        while (this.m_userReaderList.size() > 0) {
            RTPSDomain.removeRTPSReader(this.m_userReaderList.get(0));
        }
        
        while (this.m_userWriterList.size() > 0) {
            RTPSDomain.removeRTPSWriter(this.m_userWriterList.get(0));
        }
        
        // Destroy threads
        for (ListenResource it : this.m_listenResourceList) {
            it.destroy();
        }
        
    }
    
    public RTPSWriter createWriter(WriterAttributes watt, WriterHistoryCache history, WriterListener listener, EntityId entityId, boolean isBuiltin) {
        
        String type = watt.endpointAtt.reliabilityKind == ReliabilityKind.RELIABLE ? "RELIABLE" : "BEST_EFFORT";
        logger.info("Participant of type " + type);
        
        EntityId entId = new EntityId();
        if (entityId.equals(new EntityId())) { // Unknown
            if (watt.endpointAtt.topicKind == TopicKind.NO_KEY) {
                entId.setValue(3, (byte) 0x03);
            } else if (watt.endpointAtt.topicKind == TopicKind.WITH_KEY) {
                entId.setValue(3, (byte) 0x02);
            }
            
            int idnum;
            if (watt.endpointAtt.getEntityID() > 0) {
                idnum = watt.endpointAtt.getEntityID();
            } else {
                idnum = ++this.idCounter;
            }
            
            byte[] bytes = ByteBuffer.allocate(4).putInt(idnum).array();
            for (int i=0; i < 3; ++i) {
                entId.setValue(2-i, bytes[i]);
            }
            
            if (this.existsEntityId(entId, EndpointKind.WRITER)) {
                logger.error("A writer with the same entityId already exists in this RTPSParticipant");
                return null;
            }
        } else {
            entId = entityId;
        }
        
        if (!watt.endpointAtt.unicastLocatorList.isValid()) {
            logger.error("Unicast Locator List for Writer contains invalid Locator.");
            return null;
        }
        
        if (!watt.endpointAtt.multicastLocatorList.isValid()) {
            logger.error("Multicast Locator List for Writer contains invalid Locator.");
            return null;
        }
        
        RTPSWriter writer;
        GUID guid = new GUID(this.m_guid.getGUIDPrefix(), entId);

        if (watt.endpointAtt.reliabilityKind == ReliabilityKind.BEST_EFFORT) {
            writer = new StatelessWriter(this, guid, watt, history, listener);
        } else {
            writer = new StatefulWriter(this, guid, watt, history, listener);
        }
        
        if (watt.endpointAtt.reliabilityKind == ReliabilityKind.RELIABLE) {
            if (!assignEndpointListenResources(writer, isBuiltin)) {
                return null;
            }
        }
        
        this.m_mutex.lock();
        try {
            this.m_allWriterList.add(writer);
            if (!isBuiltin) {
                this.m_userWriterList.add(writer);
            }
        } finally {
            this.m_mutex.unlock();
        }
        
        return writer;
    }
    
    public RTPSReader createReader(ReaderAttributes ratt, ReaderHistoryCache history, ReaderListener listener, EntityId entityId, boolean isBuiltin) {
        
        String type = ratt.endpointAtt.reliabilityKind == ReliabilityKind.RELIABLE ? "RELIABLE" : "BEST_EFFORT";
        logger.info("Participant of type " + type);
        
        EntityId entId = new EntityId();
        
        if (entityId == new EntityId()) {
            if (ratt.endpointAtt.topicKind == TopicKind.NO_KEY) {
                entId.setValue(3, (byte) 0x04); 
            } else if (ratt.endpointAtt.topicKind == TopicKind.WITH_KEY) {
                entId.setValue(3, (byte) 0x07);
            }
            
            int idnum;
            if (ratt.endpointAtt.getEntityID() > 0) {
                idnum = ratt.endpointAtt.getEntityID();
            } else {
                idnum = ++this.idCounter;
            }
            
            byte[] bytes = ByteBuffer.allocate(4).putInt(idnum).array();
            for (int i=0; i < 3; ++i) {
                entId.setValue(2-i, bytes[i]);
            }
            
            if (this.existsEntityId(entId, EndpointKind.READER)) { // TODO Check this
                logger.error("A reader with the same entityId already exists in this RTPSParticipant");
                return null;
            }
        } else {
            entId = entityId;
        }
        
        if (!ratt.endpointAtt.unicastLocatorList.isValid()) {
            logger.error("Unicast Locator List for Reader contains invalid Locator.");
            return null;
        }
        
        if (!ratt.endpointAtt.multicastLocatorList.isValid()) {
            logger.error("Multicast Locator List for Reader contains invalid Locator.");
            return null;
        }
        
        RTPSReader reader = null;
        GUID guid = new GUID(this.m_guid.getGUIDPrefix(), entId);
        
        if (ratt.endpointAtt.reliabilityKind == ReliabilityKind.BEST_EFFORT) {
            reader = new StatelessReader(this, guid, ratt, history, listener);
        } else if (ratt.endpointAtt.reliabilityKind == ReliabilityKind.RELIABLE) {
            reader = new StatefulReader(this, guid, ratt, history, listener);
        }
        if (reader == null) {
            return null;
        }
        
        if (isBuiltin) {
            reader.setTrustedWriter(this.createTrustedWriter(reader.getGuid().getEntityId()));
        }
        
        if (!assignEndpointListenResources(reader, isBuiltin)) {
            return null;
        }
        
        this.m_mutex.lock();
        try {
            this.m_allReaderList.add(reader);
            if (!isBuiltin) {
                this.m_userReaderList.add(reader);
            }
            
        } finally {
            this.m_mutex.unlock();
        }
        
        return reader;
    }
    
    public boolean registerWriter(RTPSWriter writer, TopicAttributes topicAtt, WriterQos wqos) {
        return this.m_builtinProtocols.addLocalWriter(writer, topicAtt, wqos);
    }
    
    public boolean registerReader(RTPSReader reader, TopicAttributes topicAtt, ReaderQos rqos) {
        return this.m_builtinProtocols.addLocalReader(reader, topicAtt, rqos);
    }
    
    public boolean updateLocalWriter(RTPSWriter writer, WriterQos wqos) {
        return this.m_builtinProtocols.updateLocalWriter(writer, wqos);
    }
    
    public boolean updateLocalReader(RTPSReader reader, ReaderQos rqos) {
        return this.m_builtinProtocols.updateLocalReader(reader, rqos);
    }
    
    /*
     * AUXILIARY METHODS
     */
    
    private EntityId createTrustedWriter(EntityId reader) {
        if (reader.equals(new EntityId(EntityIdEnum.ENTITYID_SPDP_BUILTIN_RTPSPARTICIPANT_READER))) {
            return new EntityId(EntityIdEnum.ENTITYID_SPDP_BUILTIN_RTPSPARTICIPANT_WRITER);
        }
        
        if (reader.equals(new EntityId(EntityIdEnum.ENTITYID_SEDP_BUILTIN_PUBLICATIONS_READER))) {
            return new EntityId(EntityIdEnum.ENTITYID_SEDP_BUILTIN_PUBLICATIONS_WRITER);
        }
        
        if (reader.equals(new EntityId(EntityIdEnum.ENTITYID_SEDP_BUILTIN_SUBSCRIPTIONS_READER))) {
            return new EntityId(EntityIdEnum.ENTITYID_SEDP_BUILTIN_SUBSCRIPTIONS_WRITER);
        }
        
        if (reader.equals(new EntityId(EntityIdEnum.ENTITYID_P2P_BUILTIN_RTPSPARTICIPANT_MESSAGE_READER))) {
            return new EntityId(EntityIdEnum.ENTITYID_P2P_BUILTIN_RTPSPARTICIPANT_MESSAGE_WRITER);
        }
        
        return new EntityId();
    }
    
    public boolean existsEntityId(EntityId ent, EndpointKind kind) {
        if (kind == EndpointKind.WRITER) {
            for (RTPSWriter it : this.m_userWriterList) {
                if (ent.equals(it.getGuid().getEntityId())) {
                    return true;
                }
            }
        } else {
            for (RTPSReader it : this.m_userReaderList) {
                if (ent.equals(it.getGuid().getEntityId())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /*
     * LISTEN RESOURCE METHODS
     */
    
    private boolean assignEndpointListenResources(Endpoint endp, boolean isBuiltin) {
        boolean valid = true;
        
        boolean unicastEmpty = endp.getAttributes().unicastLocatorList.isEmpty();
        boolean multicastEmpty = endp.getAttributes().multicastLocatorList.isEmpty();
        
        LocatorList uniList = new LocatorList();
        LocatorList multiList = new LocatorList();
        
        if (!unicastEmpty) {
            uniList = endp.getAttributes().unicastLocatorList;
        }
        if (!multicastEmpty) {
            multiList = endp.getAttributes().multicastLocatorList;
        }
        
        // UNICAST
        
        if (unicastEmpty && !isBuiltin && multicastEmpty) {
            String auxStr = endp.getAttributes().endpointKind == EndpointKind.WRITER ? "WRITER" : "READER";
            logger.info("Adding default Locator list to this " + auxStr);
            valid &= assignEndpointToLocatorList(endp, this.m_att.defaultUnicastLocatorList, false, false);
            this.m_mutex.lock();
            try {
                endp.getAttributes().unicastLocatorList = this.m_att.defaultUnicastLocatorList;
            } finally {
                this.m_mutex.unlock();
            }
        } else {
            valid &= assignEndpointToLocatorList(endp, uniList, false, !isBuiltin);
            this.m_mutex.lock();
            try {
                endp.getAttributes().unicastLocatorList = uniList;
            } finally {
                this.m_mutex.unlock();
            }
        }
        
        // MULTICAST
        
        if (multicastEmpty && !isBuiltin && unicastEmpty) {
            valid &= assignEndpointToLocatorList(endp, this.m_att.defaultMulticastLocatorList, true, false);
            this.m_mutex.lock();
            try {
                endp.getAttributes().multicastLocatorList = this.m_att.defaultMulticastLocatorList;
            } finally {
                this.m_mutex.unlock();
            }
        } else {
            valid &= assignEndpointToLocatorList(endp, multiList, true, !isBuiltin);
            this.m_mutex.lock();
            try {
                endp.getAttributes().multicastLocatorList = multiList;
            } finally {
                this.m_mutex.unlock();
            }
        }
        
        return valid;
    }
    
    private boolean assignEndpointToLocatorList(Endpoint endp, LocatorList list, boolean isMulti, boolean isFixed) {
        boolean valid = true;
        LocatorList finalList = new LocatorList();
        boolean added = false;
        
        for (Locator lit : list.getLocators()) {
            added = false;
            this.m_mutex.lock();
            
            try {
                for (ListenResource it : this.m_listenResourceList) {
                    if (it.isListeningTo(lit)) {
                        it.addAssociatedEndpoint(endp);
                        LocatorList locList = it.getListenLocators();
                        finalList.pushBack(locList);
                        added = true;
                    }
                }
                if (added) {
                    continue;
                }
                
                ListenResource lr = new ListenResource(this, ++this.m_threadID,  false);
                if (lr.initThread(this, lit, this.m_att.listenSocketBufferSize, isMulti, isFixed)) {
                    lr.addAssociatedEndpoint(endp);
                    LocatorList locList = lr.getListenLocators();
                    finalList.pushBack(locList);
                    added = true;
                } else {
                    valid &= false;
                }
            } finally {
                this.m_mutex.unlock();
            }
        }
        
        if (valid && added) {
            list = finalList;
        }
        
        return valid;
    }
    
    public boolean deleteUserEndpoint(Endpoint endpoint) {
        boolean found = false;
        if (endpoint.getAttributes().endpointKind == EndpointKind.WRITER) {
            this.m_mutex.lock();
            try {
                for (RTPSWriter wit : this.m_userWriterList) {
                    if (wit.getGuid().getEntityId().equals(endpoint.getGuid().getEntityId())) {
                        this.m_userWriterList.remove(wit);
                        found = true;
                        break;
                    }
                }
            } finally {
                this.m_mutex.unlock();
            }
        } else {
            this.m_mutex.lock();
            try {
                for (RTPSReader rit : this.m_userReaderList) {
                    if (rit.getGuid().getEntityId().equals(endpoint.getGuid().getEntityId())) {
                        this.m_userReaderList.remove(rit);
                        found = true;
                        break;
                    }
                }
            } finally {
                this.m_mutex.unlock();
            }
        }
        
        if (!found) {
            return false;
        }
        
        // Remove from builtin protocols
        if (endpoint.getAttributes().endpointKind == EndpointKind.WRITER) {
            this.m_builtinProtocols.removeLocalWriter((RTPSWriter) endpoint);
        } else {
            this.m_builtinProtocols.removeLocalReader((RTPSReader) endpoint);
        }
        
        // Remove from threadListenList
        this.m_mutex.lock();
        try {
            
            for (ListenResource lrit : this.m_listenResourceList) {
                lrit.removeAssociatedEndpoint(endpoint);
            }
            
            boolean continueRemoving = true;
            while (continueRemoving) {
                continueRemoving = false;
                for (ListenResource lrit : this.m_listenResourceList) {
                    if (lrit.hasAssociatedEndpoints() && !lrit.isDefaultListenResource()) {
                        this.m_listenResourceList.remove(lrit);
                        continueRemoving = true;
                        break;
                    }
                }
            }
            
        } finally {
            this.m_mutex.unlock();
        }
        
        return true;
        
    }
    
    public void sendSync(RTPSMessage msg, Locator loc) {
        this.m_sendResource.sendSync(msg, loc);
    }
    
    public void announceRTPSParticipantState() {
        this.m_builtinProtocols.announceRTPSParticipantState();
    }
    
    public void stopRTPSParticipantAnnouncement() {
        this.m_builtinProtocols.stopRTPSParticipantAnnouncement();
    }
    
    public void resetRTPSParticipantAnnouncement() {
        this.m_builtinProtocols.resetRTPSParticipantAnnouncement();
    }
    
    public void looseNextChange() {
        this.m_sendResource.looseNextChange();
    }
    
    public boolean newRemoteEndpointDiscovered(GUID pguid, short userDefienedId, EndpointKind kind) {
        if (this.m_att.builtinAtt.useStaticEDP == false) {
            logger.warn("Remote Endpoints can only be activated with static discovery protocol");
            return false;
        }
        return this.m_builtinProtocols.getPDP().newRemoteEndpointStaticallyDiscovered(pguid, userDefienedId, kind);
    }
    
    public void resourceSemaphorePost() {
        synchronized(this.m_resourceSemaphore) {
            if (this.m_resourceSemaphore != null) {
                this.m_resourceSemaphore.notify();
            }
        }
    }
    
    public void resourceSemaphoreWait() {
        synchronized(this.m_resourceSemaphore) {
            if (this.m_resourceSemaphore != null) {
                try {
                    this.m_resourceSemaphore.wait();
                } catch (InterruptedException e) {
                    // TODO Handle exception
                    e.printStackTrace();
                }
            }
        }
    }
    
    public GUID getGUID() {
        return this.m_guid;
    }
    
    public void setGUID(GUID guid) {
        this.m_guid = guid;
    }

    public int getRTPSParticipantID() {
        return m_att.participantID;
    }

    

}