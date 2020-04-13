package mazeSolver;

import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.WorldImage;

import java.awt.*;
import java.util.ArrayList;

//To represent a player in the game
public class Player {
    // this player's location
    Node loc;
    // number of wrong moves made by the player
    int wrongMoves = 0;

    Player(Node loc) {
        this.loc = loc;
    }

    // return a WorldImage representation of this player
    WorldImage playerImage() {
        WorldImage outline = new CircleImage(6, OutlineMode.SOLID, Color.BLACK);
        WorldImage player = new CircleImage(6, OutlineMode.OUTLINE, Color.WHITE);
        return new OverlayImage(outline, player);
    }

    // EFFECT: attempt to move this player up, if they made a wrong move
    // add one to their wrongMoves
    void moveUp(ArrayList<Node> path) {
        if (this.loc.up instanceof Node) {
            this.loc = this.loc.up;
            if (!path.contains(this.loc)) {
                this.wrongMoves += 1;
            }
        }
    }

    // EFFECT: attempt to move this player down, if they made a wrong move
    // add one to their wrongMoves
    void moveDown(ArrayList<Node> path) {
        if (this.loc.down instanceof Node) {
            this.loc = this.loc.down;
            if (!path.contains(this.loc)) {
                this.wrongMoves += 1;
            }
        }
    }

    // EFFECT: attempt to move this player left, if they made a wrong move
    // add one to their wrongMoves
    void moveLeft(ArrayList<Node> path) {
        if (this.loc.left instanceof Node) {
            this.loc = this.loc.left;
            if (!path.contains(this.loc)) {
                this.wrongMoves += 1;
            }
        }
    }

    // EFFECT: attempt to move this player right, if they made a wrong move
    // add one to their wrongMoves
    void moveRight(ArrayList<Node> path) {
        if (this.loc.right instanceof Node) {
            this.loc = this.loc.right;
            if (!path.contains(this.loc)) {
                this.wrongMoves += 1;
            }
        }
    }

    // return a string  revealing this player's score
    String getScore() {
        String result = "You made ";
        result = result + Integer.toString(this.wrongMoves) + " wrong moves";

        return result;
    }
}