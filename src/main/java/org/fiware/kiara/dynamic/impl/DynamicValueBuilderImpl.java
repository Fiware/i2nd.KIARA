/* KIARA - Middleware for efficient and QoS/Security-aware invocation of services and exchange of messages
 *
 * Copyright (C) 2014 Proyectos y Sistemas de Mantenimiento S.L. (eProsima)
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
package org.fiware.kiara.dynamic.impl;

import org.fiware.kiara.dynamic.DynamicValueBuilder;
import org.fiware.kiara.dynamic.data.DynamicData;
import org.fiware.kiara.dynamic.data.DynamicEnum;
import org.fiware.kiara.dynamic.data.DynamicException;
import org.fiware.kiara.dynamic.data.DynamicPrimitive;
import org.fiware.kiara.dynamic.data.DynamicStruct;
import org.fiware.kiara.dynamic.data.DynamicUnion;
import org.fiware.kiara.dynamic.impl.data.DynamicArrayImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicEnumImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicExceptionImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicListImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicMapImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicPrimitiveImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicSetImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicStructImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicUnionImpl;
import org.fiware.kiara.dynamic.impl.services.DynamicFunctionRequestImpl;
import org.fiware.kiara.dynamic.impl.services.DynamicFunctionResponseImpl;
import org.fiware.kiara.dynamic.impl.services.DynamicProxyImpl;
import org.fiware.kiara.dynamic.services.DynamicFunctionRequest;
import org.fiware.kiara.dynamic.services.DynamicFunctionResponse;
import org.fiware.kiara.dynamic.services.DynamicProxy;
import org.fiware.kiara.exceptions.DynamicTypeException;
import org.fiware.kiara.serialization.Serializer;
import org.fiware.kiara.serialization.impl.SerializerImpl;
import org.fiware.kiara.transport.Transport;
import org.fiware.kiara.typecode.data.ArrayTypeDescriptor;
import org.fiware.kiara.typecode.data.DataTypeDescriptor;
import org.fiware.kiara.typecode.data.EnumTypeDescriptor;
import org.fiware.kiara.typecode.data.ExceptionTypeDescriptor;
import org.fiware.kiara.typecode.data.ListTypeDescriptor;
import org.fiware.kiara.typecode.data.MapTypeDescriptor;
import org.fiware.kiara.typecode.data.Member;
import org.fiware.kiara.typecode.data.PrimitiveTypeDescriptor;
import org.fiware.kiara.typecode.data.SetTypeDescriptor;
import org.fiware.kiara.typecode.data.StructTypeDescriptor;
import org.fiware.kiara.typecode.data.UnionTypeDescriptor;
import org.fiware.kiara.typecode.services.FunctionTypeDescriptor;
import org.fiware.kiara.typecode.impl.data.UnionMemberImpl;
import org.fiware.kiara.typecode.impl.data.UnionTypeDescriptorImpl;
import org.fiware.kiara.typecode.impl.services.FunctionTypeDescriptorImpl;
import org.fiware.kiara.typecode.services.ServiceTypeDescriptor;

/**
*
* @author Rafael Lara {@literal <rafaellara@eprosima.com>}
*
*/
public class DynamicValueBuilderImpl implements DynamicValueBuilder {
    
    private DynamicValueBuilderImpl() {}
    
    private static class LazyDynamicBuilderHolder {
        private static DynamicValueBuilderImpl instance = new DynamicValueBuilderImpl();
    }
    
    public static DynamicValueBuilder getInstance() {
        return LazyDynamicBuilderHolder.instance;
    }
    
    public DynamicProxy createService(ServiceTypeDescriptor serviceDescriptor, Serializer serializer, Transport transport) {
        DynamicProxyImpl ret = new DynamicProxyImpl(serviceDescriptor, serializer, transport);
        
        /*for (FunctionTypeDescriptor fd : serviceDescriptor.getFunctions()) {
            DynamicFunctionRequest funcRequest = this.createFunctionRequest(fd);
            DynamicFunctionResponse funcResponse = this.createFunctionResponse(fd);
            
        }*/
        
        return ret;
    }
    
