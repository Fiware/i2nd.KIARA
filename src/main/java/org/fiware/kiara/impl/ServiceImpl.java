package org.fiware.kiara.impl;

import org.fiware.kiara.server.Service;
import org.fiware.kiara.server.Servant;

import java.util.HashMap;
import java.util.Map;

import org.fiware.kiara.dynamic.services.DynamicFunctionHandler;
import org.fiware.kiara.exceptions.IDLParseException;
import org.fiware.kiara.typecode.services.FunctionTypeDescriptor;
import org.fiware.kiara.typecode.services.ServiceTypeDescriptor;

public class ServiceImpl implements Service {

    private final Map<FunctionTypeDescriptor, DynamicFunctionHandler> dynamicHandlers;
    private final IDLInfoDatabase idlInfoDatabase;

    public ServiceImpl() {
        dynamicHandlers = new HashMap<>();
        idlInfoDatabase = new IDLInfoDatabase();
    }

    public IDLInfoDatabase getIDLInfoDatabase() {
        return idlInfoDatabase;
    }

    @Override
    public void register(Object serviceImpl) {
        if (serviceImpl instanceof Servant) {
            // generated by IDL compiler
            idlInfoDatabase.addServant((Servant) serviceImpl);
        } else {
            // dynamic API starts here
            throw new UnsupportedOperationException("Dynamic API not implemented yet");
        }
    }

    @Override
    public void register(String idlOperationName, DynamicFunctionHandler handler) {
        if (idlOperationName == null) {
            throw new NullPointerException("idlOperationName");
        } else if (handler == null) {
            throw new NullPointerException("handler");
        }

        // split idlOperationName into service name and operationName
        int lastDotIndex = idlOperationName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("invalid idlOperationName: operation name missing");
        }
        final String serviceName = idlOperationName.substring(0, lastDotIndex);
        final String operationName = idlOperationName.substring(lastDotIndex + 1);

        if (serviceName.isEmpty()) {
            throw new IllegalArgumentException("invalid idlOperationName: service name is empty");
        }
        if (operationName.isEmpty()) {
            throw new IllegalArgumentException("invalid idlOperationName: operation name is empty");
        }

        FunctionTypeDescriptor type = null;

        for (IDLInfo idlInfo : idlInfoDatabase.getIDLInfos()) {
            for (ServiceTypeDescriptor serviceType : idlInfo.serviceTypes) {
                if (serviceName.equals(serviceType.getScopedName())) {
                    for (FunctionTypeDescriptor functionType : serviceType.getFunctions()) {
                        if (operationName.equals(functionType.getName())) {
                            type = functionType;
                            break;
                        }
                    }
                }
            }
        }

        if (type == null) {
            throw new IllegalArgumentException("no such IDL operation '" + idlOperationName + "' found");
        }

        if (dynamicHandlers.containsKey(type)) {
            throw new IllegalStateException("IDL operation '" + idlOperationName + "' is already registered");
        }
        dynamicHandlers.put(type, handler);
    }

    @Override
    public void loadServiceIDLFromString(String idlContents) throws IDLParseException {
        idlInfoDatabase.loadServiceIDLFromString(idlContents);
    }

    public Map<FunctionTypeDescriptor, DynamicFunctionHandler> getDynamicHandlers() {
        return dynamicHandlers;
    }

}
