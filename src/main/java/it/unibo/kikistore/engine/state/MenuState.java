package it.unibo.kikistore.engine.state;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.InventoryController;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.controller.api.RecipeBookController;
import it.unibo.kikistore.controller.impl.InventoryControllerImpl;
import it.unibo.kikistore.controller.impl.PlayerControllerImpl;
import it.unibo.kikistore.controller.impl.RecipeBookControllerImpl;
import it.unibo.kikistore.engine.api.GameState;
import it.unibo.kikistore.engine.api.GameStateTransition;
import it.unibo.kikistore.model.inventory.api.GameCatalog;
import it.unibo.kikistore.model.inventory.impl.RecipeBookImpl;
import it.unibo.kikistore.model.player.impl.PlayerImpl;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Initial title and main menu screen.
 */
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "GameStateTransition is needed to manage game startup and state transitions"
)
public final class MenuState implements GameState {

    private static final String BACKGROUND_ID = "sprites/menu/background";
    private static final String RECIPES_CONFIG_PATH = "textFiles/recipes.json";
    private static final String FONT_FAMILY = "Helvetica";

    // Layout Constants
    private static final double BUTTON_WIDTH = 220.0;
    private static final double BUTTON_HEIGHT = 50.0;
    private static final double BUTTON_GAP = 15.0;
    private static final double MARGIN_LEFT = 60.0;
    private static final double MARGIN_BOTTOM = 60.0;

    // Font Constants
    private static final double BUTTON_OPACITY = 0.95;
    private static final double BUTTON_CORNER_RADIUS = 12.0;
    private static final double BUTTON_BORDER_WIDTH = 2.5;
    private static final double BUTTON_FONT_SIZE = 22.0;

    // Color Palette
    private static final Color PLAY_BASE_COLOR = Color.web("#3d5e2a", BUTTON_OPACITY);
    private static final Color PLAY_BORDER_COLOR = Color.web("#5b8f36");
    private static final Color QUIT_BASE_COLOR = Color.web("#6e2233", BUTTON_OPACITY);
    private static final Color QUIT_BORDER_COLOR = Color.web("#9c334c");

    private final GameStateTransition transitionController;
    private final InputHandler input;
    private final PlayerController kiki;
    private final InventoryController inventory;
    private final RecipeBookController recipeBookController;
    private final SpriteManager spriteManager;
    private final GameCatalog catalog;

    private Rectangle2D startButtonBounds;
    private Rectangle2D quitButtonBounds;

    /**
     * Constructs a new MenuState.
     *
     * @param transitionController the controller handling state transitions
     * @param input the user input handler
     * @param spriteManager the manager caching graphic resources
     * @param catalog the global catalog holding game items and recipes
     */
    public MenuState(
        final GameStateTransition transitionController,
        final InputHandler input,
        final SpriteManager spriteManager,
        final GameCatalog catalog
    ) {
        this.transitionController = transitionController;
        this.input = input;
        this.spriteManager = spriteManager;
        this.catalog = catalog;
        this.inventory = new InventoryControllerImpl();
        this.recipeBookController = new RecipeBookControllerImpl(
            new RecipeBookImpl(RECIPES_CONFIG_PATH),
            this.inventory
        );
        this.kiki = new PlayerControllerImpl(new PlayerImpl(0.0, 0.0), this.input);
    }

    @Override
    public void init() { }

    @SuppressFBWarnings(
        value = "DM_EXIT",
        justification = "System.exit is required to enforce graceful application shutdown, especially on macOS >:("
    )
    @Override
    public void update() {
        if (input.isMouseClicked() && startButtonBounds != null && quitButtonBounds != null) {
            final double mouseX = input.getMouseX();
            final double mouseY = input.getMouseY();

            if (startButtonBounds.contains(mouseX, mouseY)) {
                transitionController.pushState(new VillageState(
                    transitionController,
                    input,
                    kiki,
                    inventory,
                    recipeBookController,
                    spriteManager,
                    catalog
                ));
            } else if (quitButtonBounds.contains(mouseX, mouseY)) {
                Platform.exit();
                System.exit(0);
            }
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth();
        final double screenHeight = gc.getCanvas().getHeight();

        gc.save();
        gc.setImageSmoothing(false);

        final Image background = spriteManager.getStaticSprite(BACKGROUND_ID);
        if (background != null) {
            gc.drawImage(background, 0, 0, screenWidth, screenHeight);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, screenWidth, screenHeight);
        }

        // Button positions
        final double btnX = MARGIN_LEFT;
        final double quitY = screenHeight - MARGIN_BOTTOM - BUTTON_HEIGHT;
        final double startY = quitY - BUTTON_HEIGHT - BUTTON_GAP;

        this.startButtonBounds = new Rectangle2D(btnX, startY, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.quitButtonBounds = new Rectangle2D(btnX, quitY, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Draw buttons
        drawButton(gc, startButtonBounds, "GIOCA", PLAY_BASE_COLOR, PLAY_BORDER_COLOR);
        drawButton(gc, quitButtonBounds, "ESCI", QUIT_BASE_COLOR, QUIT_BORDER_COLOR);

        gc.restore();
    }

    private void drawButton(
        final GraphicsContext gc,
        final Rectangle2D bounds,
        final String text,
        final Color baseColor,
        final Color borderColor
    ) {
        // Draw button background
        gc.setFill(baseColor);
        gc.fillRoundRect(
            bounds.getMinX(),
            bounds.getMinY(),
            bounds.getWidth(),
            bounds.getHeight(),
            BUTTON_CORNER_RADIUS,
            BUTTON_CORNER_RADIUS
        );

        // Draw button border
        gc.setStroke(borderColor);
        gc.setLineWidth(BUTTON_BORDER_WIDTH);
        gc.strokeRoundRect(
            bounds.getMinX(),
            bounds.getMinY(),
            bounds.getWidth(),
            bounds.getHeight(),
            BUTTON_CORNER_RADIUS,
            BUTTON_CORNER_RADIUS
        );

        // Draw button text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, BUTTON_FONT_SIZE));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        final double textCenterX = bounds.getMinX() + (bounds.getWidth() / 2.0);
        final double textCenterY = bounds.getMinY() + (bounds.getHeight() / 2.0);
        gc.fillText(text, textCenterX, textCenterY);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