    @Override
    public DynamicFunctionRequest createFunctionRequest(FunctionTypeDescriptor functionDescriptor, Serializer serializer, Transport transport) {
        DynamicFunctionRequestImpl ret = new DynamicFunctionRequestImpl((FunctionTypeDescriptorImpl) functionDescriptor, serializer, transport);
        
        for (Member param : ((FunctionTypeDescriptorImpl) functionDescriptor).getParameters()) {
            DynamicData data = this.createData(param.getTypeDescriptor());
            ret.addParameter(data, param.getName());
        }
        
        return ret;
    }
    
    @Override
    public DynamicFunctionRequest createFunctionRequest(FunctionTypeDescriptor functionDescriptor) {
        DynamicFunctionRequestImpl ret = new DynamicFunctionRequestImpl((FunctionTypeDescriptorImpl) functionDescriptor);
        
        for (Member param : ((FunctionTypeDescriptorImpl) functionDescriptor).getParameters()) {
            DynamicData data = this.createData(param.getTypeDescriptor());
            ret.addParameter(data, param.getName());
        }
        
        return ret;
    }
    
    @Override
    public DynamicFunctionResponse createFunctionResponse(FunctionTypeDescriptor functionDescriptor, Serializer serializer, Transport transport) {
        DynamicFunctionResponseImpl ret = new DynamicFunctionResponseImpl((FunctionTypeDescriptorImpl) functionDescriptor, serializer, transport);
        return ret;
    }
    
    @Override
    public DynamicFunctionResponse createFunctionResponse(FunctionTypeDescriptor functionDescriptor) {
        DynamicFunctionResponseImpl ret = new DynamicFunctionResponseImpl((FunctionTypeDescriptorImpl) functionDescriptor);
        return ret;
    }

    @Override
    public DynamicData createData(DataTypeDescriptor dataDescriptor) {
        switch (dataDescriptor.getKind()) {
        case BOOLEAN_TYPE:
        case BYTE_TYPE:
        case INT_16_TYPE:
        case UINT_16_TYPE:
        case INT_32_TYPE:
        case UINT_32_TYPE:
        case INT_64_TYPE:
        case UINT_64_TYPE:
        case FLOAT_32_TYPE:
        case FLOAT_64_TYPE:
        case CHAR_8_TYPE:
        case STRING_TYPE:
            return this.createPrimitiveType((PrimitiveTypeDescriptor) dataDescriptor);
        case ARRAY_TYPE:
            return this.createArrayType((ArrayTypeDescriptor) dataDescriptor);
        case LIST_TYPE:
            return this.createListType((ListTypeDescriptor) dataDescriptor);
        case MAP_TYPE:
            return this.createMapType((MapTypeDescriptor) dataDescriptor);
        case SET_TYPE:
            return this.createSetType((SetTypeDescriptor) dataDescriptor);
        case ENUM_TYPE:
            return this.createEnumType((EnumTypeDescriptor) dataDescriptor);
        case UNION_TYPE:
            return this.createUnionType((UnionTypeDescriptor) dataDescriptor);
        case STRUCT_TYPE:
            return this.createStructType((StructTypeDescriptor) dataDescriptor);
        case EXCEPTION_TYPE:
            return this.createExceptionType((ExceptionTypeDescriptor) dataDescriptor);
        default:
            break;
        }
        
        return null;
    }
    
    private DynamicPrimitive createPrimitiveType(PrimitiveTypeDescriptor dataDescriptor) {
        return new DynamicPrimitiveImpl(dataDescriptor);
    }
    
