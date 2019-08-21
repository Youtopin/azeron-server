# Azeron-Server
Azeron Server - The scalable and reliable messaging library, Wraps nats.io and uses Java Spring framework

---

Azeron is simple event messaging library that wraps [nats.io](https://nats.io/) java connector and handles event messaging for you. Currently azeron is only available as Spring module.

The advantage of using Azeron client-server instead of nats is that in handles message recovery for you. Nats is a `FIRE AND FORGET` system. But Azeron does its best to not to forget any message ever. Although nats already supports `streaming` or can turn itself to a `fire and know` system ([look here](https://nats-io.github.io/docs/developer/concepts/acks.html)) but it still forgets after a timeout or when client disconnects from nats server. Meanwhile, with nats streaming, we are loosing latency for message delivery.

Azeron Server can act as a big (possibly clustered) listener to all the channels clients are subscribing to using nats (Azeron Client). It persists all the messages so acknowledges are between Azeron server and client (not client to client). Clients can later query un-acknowledge messages from Azeron Server (which happens automatically in azeron environment).

The thoughts behind Azeron is to provide simple abstraction layer to anyone who wants to use it.
For example, the repository layer of Azeron server is abstract. You can implement it with any database you want. Or you can implement clustered nodes for high-availability. There are different examples of Azeron available [here](https://github.com/pinect-io/azeron-examples).