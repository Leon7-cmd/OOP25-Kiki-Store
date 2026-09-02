package it.unibo.KikiStore.controller.api;

/**
 * Interface representing the player's input state.
 * It abstracts the physical input source into logical game actions.
 */
public interface InputHandler {
    /**
     * Up variable.
     * 
     * @return the Up boolean
     */
    boolean isUp();

    /**
     * Down variable.
     * 
     * @return the Down boolean
     */
    boolean isDown();

    /**
     * Left variable.
     * 
     * @return the Left boolean
     */
    boolean isLeft();

    /**
     * Right variable.
     * 
     * @return the Right boolean
     */
    boolean isRight();

    /**
     * Action variable.
     * 
     * @return the Action boolean
     */
    boolean isAction();

    /**
     * Reset the action variable.
     */
    void resetAction();
}
