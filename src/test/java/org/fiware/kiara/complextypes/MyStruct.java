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
 *
 *
 * @file .java
 * This file contains the class representing a user defined structure.
 *
 * This file was generated by using the tool Kiaragen.
 *
 */
package org.fiware.kiara.complextypes;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import org.fiware.kiara.serialization.impl.BasicSerializers;
import org.fiware.kiara.serialization.impl.BinaryInputStream;
import org.fiware.kiara.serialization.impl.BinaryOutputStream;

import org.fiware.kiara.serialization.impl.Serializable;
import org.fiware.kiara.serialization.impl.SerializerImpl;
import org.fiware.kiara.serialization.impl.CDRSerializer;
import org.fiware.kiara.serialization.impl.ListAsArraySerializer;
import org.fiware.kiara.serialization.impl.ListAsSequenceSerializer;
import org.fiware.kiara.serialization.impl.Serializer;
import org.fiware.kiara.serialization.impl.MapAsMapSerializer;
import org.fiware.kiara.serialization.impl.SetAsSetSerializer;
import org.fiware.kiara.serialization.impl.ObjectSerializer;
import org.fiware.kiara.serialization.impl.EnumSerializer;

/**
 * Class definition for the user defined type MyStruct.
 *
 * @author Kiaragen tool.
 *
 */
public class MyStruct implements Serializable {
	/*
	 *	Attributes
	 */
	private int myInt;
	private java.lang.String myString;
	private List<List<Integer>> arrayInt;
	private List<java.lang.String> arrayString;
	private List<Integer> sequenceInt;

        /*
         *      Attribute Serializers
         */


        private static final org.fiware.kiara.serialization.impl.Serializer<List<List<Integer>>> s_arrayInt =
            new ListAsArraySerializer<>(10, new ListAsArraySerializer<>(5, new BasicSerializers.I32Serializer()));
        private static final org.fiware.kiara.serialization.impl.Serializer<List<java.lang.String>> s_arrayString =
            new ListAsArraySerializer<>(10, new BasicSerializers.StringSerializer());
        private static final org.fiware.kiara.serialization.impl.Serializer<List<Integer>> s_sequenceInt =
            new ListAsSequenceSerializer<>(new BasicSerializers.I32Serializer());

        /*
	 *	Default constructor
	 */

	public MyStruct() {
		this.myInt = 0;
		this.myString = "";
		this.arrayInt = new ArrayList<List<Integer>>();
		this.arrayString = new ArrayList<java.lang.String>();
		this.sequenceInt = new ArrayList<Integer>();
	} 
	
	
	/*
	 * This method serializes a MyStruct.
	 *
	 * @see org.fiware.kiara.serialization.impl.Serializable#serialize(org.fiware.kiara.serialization.impl.SerializerImpl, org.fiware.kiara.serialization.impl.BinaryOutputStream, java.lang.String)
	 */
    @Override
    public void serialize(SerializerImpl impl, BinaryOutputStream message, String name) throws IOException {
		impl.serializeI32(message, name, this.myInt);
		impl.serializeString(message, name, this.myString);
		s_arrayInt.write(impl, message, name, this.arrayInt);
		s_arrayString.write(impl, message, name, this.arrayString);
		s_sequenceInt.write(impl, message, name, this.sequenceInt);
	}

	/*
	 * This method deserializes a MyStruct.
	 *
	 * @see org.fiware.kiara.serialization.impl.Serializable#deserialize(org.fiware.kiara.serialization.impl.SerializerImpl, org.fiware.kiara.serialization.impl.BinaryInputStream, java.lang.String)
	 */
    @Override
    public void deserialize(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
		this.myInt = impl.deserializeI32(message, name);
		this.myString = impl.deserializeString(message, name);
                this.arrayInt = s_arrayInt.read(impl, message, name);
		this.arrayString = s_arrayString.read(impl, message, name);
		this.sequenceInt = s_sequenceInt.read(impl, message, name);
	}



	/*
	 * @param other An object instance of Object
	 */
	 @Override
	public boolean equals(Object other) {
		boolean comparison = true;
		
		if (other instanceof MyStruct) {
		
			comparison = comparison && (this.myInt == ((MyStruct) other).myInt);

			comparison = comparison && (this.myString.compareTo(((MyStruct) other).myString) == 0);

			comparison = comparison && Objects.deepEquals(this.arrayInt, ((MyStruct) other).arrayInt);

			comparison = comparison && Objects.deepEquals(this.arrayString, ((MyStruct) other).arrayString);

			comparison = comparison && this.sequenceInt.equals(((MyStruct) other).sequenceInt);

		}
		
		return comparison;
	}
	
	/*
	 * Method to get the attribute myInt.
	 */
	public int getMyInt() {
		return this.myInt;
	}

    /*
     * Method to set the attribute myInt.
     */
    public void setMyInt(int myInt) {
        this.myInt = myInt;
    }

    /*
     * Method to get the attribute myString.
     */
    public java.lang.String getMyString() {
        return this.myString;
    }

    /*
     * Method to set the attribute myString.
     */
    public void setMyString(java.lang.String myString) {
        this.myString = myString;
    }

    /*
     * Method to get the attribute arrayInt.
     */
    public List<List<Integer>> getArrayInt() {
        return this.arrayInt;
    }

    /*
	 * Method to set the attribute arrayInt.
     */
    public void setArrayInt(List<List<Integer>> arrayInt) {
        this.arrayInt = arrayInt;
    }

    /*
     * Method to get the attribute arrayString.
     */
    public List<java.lang.String> getArrayString() {
        return this.arrayString;
    }

    /*
	 * Method to set the attribute arrayString.
     */
    public void setArrayString(List<java.lang.String> arrayString) {
        this.arrayString = arrayString;
    }

    /*
     * Method to get the attribute sequenceInt.
     */
    public List<Integer> getSequenceInt() {
        return this.sequenceInt;
    }

    /*
     * Method to set the attribute sequenceInt.
     */
    public void setSequenceInt(List<Integer> sequenceInt) {
        this.sequenceInt = sequenceInt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.myInt, this.myString, this.arrayInt, this.arrayString, this.sequenceInt);
    }
}
