package it.unibo.KikiStore.view.entity.api;

/**
 * A DTO representing all the information needed to render an entity.
 * Being a record, ensures that the View cannot modify the Model's state.
 * 
 * @param x         The horizontal screen coordinate for rendering.
 * @param y         The vertical screen coordinate for rendering.
 * @param width     The display width of the entity.
 * @param height    The display height of the entity.
 * @param entityId  A unique identifier used to select the correct spritesheet (e.g., "kiki").
 * @param state     The current animation state (e.g., "idle", "walk").
 * @param direction The current facing direction (e.g., "up", "down", "left", "right").
 */
public record EntityRenderData (
    double x, 
    double y, 
    double width, 
    double height, 
    String entityId, 
    String state,     
    String direction  
){}
