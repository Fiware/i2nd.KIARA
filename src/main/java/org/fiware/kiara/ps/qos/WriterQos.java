package org.fiware.kiara.ps.qos;

import org.fiware.kiara.ps.qos.policies.DeadLineQosPolicy;
import org.fiware.kiara.ps.qos.policies.DestinationOrderQosPolicy;
import org.fiware.kiara.ps.qos.policies.DestinationOrderQosPolicyKind;
import org.fiware.kiara.ps.qos.policies.DurabilityQosPolicy;
import org.fiware.kiara.ps.qos.policies.DurabilityQosPolicyKind;
import org.fiware.kiara.ps.qos.policies.DurabilityServiceQosPolicy;
import org.fiware.kiara.ps.qos.policies.GroupDataQosPolicy;
import org.fiware.kiara.ps.qos.policies.LatencyBudgetQosPolicy;
import org.fiware.kiara.ps.qos.policies.LifespanQosPolicy;
import org.fiware.kiara.ps.qos.policies.LivelinessQosPolicy;
import org.fiware.kiara.ps.qos.policies.LivelinessQosPolicyKind;
import org.fiware.kiara.ps.qos.policies.OwnershipQosPolicy;
import org.fiware.kiara.ps.qos.policies.OwnershipQosPolicyKind;
import org.fiware.kiara.ps.qos.policies.OwnershipStrengthQosPolicy;
import org.fiware.kiara.ps.qos.policies.PartitionQosPolicy;
import org.fiware.kiara.ps.qos.policies.PresentationQosPolicy;
import org.fiware.kiara.ps.qos.policies.ReliabilityQosPolicy;
import org.fiware.kiara.ps.qos.policies.ReliabilityQosPolicyKind;
import org.fiware.kiara.ps.qos.policies.TimeBasedFilterQosPolicy;
import org.fiware.kiara.ps.qos.policies.TopicDataQosPolicy;
import org.fiware.kiara.ps.qos.policies.UserDataQosPolicy;
import org.fiware.kiara.ps.rtps.messages.elements.Timestamp;

public class WriterQos {

    public DurabilityQosPolicy durability;

    public DurabilityServiceQosPolicy durabilityService;

    public DeadLineQosPolicy deadline;

    public LatencyBudgetQosPolicy latencyBudget;

    public LivelinessQosPolicy liveliness;

    public ReliabilityQosPolicy reliability;

    public LifespanQosPolicy lifespan;

    public UserDataQosPolicy userData;

    public TimeBasedFilterQosPolicy timeBasedFilter;

    public OwnershipQosPolicy ownership;

    public OwnershipStrengthQosPolicy ownershipStrength;

    public DestinationOrderQosPolicy destinationOrder;

    public PresentationQosPolicy presentation;

    public PartitionQosPolicy partition;

    public TopicDataQosPolicy topicData;

    public GroupDataQosPolicy groupData;

    public WriterQos() {
        this.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        this.durability.kind = DurabilityQosPolicyKind.TRANSIENT_LOCAL_DURABILITY_QOS;
        
        this.durability = new DurabilityQosPolicy();
        this.durabilityService = new DurabilityServiceQosPolicy();
        this.deadline = new DeadLineQosPolicy();
        this.latencyBudget = new LatencyBudgetQosPolicy();
        this.liveliness = new LivelinessQosPolicy();
        this.reliability = new ReliabilityQosPolicy();
        this.lifespan = new LifespanQosPolicy();
        this.userData = new UserDataQosPolicy();
        this.timeBasedFilter = new TimeBasedFilterQosPolicy();
        this.ownership = new OwnershipQosPolicy();
        this.ownershipStrength = new OwnershipStrengthQosPolicy();
        this.destinationOrder = new DestinationOrderQosPolicy();
        this.presentation = new PresentationQosPolicy();
        this.partition = new PartitionQosPolicy();
        this.topicData = new TopicDataQosPolicy();
        this.groupData = new GroupDataQosPolicy();
    }

