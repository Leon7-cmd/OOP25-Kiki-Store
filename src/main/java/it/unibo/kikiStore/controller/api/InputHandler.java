package it.unibo.kikiStore.controller.api;

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
     * Checks whether the mouse was clicked since the last time this was checked.
     * Applies the click - returns true only once per click.
     *
     * @return true if a click happened and hasn't been applied yet
     */
    boolean isMouseClicked();

    /**
     * Returns the x coordinate of the last mouse click.
     *
     * @return the mouse click x position
     */
    double getMouseX();

    /**
     * Returns the y coordinate of the last mouse click.
     *
     * @return the mouse click y position
     */
    double getMouseY();

    /**
     * Checks if the Escape key was pressed.
     * 
     * @return true if Escape was pressed.
     */
    boolean isEscapePressed();

    /**
     * Checks if the Inventory key was pressed.
     * 
     * @return true if Inventory was pressed.
     */
    boolean isInventoryPressed();

    /**
     * Checks if the Crafting key was pressed.
     * 
     * @return true if Crafting was pressed.
     */
    boolean isCraftingPressed();
}
