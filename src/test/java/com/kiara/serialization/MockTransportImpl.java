package com.kiara.serialization;

import java.io.IOException;
import java.net.SocketAddress;

import com.google.common.util.concurrent.ListenableFuture;
import com.kiara.transport.impl.TransportImpl;
import com.kiara.transport.impl.TransportMessage;
import com.kiara.transport.impl.TransportMessageListener;

public class MockTransportImpl implements TransportImpl {

    public MockTransportImpl() {

    }

    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    public SocketAddress getLocalAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    public SocketAddress getRemoteAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    public TransportMessage createTransportMessage(TransportMessage message) {
        // TODO Auto-generated method stub
        return null;
    }

    public ListenableFuture<Void> send(TransportMessage message) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addMessageListener(TransportMessageListener listener) {
        // TODO Auto-generated method stub

    }

    public boolean removeMessageListener(TransportMessageListener listener) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

}
