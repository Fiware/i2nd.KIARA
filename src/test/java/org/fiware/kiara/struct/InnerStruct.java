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
package org.fiware.kiara.struct;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import org.fiware.kiara.serialization.impl.BinaryInputStream;
import org.fiware.kiara.serialization.impl.BinaryOutputStream;

import org.fiware.kiara.serialization.impl.Serializable;
import org.fiware.kiara.serialization.impl.SerializerImpl;
import org.fiware.kiara.serialization.impl.CDRSerializer;

/**
 * Class definition for the user defined type InnerStruct.
 *
 * @author Kiaragen tool.
 *
 */
public class InnerStruct implements Serializable {

    /*
     *	Attributes
     */
    private int innerLongAtt;
    private java.lang.String innerStringAtt;

    /*
     *	Default constructor
     */
    public InnerStruct() {
        this.innerLongAtt = 0;
        this.innerStringAtt = "";
    }

    /*
     * This method serializes a InnerStruct.
     *
	 * @see org.fiware.kiara.serialization.impl.Serializable#serialize(org.fiware.kiara.serialization.impl.SerializerImpl, org.fiware.kiara.serialization.impl.BinaryOutputStream, java.lang.String)
     */
    @Override
    public void serialize(SerializerImpl impl, BinaryOutputStream message, String name) throws IOException {
        impl.serializeI32(message, name, this.innerLongAtt);
        impl.serializeString(message, name, this.innerStringAtt);
    }

    /*
     * This method deserializes a InnerStruct.
     *
	 * @see org.fiware.kiara.serialization.impl.Serializable#deserialize(org.fiware.kiara.serialization.impl.SerializerImpl, org.fiware.kiara.serialization.impl.BinaryInputStream, java.lang.String)
     */
    @Override
    public void deserialize(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
        this.innerLongAtt = impl.deserializeI32(message, name);
        this.innerStringAtt = impl.deserializeString(message, name);
    }

    /*
     * @param other An object instance of Object
     */
    @Override
    public boolean equals(Object other) {
        boolean comparison = true;

        if (other instanceof InnerStruct) {

            comparison = comparison && (this.innerLongAtt == ((InnerStruct) other).innerLongAtt);

            comparison = comparison && (this.innerStringAtt.compareTo(((InnerStruct) other).innerStringAtt) == 0);

        }

        return comparison;
    }

    /*
     *This method calculates the maximum size in CDR for this class.
     * 
     * @param current_alignment Integer containing the current position in the buffer.
     */
	public static int getMaxCdrSerializedSize(int current_alignment)
	{
        int current_align = current_alignment;

        current_align += 4 + CDRSerializer.alignment(current_align, 4);

        current_align += 4 + CDRSerializer.alignment(current_align, 4) + 255 + 1;

        return current_align;
    }

    /*
     * Method to get the attribute innerLongAtt.
     */
    public int getInnerLongAtt() {
        return this.innerLongAtt;
    }

    /*
     * Method to set the attribute innerLongAtt.
     */
    public void setInnerLongAtt(int innerLongAtt) {
        this.innerLongAtt = innerLongAtt;
    }

    /*
     * Method to get the attribute innerStringAtt.
     */
    public java.lang.String getInnerStringAtt() {
        return this.innerStringAtt;
    }

    /*
     * Method to set the attribute innerStringAtt.
     */
    public void setInnerStringAtt(java.lang.String innerStringAtt) {
        this.innerStringAtt = innerStringAtt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.innerLongAtt, this.innerStringAtt);
    }
}
