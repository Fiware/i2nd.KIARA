package org.fiware.kiara.typecode;

import org.fiware.kiara.typecode.data.ArrayTypeDescriptor;
import org.fiware.kiara.typecode.data.DataTypeDescriptor;
import org.fiware.kiara.typecode.data.EnumTypeDescriptor;
import org.fiware.kiara.typecode.data.ExceptionTypeDescriptor;
import org.fiware.kiara.typecode.data.ListTypeDescriptor;
import org.fiware.kiara.typecode.data.MapTypeDescriptor;
import org.fiware.kiara.typecode.data.PrimitiveTypeDescriptor;
import org.fiware.kiara.typecode.data.SetTypeDescriptor;
import org.fiware.kiara.typecode.data.StructTypeDescriptor;
import org.fiware.kiara.typecode.data.UnionTypeDescriptor;
import org.fiware.kiara.typecode.impl.FunctionTypeDescriptor;
import org.fiware.kiara.typecode.services.ServiceTypeDescriptor;

/**
*
* @author Rafael Lara {@literal <rafaellara@eprosima.com>}
*
*/
public interface TypeDescriptorBuilder {

    public DataTypeDescriptor createVoidType();

    public PrimitiveTypeDescriptor createPrimitiveType(TypeKind kind);

    public ArrayTypeDescriptor createArrayType(DataTypeDescriptor contentDescriptor, int... dimensions);

    public ListTypeDescriptor createListType(DataTypeDescriptor contentDescriptor, int maxSize);
    
    public SetTypeDescriptor createSetType(DataTypeDescriptor contentDescriptor, int maxSize);
    
    public MapTypeDescriptor createMapType(DataTypeDescriptor keyDescriptor, DataTypeDescriptor valueDescriptor, int maxSize);
    
    public StructTypeDescriptor createStructType(String name);
    
    public EnumTypeDescriptor createEnumType(String name, String... values);
    
    public UnionTypeDescriptor createUnionType(String name, DataTypeDescriptor discriminatorDescriptor);
    
    
    
    public ExceptionTypeDescriptor createExceptionType(String name);

    public FunctionTypeDescriptor createFunctionType(String name);

    public ServiceTypeDescriptor createServiceType(String name, String scopedName);

}