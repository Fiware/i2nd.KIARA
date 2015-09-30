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
 * @file BooleanSwitchUnion.java
 * This file contains the class representing a user defined union.
 *
 * This file was generated by using the tool Kiaragen.
 *
 */
 
 
package org.fiware.kiara.serialization.types;

import java.io.IOException;
import org.fiware.kiara.serialization.impl.Serializable;
import org.fiware.kiara.serialization.impl.SerializerImpl;
import org.fiware.kiara.serialization.impl.CDRSerializer;
import org.fiware.kiara.transport.impl.TransportMessage;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
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
public class BooleanSwitchUnion implements Serializable {

	private boolean m_d;

	private int intVal;
	private java.lang.String stringVal;
	
	public BooleanSwitchUnion() {
		this.intVal = 0;
		this.stringVal = "";
	}
	
	public void _d(boolean discriminator) {
		this.m_d = discriminator;
	}
	

	/*
	 * @param other An object instance of Object
	 */
	 @Override
	public boolean equals(Object other) {
		boolean comparison = true;
		
		if (other instanceof BooleanSwitchUnion) {
		
			Boolean bool_disc = this.m_d;
			switch(bool_disc.toString()) {
		
				case "true":
					comparison = comparison && (this.intVal == ((BooleanSwitchUnion) other).intVal);
					break;

				case "false":
					comparison = comparison && (this.stringVal.compareTo(((BooleanSwitchUnion) other).stringVal) == 0);
					break;

			}
		}
		
		return comparison;
	}
	
	/*
	 * This method serializes a BooleanSwitchUnion.
	 *
	 * @see org.fiware.kiara.serialization.impl.Serializable#serialize(org.fiware.kiara.serialization.impl.SerializerImpl, org.fiware.kiara.serialization.impl.BinaryOutputStream, java.lang.String)
	 */
	@Override
	public void serialize(SerializerImpl impl, BinaryOutputStream message, String name) throws IOException {
		impl.serializeBoolean(message, name, this.m_d);
		Boolean bool_disc = this.m_d;
		switch(bool_disc.toString()) {
			case "true":
				impl.serializeI32(message, name, this.intVal);
				break;
			case "false":
				impl.serializeString(message, name, this.stringVal);
				break;
		}
	}

	/*
	 * This method deserializes a BooleanSwitchUnion.
	 *
	 * @see org.fiware.kiara.serialization.impl.Serializable#deserialize(org.fiware.kiara.serialization.impl.SerializerImpl, org.fiware.kiara.serialization.impl.BinaryInputStream, java.lang.String)
	 */
	@Override
	public void deserialize(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
		this.m_d = impl.deserializeBoolean(message, name);
		Boolean bool_disc = this.m_d;
		switch(bool_disc.toString()) {
			case "true":
				this.intVal = impl.deserializeI32(message, name);
				break;
			case "false":
				this.stringVal = impl.deserializeString(message, name);
				break;
		}
	}

	
	/*
	 * Method to get the attribute intVal.
	 */
	public int getIntVal() {
		boolean canDoIt = false;
		Boolean bool_disc = this.m_d;
		switch(bool_disc.toString()) {
			case "true":
				canDoIt=true;
				break;
			default:
				break;
		}
		if (!canDoIt) {
			throw new UnsupportedOperationException("Invalid union value");
		}
		return this.intVal;
	}

	/*
	 * Method to set the attribute intVal.
	 */
	public void setIntVal(int intVal) {
		boolean canDoIt = false;
		Boolean bool_disc = this.m_d;
		switch(bool_disc.toString()) {
			case "true":
				canDoIt=true;
				break;
			default:
				break;
		}
		if (!canDoIt) {
			throw new UnsupportedOperationException("Invalid union value");
		}
		this.intVal = intVal;
	}

	/*
	 * Method to get the attribute stringVal.
	 */
	public java.lang.String getStringVal() {
		boolean canDoIt = false;
		Boolean bool_disc = this.m_d;
		switch(bool_disc.toString()) {
			case "false":
				canDoIt=true;
				break;
			default:
				break;
		}
		if (!canDoIt) {
			throw new UnsupportedOperationException("Invalid union value");
		}
		return this.stringVal;
	}

	/*
	 * Method to set the attribute stringVal.
	 */
	public void setStringVal(java.lang.String stringVal) {
		boolean canDoIt = false;
		Boolean bool_disc = this.m_d;
		switch(bool_disc.toString()) {
			case "false":
				canDoIt=true;
				break;
			default:
				break;
		}
		if (!canDoIt) {
			throw new UnsupportedOperationException("Invalid union value");
		}
		this.stringVal = stringVal;
	}
	
	/*
	 *This method calculates the maximum size in CDR for this class.
	 * 
	 * @param current_alignment Integer containing the current position in the buffer.
	 */
	public static int getMaxCdrSerializedSize(int current_alignment)
	{
	    int current_align = current_alignment;
	    int sum = 0;
	    int current_sum = 0;
	    
	    current_align += 1 + CDRSerializer.alignment(current_align, 1);
	            

	    current_sum += 4 + CDRSerializer.alignment(current_sum, 4);
	    if (current_sum > sum) {
	    	sum = current_sum;
	    }
	    current_sum = 0;

	        
	    current_sum += 4 + CDRSerializer.alignment(current_sum, 4) + 255 + 1;
	    if (current_sum > sum) {
	    	sum = current_sum;
	    }
	    current_sum = 0;

	        
	    
	    return sum + (current_align - current_alignment);
	}
	
	@Override
	public int hashCode() {
		Boolean bool_disc = this.m_d;
		switch(bool_disc.toString()) {
		case "true":
				return Objects.hash(this.m_d, this.intVal);
			
		case "false":
				return Objects.hash(this.m_d, this.stringVal);
			
		default:
			return -1;
		}
	}
	
}
 