    private DynamicArrayImpl createArrayType(ArrayTypeDescriptor arrayDescriptor) {
        DynamicArrayImpl ret = new DynamicArrayImpl(arrayDescriptor);
        if (arrayDescriptor.getElementType() == null) {
            throw new DynamicTypeException("DynamicTypeBuilder - The content type for this array descriptor has not been defined.");
        }
        ret.setElementType(arrayDescriptor.getElementType());
        for (int i=0; i < arrayDescriptor.getMaxSize(); ++i) {
            ret.addElement(this.createData(arrayDescriptor.getElementType()));
        }
        return ret;
    }
    
    private DynamicListImpl createListType(ListTypeDescriptor listDescriptor) {
        DynamicListImpl ret = new DynamicListImpl(listDescriptor);
        if (listDescriptor.getElementType() == null) {
            throw new DynamicTypeException("DynamicTypeBuilder - The content type for this list descriptor has not been defined.");
        }
        ret.setElementType(listDescriptor.getElementType());
        return ret;
    }
    
    private DynamicSetImpl createSetType(SetTypeDescriptor setDescriptor) {
        DynamicSetImpl ret = new DynamicSetImpl(setDescriptor);
        if (setDescriptor.getElementType() == null) {
            throw new DynamicTypeException("DynamicTypeBuilder - The content type for this set descriptor has not been defined.");
        }
        
        ret.setElementType(setDescriptor.getElementType());
        return ret;
    }
    
    private DynamicMapImpl createMapType(MapTypeDescriptor mapDescriptor) {
        if (mapDescriptor.getKeyTypeDescriptor() == null) {
            throw new DynamicTypeException("DynamicTypeBuilder - The content key type for this map descriptor has not been defined.");
        }
        
        if (mapDescriptor.getValueTypeDescriptor() == null) {
            throw new DynamicTypeException("DynamicTypeBuilder - The content value type for this map descriptor has not been defined.");
        }
        
        DynamicMapImpl ret = new DynamicMapImpl(mapDescriptor);
        
        ret.setKeyContentType((DataTypeDescriptor) mapDescriptor.getKeyTypeDescriptor());
        ret.setValueContentType((DataTypeDescriptor) mapDescriptor.getValueTypeDescriptor());
        return ret;
    }
    
    private DynamicStruct createStructType(StructTypeDescriptor dataDescriptor) {
        DynamicStructImpl ret = new DynamicStructImpl(dataDescriptor);
        for (Member member : dataDescriptor.getMembers()) {
            DynamicData dynData = this.createData(member.getTypeDescriptor());
            ret.addMember(dynData, member.getName());
        }
        return ret;
    }
    
    private DynamicEnum createEnumType(EnumTypeDescriptor dataDescriptor) {
        DynamicEnumImpl ret = new DynamicEnumImpl(dataDescriptor);
        for (Member member : dataDescriptor.getMembers()) {
            ret.addMember(null, member.getName());
        }
        return ret;
    }
    
    private DynamicUnion createUnionType(UnionTypeDescriptor dataDescriptor) {
        if (dataDescriptor.getMembers().size() == 0) {
            throw new DynamicTypeException("DynamicTypeBuilder - No members have been assigned to this enumeration.");
        }
        DynamicUnionImpl ret = new DynamicUnionImpl(dataDescriptor);
        ret.setDiscriminator(this.createData(((UnionTypeDescriptorImpl) dataDescriptor).getDiscriminator()));
        for (Member member : dataDescriptor.getMembers()) {
            UnionMemberImpl<?> unionMember = (UnionMemberImpl<?>) member;
            DynamicData dynData = this.createData(unionMember.getTypeDescriptor());
            ret.addMember(dynData, unionMember.getName(), unionMember.getLabels(), unionMember.isDefault());
        }
        ret.setDefaultDiscriminatorValue();
        return ret;
    }
    
    private DynamicException createExceptionType(ExceptionTypeDescriptor dataDescriptor) {
        DynamicExceptionImpl ret = new DynamicExceptionImpl(dataDescriptor);
        for (Member member : dataDescriptor.getMembers()) {
            DynamicData dynData = this.createData(member.getTypeDescriptor());
            ret.addMember(dynData, member.getName());
        }
        return ret;
    }

}
