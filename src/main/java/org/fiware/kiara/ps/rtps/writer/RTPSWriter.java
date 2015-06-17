package org.fiware.kiara.ps.rtps.writer;

import org.fiware.kiara.ps.rtps.Endpoint;
import org.fiware.kiara.ps.rtps.attributes.EndpointAttributes;
import org.fiware.kiara.ps.rtps.attributes.RemoteReaderAttributes;
import org.fiware.kiara.ps.rtps.attributes.WriterAttributes;
import org.fiware.kiara.ps.rtps.common.TopicKind;
import org.fiware.kiara.ps.rtps.history.CacheChange;
import org.fiware.kiara.ps.rtps.history.WriterHistoryCache;
import org.fiware.kiara.ps.rtps.messages.RTPSMessage;
import org.fiware.kiara.ps.rtps.messages.RTPSMessageBuilder;
import org.fiware.kiara.ps.rtps.messages.common.types.ChangeKind;
import org.fiware.kiara.ps.rtps.messages.common.types.RTPSEndian;
import org.fiware.kiara.ps.rtps.messages.elements.GUID;
import org.fiware.kiara.ps.rtps.messages.elements.InstanceHandle;
import org.fiware.kiara.ps.rtps.messages.elements.SequenceNumber;
import org.fiware.kiara.ps.rtps.participant.RTPSParticipant;
import org.fiware.kiara.ps.rtps.writer.timedevent.UnsentChangesNotEmptyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eprosima.log.Log;

public abstract class RTPSWriter extends Endpoint {

    protected boolean m_pushMode;
    
    //protected RTPSMessageGroup;
    
    protected RTPSMessage m_rtpsMessage;
    
    protected boolean m_livelinessAsserted;
    
    protected UnsentChangesNotEmptyEvent m_unsentChangesNotEmpty;
    
    protected WriterHistoryCache m_history;
    
    protected WriterListener m_listener;
    
    private static final Logger logger = LoggerFactory.getLogger(RTPSWriter.class);

    public RTPSWriter(RTPSParticipant participant, GUID guid, WriterAttributes att, WriterHistoryCache history, WriterListener listener) {
        super(participant, guid, att.endpointAtt);
        this.m_livelinessAsserted = false;
        this.m_history = history;
        this.m_history.m_writer = this;
        this.m_listener = listener;
        this.initHeader();
        logger.info("RTPSWriter created");
    }
    
    private void initHeader() {
        m_rtpsMessage =  RTPSMessageBuilder.createMessage(this.m_history.getAttributes().payloadMaxSize, RTPSEndian.LITTLE_ENDIAN); // TODO Set endianned defined by user
        RTPSMessageBuilder.addHeader(m_rtpsMessage, this.m_guid.getGUIDPrefix());
       
    }

    public CacheChange newChange(ChangeKind changeKind, InstanceHandle handle) {
        logger.info("Creating new change");
        CacheChange ch = this.m_history.reserveCache();
        
        if (ch == null) {
            logger.warn("Problem reserving Cache from the History");
            return null;
        }
        
        ch.setKind(changeKind);
        
        if (this.m_att.topicKind == TopicKind.WITH_KEY && handle == null) {
            logger.warn("Changes in KEYED Writers need a valid instanceHandle");
        }
        
        ch.setInstanceHandle(handle);
        ch.setWriterGUID(this.m_guid);
        
        return ch;
    }
    
    public SequenceNumber getSeqNumMin() {
        CacheChange change = this.m_history.getMinChange();
        if (change != null) {
            return change.getSequenceNumber();
        } else {
            return new SequenceNumber();
        }
    }
    
    public SequenceNumber getSeqNumMax() {
        CacheChange change = this.m_history.getMaxChange();
        if (change != null) {
            return change.getSequenceNumber();
        } else {
            return new SequenceNumber();
        }
    }
    
    public int getTypeMaxSerialized() {
        return this.m_history.getTypeMaxSerialized();
    }
    
    public boolean isAckedByAll(CacheChange change) {
        return true;
    }
    
    public abstract boolean matchedReaderAdd(RemoteReaderAttributes ratt);
    
    public abstract boolean matchedReaderRemove(RemoteReaderAttributes ratt);
    
    public abstract boolean matchedReaderIsMatched(RemoteReaderAttributes ratt);
    
    public abstract void updateAttributes(WriterAttributes att);
    
    public abstract void unsentChangesNotEmpty();

    public abstract void unsentChangeAddedToHistory(CacheChange change);

    public abstract boolean changeRemovedByHistory(CacheChange change);
    
    public void setLivelinessAsserted(boolean value) {
        this.m_livelinessAsserted = value;
    }
    
    public boolean getLivelinessAsserted() {
        return this.m_livelinessAsserted;
    }
}