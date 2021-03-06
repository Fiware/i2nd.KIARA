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
package org.fiware.kiara.dynamic.impl.data;

import org.fiware.kiara.dynamic.data.DynamicData;
import org.fiware.kiara.dynamic.data.DynamicMember;

/**
*
* @author Rafael Lara {@literal <rafaellara@eprosima.com>}
*
*/
public class DynamicMemberImpl implements DynamicMember { 
    
    private String m_name; 
    private DynamicData m_dynamicData;
    
    public DynamicMemberImpl(DynamicData dynamicData, String name) {
        this.m_name = name;
        this.m_dynamicData = dynamicData;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public DynamicData getDynamicData() {
        return m_dynamicData;
    }
    
    @Override
    public boolean equals(Object anotherObject) {
        if (anotherObject instanceof DynamicMemberImpl) {
            if (((DynamicMemberImpl) anotherObject).m_name.equals(this.m_name)) {
                if (((DynamicMemberImpl) anotherObject).m_dynamicData.equals(this.m_dynamicData)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setDynamicData(DynamicData data) {
        this.m_dynamicData = data;
    }

}
