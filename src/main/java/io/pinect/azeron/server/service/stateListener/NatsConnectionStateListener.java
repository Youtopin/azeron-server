package io.pinect.azeron.server.service.stateListener;

import nats.client.ConnectionStateListener;

public interface NatsConnectionStateListener extends ConnectionStateListener {
    State getCurrentState();
}
