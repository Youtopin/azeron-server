package io.pinect.azeron.server.service.initializer;

import nats.client.Nats;

public interface MessagingInitializerService {
    void init(Nats nats);
}
