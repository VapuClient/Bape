package mc.bape.api.miliblue;

public interface EventListener<E extends Event> {
    void onEvent(E event);
}
