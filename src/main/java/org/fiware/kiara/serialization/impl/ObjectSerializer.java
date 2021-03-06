/* KIARA - Middleware for efficient and QoS/Security-aware invocation of services and exchange of messages
 *
 * Copyright (C) 2014 German Research Center for Artificial Intelligence (DFKI)
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
package org.fiware.kiara.serialization.impl;

import java.io.IOException;

/**
 *
 * @author Dmitri Rubinstein {@literal <dmitri.rubinstein@dfki.de>}
 * @param <T>
 */
public class ObjectSerializer<T extends Serializable> implements Serializer<T> {

    private final Class<T> objectClass;

    public ObjectSerializer(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    @Override
    public void write(SerializerImpl impl, BinaryOutputStream message, String name, T object) throws IOException {
        impl.serialize(message, name, object);
    }

    @Override
    public T read(SerializerImpl impl, BinaryInputStream message, String name) throws IOException {
        try {
            return impl.deserialize(message, name, objectClass);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IOException(ex);
        }
    }

}
