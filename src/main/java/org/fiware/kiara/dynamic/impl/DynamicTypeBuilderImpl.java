package org.fiware.kiara.dynamic.impl;

import org.fiware.kiara.dynamic.DynamicTypeBuilder;
import org.fiware.kiara.dynamic.data.DynamicData;
import org.fiware.kiara.dynamic.data.DynamicPrimitive;
import org.fiware.kiara.dynamic.impl.data.DynamicArrayImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicDataImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicListImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicMapImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicPrimitiveImpl;
import org.fiware.kiara.dynamic.impl.data.DynamicSetImpl;
import org.fiware.kiara.dynamic.impl.services.DynamicFunction;
import org.fiware.kiara.exceptions.DynamicTypeException;
import org.fiware.kiara.typecode.data.ArrayTypeDescriptor;
import org.fiware.kiara.typecode.data.DataTypeDescriptor;
import org.fiware.kiara.typecode.data.ListTypeDescriptor;
import org.fiware.kiara.typecode.data.MapTypeDescriptor;
import org.fiware.kiara.typecode.data.PrimitiveTypeDescriptor;
import org.fiware.kiara.typecode.data.SetTypeDescriptor;
import org.fiware.kiara.typecode.impl.data.ArrayTypeDescriptorImpl;
import org.fiware.kiara.typecode.impl.data.DataTypeDescriptorImpl;
import org.fiware.kiara.typecode.impl.data.MapTypeDescriptorImpl;
import org.fiware.kiara.typecode.impl.data.PrimitiveTypeDescriptorImpl;
import org.fiware.kiara.typecode.impl.data.ListTypeDescriptorImpl;
import org.fiware.kiara.typecode.impl.data.SetTypeDescriptorImpl;
import org.fiware.kiara.typecode.impl.services.FunctionTypeDescriptorImpl;
import org.fiware.kiara.typecode.services.FunctionTypeDescriptor;

public class DynamicTypeBuilderImpl implements DynamicTypeBuilder {
    
    private static DynamicTypeBuilderImpl instance = null;
    
    protected DynamicTypeBuilderImpl() { 
        // Makes constructor not accessible.
    }
    
    public static DynamicTypeBuilder getInstance() {
        if (instance == null) {
            instance = new DynamicTypeBuilderImpl();
        }
        
        return instance;
    }
    
    @Override
    public DynamicFunction createFunction(FunctionTypeDescriptor functionDescriptor) {
        return new DynamicFunction((FunctionTypeDescriptorImpl) functionDescriptor);
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
        case UNION_TYPE:
        case STRUCT_TYPE:
        case EXCEPTION_TYPE:
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
        if (arrayDescriptor.getContentType() == null) {
            throw new DynamicTypeException("DynamicTypeBuilder - The content type for this array descriptor has not been defined.");
        }
        ret.setContentType(this.createData(arrayDescriptor.getContentType()));
        for (int i=0; i < arrayDescriptor.getMaxSize(); ++i) {
            ret.addElement(this.createData(arrayDescriptor.getContentType()));
        }
        return ret;
    }
    
    private DynamicListImpl createListType(ListTypeDescriptor listDescriptor) {
        DynamicListImpl ret = new DynamicListImpl(listDescriptor);
        if (listDescriptor.getContentType() == null) {
            throw new DynamicTypeException("DynamicTypeBuilder - The content type for this list descriptor has not been defined.");
        }
        ret.setContentType(this.createData(listDescriptor.getContentType()));
        for (int i=0; i < listDescriptor.getMaxSize(); ++i) {
            ret.setElementAt(this.createData(listDescriptor.getContentType()), i);
        }
        return ret;
    }
    
    private DynamicSetImpl createSetType(SetTypeDescriptor setDescriptor) {
        DynamicSetImpl ret = new DynamicSetImpl(setDescriptor);
        if (setDescriptor.getContentType() == null) {
            throw new DynamicTypeException("DynamicTypeBuilder - The content type for this set descriptor has not been defined.");
        }
        
        ret.setContentType(this.createData(setDescriptor.getContentType()));
        for (int i=0; i < setDescriptor.getMaxSize(); ++i) {
            DynamicDataImpl data = (DynamicDataImpl) this.createData(setDescriptor.getContentType());
            data.registerVisitor(ret);
            ret.setElementAt(data, i);
        }
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
        
        ret.setKeyContentType(this.createData((DataTypeDescriptor) mapDescriptor.getKeyTypeDescriptor()));
        ret.setValueContentType(this.createData((DataTypeDescriptor) mapDescriptor.getValueTypeDescriptor()));
        
        for (int i=0; i < mapDescriptor.getMaxSize(); ++i) {
            DynamicDataImpl key = (DynamicDataImpl) this.createData((DataTypeDescriptor) mapDescriptor.getKeyTypeDescriptor());
            key.registerVisitor(ret);
            DynamicDataImpl value = (DynamicDataImpl) this.createData((DataTypeDescriptor) mapDescriptor.getValueTypeDescriptor());
            value.registerVisitor(ret);
            ret.setElementAt(key, value, i);
        }
        
        return ret;
    }

}
