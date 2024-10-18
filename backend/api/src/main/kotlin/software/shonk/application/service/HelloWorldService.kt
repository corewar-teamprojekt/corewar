package software.shonk.application.service

import software.shonk.application.port.incoming.HelloWorldUseCase

// Only do business logic on this layer, potentially calling into outgoing ports (e.g. DB, MARS
// interpreter whatever)
// Work with domain objects on this layer, only converting to DTO in the Controller if needed.
class HelloWorldService() : HelloWorldUseCase {

    override fun sayHello(): String {
        return "Hello world!"
    }
}
