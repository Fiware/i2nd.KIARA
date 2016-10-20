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
 * @file CalculatorServantExample.java
 * This file contains the servant implementation.
 *
 * This file was generated by using the tool Kiaragen.
 *
 */
package org.fiware.kiara.calculator;

/**
 * Class where the actual implementation of the procedures is located.
 *
 * @author Kiaragen tool.
 *
 */
class CalculatorServantExample extends CalculatorServant {

    /*!
     * @brief This method implements the behavious of the add remote procedure.
     *        It has to be implemented by the user.
     */
    public int add(/*in*/int n1, /*in*/ int n2) {
        return n1 + n2;
    }

    /*!
     * @brief This method implements the behavious of the subtract remote procedure.
     *        It has to be implemented by the user.
     */
    public int subtract(/*in*/int n1, /*in*/ int n2) {
        return n1 - n2;
    }

}