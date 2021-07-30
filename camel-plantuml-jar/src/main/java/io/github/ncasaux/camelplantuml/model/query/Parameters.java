package io.github.ncasaux.camelplantuml.model.query;

public class Parameters {

    private final boolean connectRoutes;

    public Parameters(boolean connectRoutes) {
        this.connectRoutes = connectRoutes;
    }

    public boolean connectRoutes() {
        return connectRoutes;
    }
}