    public void setQos(WriterQos qos, boolean firstTime) {
        if(firstTime) {
                durability = qos.durability;
                durability.parent.hasChanged = true;
        }
        if(firstTime || deadline.period != qos.deadline.period) {
                deadline = qos.deadline;
                deadline.parent.hasChanged = true;
        }
        if(latencyBudget.duration != qos.latencyBudget.duration) {
                latencyBudget = qos.latencyBudget;
                latencyBudget.parent.hasChanged = true;
        }
        if(!liveliness.leaseDuration.equals(qos.liveliness.leaseDuration)) {
                liveliness.leaseDuration = qos.liveliness.leaseDuration;
                liveliness.parent.hasChanged = true;
        }
        if(firstTime) {
                liveliness = qos.liveliness;
                liveliness.parent.hasChanged = true;
        }
        if(firstTime) {
                reliability = qos.reliability;
                reliability.parent.hasChanged = true;
        }
        if(firstTime) {
                ownership = qos.ownership;
                ownership.parent.hasChanged = true;
        }
        if(destinationOrder.kind != qos.destinationOrder.kind ) {
                destinationOrder = qos.destinationOrder;
                destinationOrder.parent.hasChanged = true;
        }
        if (!userData.getDataBuf().equals(qos.userData.getDataBuf())) {
                userData = qos.userData;
                userData.parent.hasChanged = true;
        }
        if(firstTime || !timeBasedFilter.minimumSeparation.equals(qos.timeBasedFilter.minimumSeparation)) {
                timeBasedFilter = qos.timeBasedFilter;
                timeBasedFilter.parent.hasChanged = true;
        }
        if(firstTime || presentation.accessScope != qos.presentation.accessScope ||
                        presentation.coherentAccess != qos.presentation.coherentAccess ||
                        presentation.orderedAccess != qos.presentation.orderedAccess
        ) {
                presentation = qos.presentation;
                presentation.parent.hasChanged = true;
        }
        if(qos.partition.getNames().size() > 0) {
                partition = qos.partition;
                partition.parent.hasChanged = true;
        }

        if (!topicData.getValue().equals(qos.topicData.getValue())) {
                topicData = qos.topicData;
                topicData.parent.hasChanged = true;
        }
        if (!groupData.getValue().equals(qos.groupData.getValue())) {
                groupData = qos.groupData;
                groupData.parent.hasChanged = true;
        }
        if(firstTime || durabilityService.kind != qos.durabilityService.kind ||
                        durabilityService.historyDepth != qos.durabilityService.historyDepth ||
                        durabilityService.maxInstances != qos.durabilityService.maxInstances ||
                        durabilityService.maxSamples != qos.durabilityService.maxSamples||
                        durabilityService.maxSamplesPerInstance != qos.durabilityService.maxSamplesPerInstance ||
                        durabilityService.serviceCleanupDelay != qos.durabilityService.serviceCleanupDelay
        ) {
                durabilityService = qos.durabilityService;
                durabilityService.parent.hasChanged = true;
        }
        if(lifespan.duration != qos.lifespan.duration ) {
                lifespan = qos.lifespan;
                lifespan.parent.hasChanged = true;
        }
        if(qos.ownershipStrength.value !=ownershipStrength.value) {
                ownershipStrength = qos.ownershipStrength;
                ownershipStrength.parent.hasChanged = true;
        }
    }

    public boolean checkQos() {
        if(durability.kind == DurabilityQosPolicyKind.TRANSIENT_DURABILITY_QOS) {
            System.out.println("TRANSIENT Durability not supported"); // TODO Log this
            return false;
        }
        if(durability.kind == DurabilityQosPolicyKind.PERSISTENT_DURABILITY_QOS) {
            System.out.println("PERSISTENT Durability not supported"); // TODO Log this
            return false;
        }
        if(destinationOrder.kind == DestinationOrderQosPolicyKind.BY_SOURCE_TIMESTAMP_DESTINATIONORDER_QOS) {
            System.out.println("BY SOURCE TIMESTAMP DestinationOrder not supported"); // TODO Log this
            return false;
        }
        if(reliability.kind == ReliabilityQosPolicyKind.BEST_EFFORT_RELIABILITY_QOS && ownership.kind == OwnershipQosPolicyKind.EXCLUSIVE_OWNERSHIP_QOS) {
            System.out.println("BEST_EFFORT incompatible with EXCLUSIVE ownership"); // TODO Log this
            return false;
        }
        if(liveliness.kind == LivelinessQosPolicyKind.AUTOMATIC_LIVELINESS_QOS || liveliness.kind == LivelinessQosPolicyKind.MANUAL_BY_PARTICIPANT_LIVELINESS_QOS) {
            if(liveliness.leaseDuration.isLowerThan(new Timestamp().timeInfinite()) && liveliness.leaseDuration.isLowerOrEqualThan(liveliness.announcementPeriod)) {
                System.out.println("WRITERQOS: LeaseDuration <= announcement period."); // TODO Log this
                return false;
            }
        }
        return true;
    }

    public boolean canQosBeUpdated(WriterQos qos) {
        boolean updatable = true;
        if(durability.kind != qos.durability.kind) {
            updatable = false;
            System.out.println("Durability kind cannot be changed after the creation of a subscriber."); // TODO Log this
        }

        if(liveliness.kind !=  qos.liveliness.kind) {
            updatable = false;
            System.out.println("Liveliness Kind cannot be changed after the creation of a subscriber."); // TODO Log this
        }

        if(reliability.kind != qos.reliability.kind) {
            updatable = false;
            System.out.println("Reliability Kind cannot be changed after the creation of a subscriber."); // TODO Log this
        }
        if(ownership.kind != qos.ownership.kind) {
            updatable = false;
            System.out.println("Ownership Kind cannot be changed after the creation of a subscriber."); // TODO Log this
        }
        if(destinationOrder.kind != qos.destinationOrder.kind) {
            updatable = false;
            System.out.println("Destination order Kind cannot be changed after the creation of a subscriber."); // TODO Log this
        }
        return updatable;
    }

}
