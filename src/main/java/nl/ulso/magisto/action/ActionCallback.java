package nl.ulso.magisto.action;

/**
 * Callback called after an {@link Action} has been performed by the {@link ActionSet}.
 *
 * @see ActionSet
 */
public interface ActionCallback {

    void actionPerformed(Action action);
}
