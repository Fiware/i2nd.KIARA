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
 * @file IntSwitchUnion.java
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
import org.fiware.kiara.serialization.impl.BinaryInputStream;
import org.fiware.kiara.serialization.impl.BinaryOutputStream;

public class IntSwitchUnion implements Serializable {
	
	private int m_d;
	
	private int intVal;
	private java.lang.String stringVal;
	private float floatVal;
	
	public IntSwitchUnion() {
		this.intVal = 0;
		this.stringVal = "";
		this.floatVal = (float) 0.0;
	}
	
	public void _d(int discriminator) {
		this.m_d = discriminator;
	}
	

	/*
	 * @param other An object instance of Object
	 */
	 @Override
	public boolean equals(Object other) {
		boolean comparison = true;
		
		if (other instanceof IntSwitchUnion) {
		
			switch(this.m_d) {
		
				case 0:
				case 1:
					comparison = comparison && (this.intVal == ((IntSwitchUnion) other).intVal);
					break;

				case 2:
					comparison = comparison && (this.stringVal.compareTo(((IntSwitchUnion) other).stringVal) == 0);
					break;

				default:
					comparison = comparison && (this.floatVal == ((IntSwitchUnion) other).floatVal);
					break;

			}
		}
		
		return comparison;
	}
	
	/*
	 * This method serializes a IntSwitchUnion.
	 *
	 * @see org.fiware.kiara.serialization.impl.Serializable#serialize(org.fiware.kiara.serialization.impl.SerializerImpl, org.fiware.kiara.serialization.impl.BinaryOutputStream, java.lang.String)
	 */
        @Override
	public void serialize(SerializerImpl impl, BinaryOutputStream message, String name) throws IOException {
	        impl.serializeI32(message, name, this.m_d);
	        switch(this.m_d) {
			case 0:
			case 1:
				impl.serializeI32(message, name, this.intVal);
				break;
			case 2:
				impl.serializeString(message, name, this.stringVal);
				break;
			default:
				impl.serializeFloat32(message, name, this.floatVal);
				break;
		}
	}

	/*
	 * This method deserializes a IntSwitchUnion.
	 *
	 * @see org.fiware.kiara.serialization.impl.Serializable#deserialize(org.fiware.kiara.serialization.impl.SerializerImpl, org.fiware.kiara.serialization.impl.BinaryInputStream, java.lang.String)
	 */
        @Override
        public void deserialize(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
                this.m_d = impl.deserializeI32(message, name);
	        switch(this.m_d) {
			case 0:
			case 1:
			    this.intVal = impl.deserializeI32(message, name);
			    break;
			case 2:
			    this.stringVal = impl.deserializeString(message, name);
			    break;
			default:
    			    this.floatVal = impl.deserializeFloat32(message, name);
    			    break;
		}
	}

	
	/*
	 * Method to get the attribute intVal.
	 */
	public int getIntVal() {
		boolean canDoIt = false;
		switch(this.m_d) {
			case 0:
			case 1:
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
		switch(this.m_d) {
			case 0:
			case 1:
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
		switch(this.m_d) {
			case 2:
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
		switch(this.m_d) {
			case 2:
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
	 * Method to get the attribute floatVal.
	 */
	public float getFloatVal() {
		boolean canDoIt = false;
		switch(this.m_d) {
			case 0:
			case 1:
				break;
			case 2:
				break;
			default:
				canDoIt=true;
				break;
		}
		if (!canDoIt) {
			throw new UnsupportedOperationException("Invalid union value");
		}
		return this.floatVal;
	}

	/*
	 * Method to set the attribute floatVal.
	 */
	public void setFloatVal(float floatVal) {
		boolean canDoIt = false;
		switch(this.m_d) {
			case 0:
			case 1:
				break;
			case 2:
				break;
			default:
				canDoIt=true;
				break;
		}
		if (!canDoIt) {
			throw new UnsupportedOperationException("Invalid union value");
		}
		this.floatVal = floatVal;
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
	    
	    current_align += 4 + CDRSerializer.alignment(current_align, 4);
	            

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

	        
	    current_sum += 4 + CDRSerializer.alignment(current_sum, 4);
	    if (current_sum > sum) {
	    	sum = current_sum;
	    }
	    current_sum = 0;

	        
	    
	    return sum + (current_align - current_alignment);
	}
	
	@Override
        public int hashCode() {
            return this.m_d + this.stringVal.hashCode() + (int) this.floatVal;
        }
	
}
 