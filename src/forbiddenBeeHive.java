import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//Represents a single square of the game area
class Cell {
  // represents absolute height of this cell, in feet
  double height;
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  // the four adjacent cells to this one
  Cell topLeft;
  Cell top;
  Cell topRight;
  Cell bottomRight;
  Cell bottom;
  Cell bottomLeft;
  // reports whether this cell is flooded or not
  boolean isFlooded;
  boolean isHighest;

  // constructors

  public Cell(double height, int x, int y, boolean isFlooded) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.topLeft = null;
    this.top = null;
    this.topRight = null;
    this.bottomRight = null;
    this.bottom = null;
    this.bottomLeft = null;
    this.isFlooded = isFlooded;
    this.isHighest = false;
  }

  public Cell(double height, int x, int y, Cell topLeft, Cell top, Cell topRight, Cell bottomRight,
      Cell bottom, Cell bottomLeft, boolean isFlooded, boolean isHighest) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.topLeft = topLeft;
    this.top = top;
    this.topRight = topRight;
    this.bottomRight = bottomRight;
    this.bottom = bottom;
    this.bottomLeft = bottomLeft;
    this.isFlooded = false;
    this.isHighest = false;
  }

  // draws the image of a cell
  WorldImage drawTile(int waterHeight) {
    Color color;
    // flooded cell
    if (this.isFlooded) {
      color = new Color(0, 0, (int) Math.max(100, 255 - (waterHeight - this.height) * 5));
    }
    // cell in danger of flooding
    else if (this.height <= waterHeight + 1) {
      color = new Color((int) (Math.min(255, 180 + (waterHeight - this.height) * 10)), 75, 75);
    }
    // cell above waterheight
    else {
      color = new Color((int) Math.min(255, (this.height - waterHeight)),
          (int) Math.min(255, 100 + (this.height - waterHeight)),
          (int) Math.min(255, (this.height - waterHeight)));
    }
    // allows for a centered island in both even and odd island size cases
    if (ForbiddenIslandWorld.ISLAND_SIZE % 2 == 0) {
      return new HexagonImage(
          (double) (ForbiddenIslandWorld.WINDOW_SIZE / ForbiddenIslandWorld.ISLAND_SIZE + 1)
              - (ForbiddenIslandWorld.WINDOW_SIZE / ForbiddenIslandWorld.ISLAND_SIZE + 1) / 2.3,
          "solid", color);

    }
    else {
      return new HexagonImage(
          (double) (ForbiddenIslandWorld.WINDOW_SIZE / ForbiddenIslandWorld.ISLAND_SIZE)
              - (ForbiddenIslandWorld.WINDOW_SIZE / ForbiddenIslandWorld.ISLAND_SIZE) / 2.3,
          "solid", color);
    }

  }

  // floods the cell
  public void floodCell(int waterHeight) {
    if (waterHeight > this.height && this.hasOceanNeighbor()) {
      this.isFlooded = true;
    }

  }

  // // builds up the land of this cell and its neighbors
  // void build(int acc, int waterheight) {
  // if (this.isFlooded) {
  // this.height = waterheight + 5;
  // this.isFlooded = false;
  // }
  // if (acc > 0) {
  // this.top.build(acc - 1, waterheight);
  // this.bottom.build(acc - 1, waterheight);
  // this.left.build(acc - 1, waterheight);
  // this.right.build(acc - 1, waterheight);
  // }
  //
  // }

  // checks if this cell has a neighbor who is flooded
  boolean hasOceanNeighbor() {
    return this.top.isFlooded || this.topLeft.isFlooded || this.bottom.isFlooded
        || this.topRight.isFlooded || this.bottomRight.isFlooded || this.bottomLeft.isFlooded;
  }

  // checks if this cell is an ocean cell
  public boolean isOcean() {
    return false;
  }

  // checks for contact between this cell and an item in the list of helicoptor
  // parts
  public boolean contact(ArrayList<Target> targets) {
    for (Target t : targets) {
      if ((this.x - t.cell.x < 15 && this.x - t.cell.x > -15)
          && (this.y - t.cell.y < 15 && this.y - t.cell.y > -15)) {
        targets.remove(t);
        return true;
      }
    }
    return false;
  }

}

// to represent an ocean cell
class OceanCell extends Cell {
  public OceanCell(double height, int x, int y, Cell topLeft, Cell top, Cell topRight,
      Cell bottomRight, Cell bottom, Cell bottomLeft, boolean isFlooded) {
    super(height, x, y, topLeft, top, topRight, bottomRight, bottom, bottomLeft, isFlooded, false);
  }

  // constructor
  public OceanCell(double height, int x, int y, boolean isFlooded) {
    super(height, x, y, isFlooded);
    this.topLeft = null;
    this.top = null;
    this.topRight = null;
    this.bottomRight = null;
    this.bottom = null;
    this.bottomLeft = null;
  }

  // draws the image of the ocean cell
  public WorldImage drawTile(int waterHeight) {
    Color color = new Color(0, 0, 155);
    if (this.height > waterHeight + 1) {
      color = new Color((int) Math.min(255, (this.height - waterHeight)),
          (int) Math.min(255, 100 + (this.height - waterHeight)),
          (int) Math.min(255, (this.height - waterHeight)));
    }
    else if (this.height == waterHeight + 1) {
      color = new Color((int) (Math.min(255, 180 + (waterHeight - this.height) * 10)), 75, 75);
    }
    if (ForbiddenIslandWorld.ISLAND_SIZE % 2 == 0) {
      return new HexagonImage(
          (double) (ForbiddenIslandWorld.WINDOW_SIZE / ForbiddenIslandWorld.ISLAND_SIZE + 1)
              - (ForbiddenIslandWorld.WINDOW_SIZE / ForbiddenIslandWorld.ISLAND_SIZE + 1) / 2.3,
          "solid", color);

    }
    else {
      return new HexagonImage(
          (double) (ForbiddenIslandWorld.WINDOW_SIZE / ForbiddenIslandWorld.ISLAND_SIZE)
              - (ForbiddenIslandWorld.WINDOW_SIZE / ForbiddenIslandWorld.ISLAND_SIZE) / 2.3,
          "solid", color);
    }
  }

  // checks if this oceancell is an oceancell
  public boolean isOcean() {
    return true;
  }

}

// to represent the World
class ForbiddenIslandWorld extends World {
  // All the cells of the game, including the ocean
  IList<Cell> board;
  // the current height of the ocean
  int waterHeight;
  // Defines an int constant
  int iSize;
  int count;
  Player player;
  ArrayList<Target> targets;
  Swimsuit swimsuit;
  Helicopter heli;
  int swimCount;
  public static final int ISLAND_SIZE = 64;
  public static final int WINDOW_SIZE = 520;
  int score;

  // constructor
  public ForbiddenIslandWorld(IList<Cell> board, int waterHeight) {
    this.board = board;
    this.waterHeight = waterHeight;
    this.count = 0;
    this.player = new Player(this.board.randomCell(), 0, false);
    this.targets = this.generateTargets();
    this.swimsuit = new Swimsuit(this.board.randomCell());
    this.heli = this.crashLand();
    this.swimCount = 0;
    this.score = 0;
  }

  // lands the helicoptor at the highest cell
  Helicopter crashLand() {
    for (Cell c : this.board) {
      if (c.isHighest) {
        return new Helicopter(c);
      }
    }
    throw new RuntimeException("Improper creation, no highest tile found");
  }

  // generates a list of randomly placed helicoptor parts
  ArrayList<Target> generateTargets() {
    ArrayList<Target> result = new ArrayList<Target>();
    for (int i = 0; i < 5; i++) {
      result.add(new HelicopterPart(this.board.randomCell()));
    }

    return result;
  }

  // draws the image of a tile onto the scene
  void placeTile(Cell c, WorldScene w) {
    w.placeImageXY(c.drawTile(this.waterHeight), c.x, c.y);
  }

  // draws the image of a target onto the scene
  void placeTarget(Target t, WorldScene w) {
    w.placeImageXY(t.targetImage(), t.cell.x, t.cell.y);
  }

  // draws the scene that the user interacts with
  @Override
  public WorldScene makeScene() {
    WorldScene currScene = this.getEmptyScene();
    for (Cell c : this.board) {
      this.placeTile(c, currScene);
    }
    for (Target t : this.targets) {
      this.placeTarget(t, currScene);
    }
    currScene.placeImageXY(heli.targetImage(), heli.cell.x, heli.cell.y);
    currScene.placeImageXY(swimsuit.targetImage(), swimsuit.cell.x, swimsuit.cell.y);
    currScene.placeImageXY(player.playerImage(), player.x, player.y);
    currScene.placeImageXY(new TextImage("Score: " + this.score, Color.white), 480, 30);
    currScene.placeImageXY(
        new TextImage("Time till flooded: " + (int) (this.heli.cell.height - this.waterHeight),
            Color.ORANGE),
        440, 50);

    return currScene;
  }

  // game's on tick functionality
  public void onTick() {
    this.count += 1;
    this.swimCount += 1;
    if (this.count % 10 == 0) {
      this.waterHeight += 1;
    }
    if (this.swimCount % 100 == 0) {
      this.player.swimming = false;
    }
    this.player.drown();
    this.flood();
  }

  // ends the game
  public WorldEnd worldEnds() {
    WorldScene tempScene = this.getEmptyScene();

    if (this.player.drowned) {
      tempScene.placeImageXY(new TextImage("U lose ;(", 100, FontStyle.BOLD_ITALIC, Color.red), 250,
          250);
      tempScene.placeImageXY(
          new TextImage("Score: " + this.score, 50, FontStyle.BOLD_ITALIC, Color.red), 250, 400);
      return new WorldEnd(true, tempScene);

    }
    else if (this.heli.isBoarded) {
      tempScene.placeImageXY(new TextImage("U win ;)", 100, FontStyle.BOLD_ITALIC, Color.red), 250,
          250);
      tempScene.placeImageXY(
          new TextImage("Score: " + this.score, 50, FontStyle.BOLD_ITALIC, Color.red), 250, 400);
      return new WorldEnd(true, tempScene);
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // floods the tiles
  public void flood() {
    for (Cell c : this.board) {
      c.floodCell(this.waterHeight);
    }
  }

  // handles all on key events
  public void onKeyEvent(String ke) {
    if (ke.equals("q") && (!this.player.cell.topLeft.isFlooded || this.player.swimming)) {
      this.player.updateCell(this.player.cell.topLeft);
      this.player.loot(this.targets, this.swimsuit, this.heli);
      this.score += 1;
    }

    if (ke.equals("w") && (!this.player.cell.top.isFlooded || this.player.swimming)) {
      this.player.updateCell(this.player.cell.top);
      this.player.loot(this.targets, this.swimsuit, this.heli);
      this.score += 1;
    }
    if (ke.equals("e") && (!this.player.cell.topRight.isFlooded || this.player.swimming)) {
      this.player.updateCell(this.player.cell.topRight);
      this.player.loot(this.targets, this.swimsuit, this.heli);
      this.score += 1;
    }
    if (ke.equals("s") && (!this.player.cell.bottom.isFlooded || this.player.swimming)) {
      this.player.updateCell(this.player.cell.bottom);
      this.player.loot(this.targets, this.swimsuit, this.heli);
      this.score += 1;
    }

    if (ke.equals("a") && (!this.player.cell.bottomLeft.isFlooded || this.player.swimming)) {
      this.player.updateCell(this.player.cell.bottomLeft);
      this.player.loot(this.targets, this.swimsuit, this.heli);
      this.score += 1;
    }
    if (ke.equals("d") && (!this.player.cell.bottomRight.isFlooded || this.player.swimming)) {
      this.player.updateCell(this.player.cell.bottomRight);
      this.player.loot(this.targets, this.swimsuit, this.heli);
      this.score += 1;
    }

    if (ke.equals("z") && this.player.hasSwimsuit && !this.player.swimming) {
      this.swimCount = 0;
      this.player.swimming = true;

    }

    Utils utils = new Utils();
    if (ke.equals("r")) {
      this.count = 0;
      this.waterHeight = 0;
      this.board = utils
          .arrayListToIList(utils.makeCellList(utils.makeMountain(true, this.waterHeight), 0));
      this.player = new Player(this.board.randomCell(), 0, false);
      this.targets = this.generateTargets();
      this.swimsuit = new Swimsuit(this.board.randomCell());
      this.heli = this.crashLand();
      this.score = 0;
    }
    if (ke.equals("m")) {
      this.count = 0;
      this.waterHeight = 32;
      this.board = utils
          .arrayListToIList(utils.makeCellList(utils.makeMountain(false, this.waterHeight), 32));
      this.player = new Player(this.board.randomCell(), 0, false);
      this.targets = this.generateTargets();
      this.swimsuit = new Swimsuit(this.board.randomCell());
      this.heli = this.crashLand();
      this.score = 0;
    }
    if (ke.equals("t")) {
      this.count = 0;
      this.waterHeight = 25;
      this.board = utils.arrayListToIList(utils.makeCellList(utils.initTerrain(), 25));
      this.player = new Player(this.board.randomCell(), 0, false);
      this.targets = this.generateTargets();
      this.swimsuit = new Swimsuit(this.board.randomCell());
      this.heli = this.crashLand();
      this.score = 0;
    }
    else {
      return;
    }
  }

}

// to represent a player
class Player {
  Cell cell;
  int x;
  int y;
  int items;
  boolean hasSwimsuit;
  boolean drowned;
  boolean swimming;

  // constructor
  public Player(Cell cell, int items, boolean hasSwimsuit) {
    this.cell = cell;
    this.x = cell.x;
    this.y = cell.y;
    this.items = items;
    this.hasSwimsuit = hasSwimsuit;
    this.drowned = false;
    this.swimming = false;
  }

  // handles all interaction with targets as the player
  public void loot(ArrayList<Target> targets, Swimsuit s, Helicopter h) {
    if (this.cell.contact(targets)) {
      this.pickupItem();
    }

    if ((this.x - s.cell.x < 15 && this.x - s.cell.x > -15)
        && (this.y - s.cell.y < 15 && this.y - s.cell.y > -15)) {
      this.activateSwimsuit();
      s.remove();
    }

    if ((this.x - h.cell.x < 15 && this.x - h.cell.x > -15)
        && (this.y - h.cell.y < 15 && this.y - h.cell.y > -15) && this.items == 5) {
      h.board();
    }

  }

  // drowns the player
  public void drown() {
    if (this.cell.isFlooded && !this.swimming) {
      this.drowned = true;
    }
  }

  // the player icon
  public WorldImage playerImage() {
    return new FromFileImage("Webp.net-resizeimage (3).png");

  }

  // updates the current player position
  void updateCell(Cell newCell) {
    this.cell = newCell;
    this.x = this.cell.x;
    this.y = this.cell.y;
  }

  // adds another item when player picks up helicopter part
  void pickupItem() {
    this.items += 1;
  }

  // returns true when player picks up swimsuit
  void activateSwimsuit() {
    this.hasSwimsuit = true;
  }
}

// to represent a second player
class Player2 extends Player {

  public Player2(Cell cell, int items, boolean hasSwimsuit) {
    super(cell, items, hasSwimsuit);
  }

  // the player icon
  public WorldImage playerImage() {
    return new FromFileImage("Webp.net-resizeimage (1).png");

  }

  // handles the interaction between this player2 and all targets generated
  public void loot(ArrayList<Target> targets, Swimsuit s, Helicopter h, Player p1) {
    if (this.cell.contact(targets)) {
      p1.pickupItem();
    }

    if ((this.x - s.cell.x < 15 && this.x - s.cell.x > -15)
        && (this.y - s.cell.y < 15 && this.y - s.cell.y > -15)) {
      this.activateSwimsuit();
      s.remove();
    }

    if ((this.x - h.cell.x < 15 && this.x - h.cell.x > -15)
        && (this.y - h.cell.y < 15 && this.y - h.cell.y > -15) && p1.items == 5
        && (p1.x - h.cell.x < 15 && p1.x - h.cell.x > -15)
        && (p1.y - h.cell.y < 15 && p1.y - h.cell.y > -15)) {
      h.board();
    }

  }

}

// to represent an object in the game to be picked up
abstract class Target {
  Cell cell;

  // constructor
  public Target(Cell cell) {
    this.cell = cell;
  }

  abstract WorldImage targetImage();

}

// to represent a helicopter
class Helicopter extends Target {
  boolean isBoarded;

  // constructor
  public Helicopter(Cell cell) {
    super(cell);
    this.isBoarded = false;
  }

  // draws the helicopter image
  public WorldImage targetImage() {
    return new FromFileImage("Webp.net-resizeimage (4).png");

  }

  // changes the state of the helicopter to be boarded to end the game
  public void board() {
    this.isBoarded = true;
  }

}

// to represent a helicopter part
class HelicopterPart extends Target {

  // constructor
  public HelicopterPart(Cell cell) {
    super(cell);
  }

  // draws the image of a target (a cog, representing a helicopter part)
  public WorldImage targetImage() {
    return new FromFileImage("Webp.net-resizeimage (5).png");

  }

}

// to represent a swimsuit object that allows the player to swim through flooded
// cells
class Swimsuit extends Target {

  // constructor
  public Swimsuit(Cell cell) {
    super(cell);
  }

  // draws the image of a snorkel
  public WorldImage targetImage() {
    return new FromFileImage("Webp.net-resizeimage (6).png");

  }

  // removes this object by setting its cell to a cell that is not a part of the
  // world's board
  public void remove() {
    this.cell = new Cell(0.0, -10, -10, false);
  }

}

// to represent the utilities and generation of objects
class Utils {
  // makes a regular diamond shaped island that of either random heights or with a
  // normal spread of heights
  ArrayList<ArrayList<Double>> makeMountain(boolean isRandom, int waterheight) {
    ArrayList<Double> column = new ArrayList<Double>();
    ArrayList<ArrayList<Double>> listOfColumns = new ArrayList<ArrayList<Double>>();
    if (isRandom) {
      for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i++) {
        for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j++) {
          if (ForbiddenIslandWorld.ISLAND_SIZE
              - (Math.abs(ForbiddenIslandWorld.ISLAND_SIZE / 2 - i) + Math
                  .abs(ForbiddenIslandWorld.ISLAND_SIZE / 2 - j)) < ForbiddenIslandWorld.ISLAND_SIZE
                      / 2 + 1) {
            column.add(0.0);
          }
          else {
            column.add((double) new Random().nextInt(ForbiddenIslandWorld.ISLAND_SIZE - waterheight)
                + waterheight);
          }
          if (ForbiddenIslandWorld.ISLAND_SIZE % 2 == 0
              && column.size() == ForbiddenIslandWorld.ISLAND_SIZE) {
            column.add(0.0);
          }
        }
        listOfColumns.add(column);
        column = new ArrayList<Double>();
      }
      listOfColumns.get(ForbiddenIslandWorld.ISLAND_SIZE / 2)
          .set(ForbiddenIslandWorld.ISLAND_SIZE / 2, (double) ForbiddenIslandWorld.ISLAND_SIZE);
      if (ForbiddenIslandWorld.ISLAND_SIZE % 2 == 0) {
        for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i++) {
          column.add(0.0);
        }
        listOfColumns.add(column);
      }
      return listOfColumns;
    }
    else {
      for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i++) {
        for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j++) {
          if (ForbiddenIslandWorld.ISLAND_SIZE
              - (Math.abs(ForbiddenIslandWorld.ISLAND_SIZE / 2 - i) + Math
                  .abs(ForbiddenIslandWorld.ISLAND_SIZE / 2 - j)) < ForbiddenIslandWorld.ISLAND_SIZE
                      / 2 + 1) {
            column.add(0.0);
          }
          else {
            column.add((double) ForbiddenIslandWorld.ISLAND_SIZE
                - (Math.abs(ForbiddenIslandWorld.ISLAND_SIZE / 2 - i)
                    + Math.abs(ForbiddenIslandWorld.ISLAND_SIZE / 2 - j)));
          }
          if (ForbiddenIslandWorld.ISLAND_SIZE % 2 == 0
              && column.size() == ForbiddenIslandWorld.ISLAND_SIZE) {
            column.add(0.0);
          }
        }
        listOfColumns.add(column);
        column = new ArrayList<Double>();
      }
      if (ForbiddenIslandWorld.ISLAND_SIZE % 2 == 0) {
        for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i++) {
          column.add(0.0);
        }
        listOfColumns.add(column);
      }
      return listOfColumns;
    }
  }

  // initializes the terrain so the corners are height zero the center is Island
  // size height and the center of
  // the edges are 1
  ArrayList<ArrayList<Double>> initTerrain() {
    ArrayList<Double> column = new ArrayList<Double>();
    ArrayList<ArrayList<Double>> listOfColumns = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i++) {

      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE + 1; j++) {
        column.add(0.0);

      }
      listOfColumns.add(column);
      column = new ArrayList<Double>();
    }
    int mid = ForbiddenIslandWorld.ISLAND_SIZE / 2;
    listOfColumns.get(0).set(mid, 1.0);
    listOfColumns.get(mid).set(ForbiddenIslandWorld.ISLAND_SIZE - 1, 1.0);
    listOfColumns.get(mid).set(0, 1.0);
    listOfColumns.get(mid).set((int) ForbiddenIslandWorld.ISLAND_SIZE / 2,
        (double) ForbiddenIslandWorld.ISLAND_SIZE);
    listOfColumns.get(ForbiddenIslandWorld.ISLAND_SIZE - 1).set(mid, 1.0);
    listOfColumns.get(ForbiddenIslandWorld.ISLAND_SIZE / 2)
        .set(ForbiddenIslandWorld.ISLAND_SIZE / 2, (double) ForbiddenIslandWorld.ISLAND_SIZE);

    this.smooth(listOfColumns, 0, 0, listOfColumns.size() - 1, listOfColumns.size() - 1);
    return listOfColumns;
  }

  // smooths the terrain into a more regular looking terrain
  void smooth(ArrayList<ArrayList<Double>> listOfColumns, int loy, int lox, int hiy, int hix) {
    int midy = (int) Math.ceil((loy + hiy) / 2.0);
    int midx = (int) Math.ceil((lox + hix) / 2.0);
    int area = (hix - lox) * (hiy - loy);
    double nudge = Math.sqrt(area / Math.pow(ForbiddenIslandWorld.ISLAND_SIZE, 2));
    if (area < 2) {
      return;
    }

    // set unchanged cells to zero then only change unchanged cells
    else {
      double tl = listOfColumns.get(lox).get(loy);
      double bl = listOfColumns.get(lox).get(hiy);
      double tr = listOfColumns.get(hix).get(loy);
      double br = listOfColumns.get(hix).get(hiy);
      double t = (new Random().nextInt((int) ForbiddenIslandWorld.ISLAND_SIZE / 2)) * nudge
          + (tl + tr) / 2;
      double b = (new Random().nextInt((int) ForbiddenIslandWorld.ISLAND_SIZE / 2)) * nudge
          + (bl + br) / 2;
      double l = (new Random().nextInt((int) ForbiddenIslandWorld.ISLAND_SIZE / 2)) * nudge
          + (tl + bl) / 2;
      double r = (new Random().nextInt((int) ForbiddenIslandWorld.ISLAND_SIZE / 2)) * nudge
          + (tr + br) / 2;
      double m = (new Random().nextInt((int) ForbiddenIslandWorld.ISLAND_SIZE / 2)) * nudge
          + (tl + tr + bl + br) / 4;

      if (listOfColumns.get(midx).get(midy) == 0.0) {
        listOfColumns.get(midx).set(midy, m);
      }
      if (listOfColumns.get(lox).get(midy) == 0.0) {
        listOfColumns.get(lox).set(midy, l);
      }
      if (listOfColumns.get(midx).get(loy) == 0.0) {
        listOfColumns.get(midx).set(loy, t);
      }
      if (listOfColumns.get(midx).get(hiy) == 0.0) {
        listOfColumns.get(midx).set(hiy, b);
      }
      if (listOfColumns.get(hix).get(midy) == 0.0) {
        listOfColumns.get(hix).set(midy, r);
      }
      this.smooth(listOfColumns, midy, midx, hiy, hix);
      this.smooth(listOfColumns, loy, lox, midy, midx);
      this.smooth(listOfColumns, midy, lox, hiy, midx);
      this.smooth(listOfColumns, loy, midx, midy, hix);
    }

  }

  // transforms an arraylist of doubles to an arraylist of cells
  ArrayList<ArrayList<Cell>> makeCellList(ArrayList<ArrayList<Double>> listOfHeights,
      int waterHeight) {
    ArrayList<Cell> result = new ArrayList<Cell>();
    ArrayList<ArrayList<Cell>> listOfResult = new ArrayList<ArrayList<Cell>>();
    Double highestCellHeight = 0.0;
    for (int i = 0; i < listOfHeights.size(); i++) {
      ArrayList<Double> column = listOfHeights.get(i);
      for (int j = 0; j < column.size(); j++) {
        if (i % 2 == 0) {
          if (column.get(j) <= waterHeight) {
            result.add(new OceanCell(waterHeight - 1,
                i * (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size())
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2,
                j * (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size())
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2,
                true));
          }
          else {
            if (column.get(j) > highestCellHeight) {
              highestCellHeight = column.get(j);
            }
            result.add(new Cell(column.get(j),
                i * (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size())
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2,
                j * (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size())
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2,
                false));
          }
        }
        else {
          if (column.get(j) <= waterHeight) {
            result.add(new OceanCell(waterHeight - 1,
                i * (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size())
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2,
                ((j * (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size())
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2)
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2),
                true));
          }
          else {
            if (column.get(j) > highestCellHeight) {
              highestCellHeight = column.get(j);
            }
            result.add(new Cell(column.get(j),
                i * (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size())
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2,
                ((j * (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size())
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2)
                    + (ForbiddenIslandWorld.WINDOW_SIZE / listOfHeights.size()) / 2),
                false));
          }
        }
      }
      listOfResult.add(result);
      result = new ArrayList<Cell>();
    }
    fixNeighbors(listOfResult);
    for (ArrayList<Cell> column : listOfResult) {
      for (Cell c : column) {
        if (c.height == highestCellHeight) {
          c.isHighest = true;
        }
      }
    }
    return listOfResult;
  }

  // fixes the neighbors of each cell
  void fixNeighbors(ArrayList<ArrayList<Cell>> listOfResult) {
    ArrayList<Cell> prevColumn = new ArrayList<Cell>();
    ArrayList<Cell> nextColumn = new ArrayList<Cell>();
    for (int i = 0; i < listOfResult.size(); i++) {
      if (i != 0) {
        prevColumn = listOfResult.get(i - 1);
      }
      if (i != listOfResult.size() - 1) {
        nextColumn = listOfResult.get(i + 1);
      }
      ArrayList<Cell> currentColumn = listOfResult.get(i);
      boolean leftChanged = false;
      boolean rightChanged = false;
      if (i == 0) {
        for (Cell c : currentColumn) {
          c.topLeft = c;
          c.bottomLeft = c;
        }
        leftChanged = true;
      }
      else if (i == listOfResult.size() - 1) {
        for (Cell c : currentColumn) {
          c.topRight = c;
          c.bottomRight = c;
        }
        rightChanged = true;
      }
      for (int j = 0; j < listOfResult.size(); j++) {
        boolean topChanged = false;
        boolean bottomChanged = false;
        if (j == 0) {
          currentColumn.get(j).top = currentColumn.get(j);
          topChanged = true;
        }
        else if (j == listOfResult.size() - 1) {
          currentColumn.get(j).bottom = currentColumn.get(j);
          bottomChanged = true;
        }
        if (!topChanged) {
          currentColumn.get(j).top = currentColumn.get(j - 1);
        }
        if (!bottomChanged) {
          currentColumn.get(j).bottom = currentColumn.get(j + 1);
        }
        if (i % 2 == 1) {
          if (!leftChanged) {
            if (j == listOfResult.size() - 1) {
              currentColumn.get(j).bottomLeft = currentColumn.get(j);
            }
            else {
              currentColumn.get(j).bottomLeft = prevColumn.get(j + 1);
            }

            currentColumn.get(j).topLeft = prevColumn.get(j);
          }
          if (!rightChanged) {
            if (j == listOfResult.size() - 1) {
              currentColumn.get(j).bottomRight = currentColumn.get(j);
            }
            else {
              currentColumn.get(j).bottomRight = nextColumn.get(j + 1);
            }

            currentColumn.get(j).topRight = nextColumn.get(j);
          }

        }
        else {
          if (!leftChanged) {
            if (j == 0) {
              currentColumn.get(j).topLeft = currentColumn.get(j);
            }
            else {
              currentColumn.get(j).topLeft = prevColumn.get(j - 1);
            }

            currentColumn.get(j).bottomLeft = prevColumn.get(j);
          }
          if (!rightChanged) {
            if (j == 0) {
              currentColumn.get(j).topRight = currentColumn.get(j);
            }
            else {
              currentColumn.get(j).topRight = nextColumn.get(j - 1);
            }
            currentColumn.get(j).bottomRight = nextColumn.get(j);
          }

        }
      }
    }
  }

  // transforms the given arraylist of arraylist of cells to an IList<Cell>
  IList<Cell> arrayListToIList(ArrayList<ArrayList<Cell>> cellArrayList) {
    IList<Cell> result = new MtList<Cell>();
    for (ArrayList<Cell> column : cellArrayList) {
      for (Cell c : column) {
        result = new ConsList<Cell>(c, result);
      }
    }
    return result;
  }
}

// to represent an IList<T>
interface IList<T> extends Iterable<T> {
  boolean isCons();

  T randomCell();

  ConsList<T> asCons();

  int size();

  ArrayList<T> landCells();
}

// to represent an MtList<T>
class MtList<T> implements IList<T> {
  // Returns an IListIterator, and passes in this MtList
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

  // Is an MtList a ConsList? No.
  public boolean isCons() {
    return false;
  }

  // Throw an error because an MtList cannot become a ConsList
  public ConsList<T> asCons() {
    throw new IllegalArgumentException("MtList cannot be a ConsList");
  }

  // returns the size of the IList
  @Override
  public int size() {
    return 0;
  }

  // picks a random cell from this empty list of cells
  @Override
  public T randomCell() {
    throw new IllegalArgumentException("Board can't be empty");
  }

  // picks a random cell from this empty list of land cells
  @Override
  public ArrayList<T> landCells() {
    throw new IllegalArgumentException("Board can't be empty");

  }
}

// to represent a ConsList<T>
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  // constructor
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // Returns an IListIterator and passes in this ConsList
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

  // Return this ConsList as a ConsList object, so we can access its fields.
  public ConsList<T> asCons() {
    return this;
  }

  // Is a ConsList a ConsList? true
  public boolean isCons() {
    return true;
  }

  // returns the size of the IList
  @Override
  public int size() {
    int count = 0;
    for (T t : this) {
      count++;
    }
    return count;
  }

  // picks a random land cell from this Cons list of cells
  @Override
  public T randomCell() {
    ArrayList<T> landCells = new ArrayList<T>();
    for (T t : this) {
      if (!(t instanceof OceanCell)) {
        landCells.add(t);
      }
    }
    return landCells.get(new Random().nextInt(landCells.size()));

  }

  // selects all of the land cells from this ConsList of cells
  @Override
  public ArrayList<T> landCells() {
    ArrayList<T> landCells = new ArrayList<T>();
    for (T t : this) {
      if (!(t instanceof OceanCell)) {
        landCells.add(t);
      }
    }
    return landCells;
  }

}

// to represent an iterator
class IListIterator<T> implements Iterator<T> {
  IList<T> items;

  // constructor
  IListIterator(IList<T> items) {
    this.items = items;
  }

  // does this iterator have a next item to iterate to?
  public boolean hasNext() {
    return this.items.isCons();
  }

  // returns the next item to be iterated over
  public T next() {
    ConsList<T> itemsAsCons = this.items.asCons();
    T answer = itemsAsCons.first;
    this.items = itemsAsCons.rest;
    return answer;
  }
}

class ExamplesIsland {
  MtList<Cell> testBoard = new MtList<Cell>();
  MtList<Cell> testBoard2 = new MtList<Cell>();

  IList<Integer> Mt = new MtList<Integer>();
  IList<Integer> stuff = new ConsList<Integer>(1, new ConsList<Integer>(2,
      new ConsList<Integer>(3, new ConsList<Integer>(4, new ConsList<Integer>(5, this.Mt)))));
  Iterator<Integer> iter = new ConsList<Integer>(1,
      new ConsList<Integer>(2,
          new ConsList<Integer>(3, new ConsList<Integer>(4, new ConsList<Integer>(5, this.Mt)))))
              .iterator();
  Iterator<Integer> iter2 = new MtList<Integer>().iterator();
  Utils utils = new Utils();
  ForbiddenIslandWorld randomWorld = new ForbiddenIslandWorld(
      utils.arrayListToIList(utils.makeCellList(utils.makeMountain(true, 0), 0)), 0);
  ForbiddenIslandWorld normalWorld = new ForbiddenIslandWorld(
      utils.arrayListToIList(utils.makeCellList(utils.makeMountain(false, 0), 0)), 0);
  ForbiddenIslandWorld terrain = new ForbiddenIslandWorld(
      utils.arrayListToIList(utils.makeCellList(utils.initTerrain(), 0)), 0);

  OceanCell c14 = new OceanCell(0, 0, 0, true);
  Cell c15 = new Cell(1, 0, 1, false);
  Cell c16 = new Cell(1, 0, 2, false);
  OceanCell c17 = new OceanCell(0, 0, 3, true);
  Cell c18 = new Cell(1, 1, 0, false);
  Cell c19 = new Cell(5, 1, 2, false);
  Cell c20 = new Cell(1, 1, 2, false);
  OceanCell c21 = new OceanCell(0, 2, 0, true);
  Cell c22 = new Cell(1, 2, 1, false);
  Cell c23 = new Cell(1, 2, 2, false);
  OceanCell c24 = new OceanCell(0, 2, 3, true);

  ArrayList<Cell> col0 = new ArrayList(Arrays.asList(c14, c15, c16, c17));
  ArrayList<Cell> col1 = new ArrayList(Arrays.asList(c18, c19, c20));
  ArrayList<Cell> col2 = new ArrayList(Arrays.asList(c21, c22, c23, c24));
  ArrayList<ArrayList<Cell>> loc = new ArrayList<ArrayList<Cell>>(Arrays.asList(col0, col1, col2));

  // resets the cells
  void setNeighbors() {

    this.c14.height = 0;
    this.c15.height = 1;
    this.c16.height = 1;
    this.c17.height = 0;
    this.c18.height = 1;
    this.c19.height = 5;
    this.c20.height = 1;
    this.c21.height = 0;
    this.c22.height = 1;
    this.c23.height = 1;
    this.c24.height = 0;

    this.c14.isFlooded = true;
    this.c15.isFlooded = false;
    this.c16.isFlooded = false;
    this.c17.isFlooded = true;
    this.c18.isFlooded = false;
    this.c19.isFlooded = false;
    this.c20.isFlooded = false;
    this.c21.isFlooded = true;
    this.c22.isFlooded = false;
    this.c23.isFlooded = false;
    this.c24.isFlooded = true;

    c19.isHighest = true;
    this.c14.topLeft = c14;
    this.c14.top = c14;
    this.c14.topRight = c14;
    this.c14.bottomRight = c18;
    this.c14.bottom = c15;
    this.c14.bottomLeft = c14;
    this.c15.topLeft = c15;
    this.c15.top = c14;
    this.c15.topRight = c18;
    this.c15.bottomRight = c19;
    this.c15.bottom = c16;
    this.c15.bottomLeft = c16;
    this.c16.topLeft = c16;
    this.c16.top = c15;
    this.c16.topRight = c19;
    this.c16.bottomRight = c20;
    this.c16.bottom = c17;
    this.c16.bottomLeft = c16;
    this.c17.topLeft = c17;
    this.c17.top = c16;
    this.c17.topRight = c20;
    this.c17.bottomRight = c17;
    this.c17.bottom = c17;
    this.c17.bottomLeft = c17;
    this.c18.topLeft = c14;
    this.c18.top = c18;
    this.c18.topRight = c21;
    this.c18.bottomRight = c22;
    this.c18.bottom = c19;
    this.c18.bottomLeft = c15;
    this.c19.topLeft = c15;
    this.c19.top = c18;
    this.c19.topRight = c22;
    this.c19.bottomRight = c23;
    this.c19.bottom = c20;
    this.c19.bottomLeft = c16;
    this.c20.topLeft = c16;
    this.c20.top = c19;
    this.c20.topRight = c23;
    this.c20.bottomRight = c24;
    this.c20.bottom = c20;
    this.c20.bottomLeft = c17;
    this.c21.topLeft = c21;
    this.c21.top = c21;
    this.c21.topRight = c21;
    this.c21.bottomRight = c21;
    this.c21.bottom = c22;
    this.c21.bottomLeft = c18;
    this.c22.topLeft = c18;
    this.c22.top = c21;
    this.c22.topRight = c22;
    this.c22.bottomRight = c22;
    this.c22.bottom = c23;
    this.c22.bottomLeft = c19;
    this.c23.topLeft = c19;
    this.c23.top = c22;
    this.c23.topRight = c23;
    this.c23.bottomRight = c23;
    this.c23.bottom = c24;
    this.c23.bottomLeft = c20;
    this.c24.topLeft = c20;
    this.c24.top = c23;
    this.c24.topRight = c24;
    this.c24.bottomRight = c24;
    this.c24.bottom = c24;
    this.c24.bottomLeft = c24;

    this.c6.isHighest = true;
  }

  Cell c1 = new Cell(0, 1, 1, false);
  Cell c2 = new Cell(1, 2, 2, false);
  Cell c3 = new Cell(2, 3, 3, false);
  Cell c4 = new Cell(3, 4, 4, false);
  Cell c5 = new Cell(4, 5, 5, false);
  Cell c6 = new Cell(5, 6, 6, false);
  Cell c13 = new Cell(5, 6, 6, true);

  OceanCell c7 = new OceanCell(0, 0, 0, true);
  OceanCell c8 = new OceanCell(0, 0, 1, true);
  OceanCell c9 = new OceanCell(0, 0, 2, true);
  OceanCell c10 = new OceanCell(0, 0, 3, true);
  OceanCell c11 = new OceanCell(0, 0, 4, true);
  OceanCell c12 = new OceanCell(0, 0, 5, true);
  ArrayList<Cell> cl0 = new ArrayList(Arrays.asList(c1, c7, c8, c9, c10, c11, c12));

  ArrayList<Cell> cl1 = new ArrayList(Arrays.asList(c2, c3, c4, c5, c6));
  ArrayList<ArrayList<Cell>> cellList = new ArrayList<ArrayList<Cell>>(Arrays.asList(cl0, cl1));
  ArrayList<Cell> onecelll = new ArrayList(Arrays.asList(c1));
  ArrayList<ArrayList<Cell>> onecell = new ArrayList<ArrayList<Cell>>(Arrays.asList(onecelll));

  Target t1 = new HelicopterPart(c2);
  Target t2 = new HelicopterPart(c3);
  Target t3 = new HelicopterPart(c1);
  Target t4 = new HelicopterPart(c5);
  Target t5 = new HelicopterPart(c6);
  Target s = new Swimsuit(c6);
  Target h = new Helicopter(c6);
  Player p = new Player(c6, 0, false);

  ArrayList<Target> targets = new ArrayList(Arrays.asList(t1, t2, t3, t4, t5));

  // init target positions
  void initTargets() {
    this.targets = new ArrayList(Arrays.asList(t1, t2, t3, t4, t5));
  }

  // we have random functionality on our onTick making it hard to test, but we
  // test each method called inside individually
  // void testOnTick (Tester t) {
  // this.setNeighbors();
  // ForbiddenIslandWorld w = new
  // ForbiddenIslandWorld(utils.arrayListToIList(this.loc), 30);
  // w.onTick();
  // t.checkExpect(, "");
  // }

  // tests the onkey method
  void testOnKeyEvent(Tester t) {
    this.setNeighbors();
    ForbiddenIslandWorld w = new ForbiddenIslandWorld(utils.arrayListToIList(this.loc), 30);
    w.player = new Player(c19, 0, false);
    w.onKeyEvent("w");
    t.checkExpect(w.player.cell, c18);
    w.player = new Player(c19, 0, false);
    w.onKeyEvent("e");
    t.checkExpect(w.player.cell, c22);
    w.player = new Player(c19, 0, false);
    w.onKeyEvent("d");
    t.checkExpect(w.player.cell, c23);
    w.player = new Player(c19, 0, false);
    w.onKeyEvent("s");
    t.checkExpect(w.player.cell, c20);
    w.player = new Player(c19, 0, false);
    w.onKeyEvent("a");
    t.checkExpect(w.player.cell, c16);
    w.player = new Player(c19, 0, false);
    w.onKeyEvent("q");
    t.checkExpect(w.player.cell, c15);

    w.player = new Player(c14, 0, false);
    w.onKeyEvent("a");
    t.checkExpect(w.player.cell, c14);
    w.player = new Player(c14, 0, false);
    w.onKeyEvent("q");
    t.checkExpect(w.player.cell, c14);
    w.player = new Player(c14, 0, false);
    w.onKeyEvent("w");
    t.checkExpect(w.player.cell, c14);
    w.player = new Player(c14, 0, false);
    w.onKeyEvent("e");
    t.checkExpect(w.player.cell, c14);

    w.player = new Player(c21, 0, false);
    w.onKeyEvent("q");
    t.checkExpect(w.player.cell, c21);
    w.player = new Player(c21, 0, false);
    w.onKeyEvent("w");
    t.checkExpect(w.player.cell, c21);
    w.player = new Player(c21, 0, false);
    w.onKeyEvent("e");
    t.checkExpect(w.player.cell, c21);
    w.player = new Player(c21, 0, false);
    w.onKeyEvent("d");
    t.checkExpect(w.player.cell, c21);

    w.player = new Player(c17, 0, false);
    w.onKeyEvent("q");
    t.checkExpect(w.player.cell, c17);
    w.player = new Player(c17, 0, false);
    w.onKeyEvent("a");
    t.checkExpect(w.player.cell, c17);
    w.player = new Player(c17, 0, false);
    w.onKeyEvent("s");
    t.checkExpect(w.player.cell, c17);
    w.player = new Player(c17, 0, false);
    w.onKeyEvent("d");
    t.checkExpect(w.player.cell, c17);

    w.player = new Player(c24, 0, false);
    w.onKeyEvent("e");
    t.checkExpect(w.player.cell, c24);
    w.player = new Player(c24, 0, false);
    w.onKeyEvent("d");
    t.checkExpect(w.player.cell, c24);
    w.player = new Player(c24, 0, false);
    w.onKeyEvent("s");
    t.checkExpect(w.player.cell, c24);
    w.player = new Player(c24, 0, false);
    w.onKeyEvent("a");
    t.checkExpect(w.player.cell, c24);

    w.player.hasSwimsuit = true;
    w.onKeyEvent("z");
    t.checkExpect(w.player.swimming, true);

  }

  // tests the generate targets method
  void testGenerateTargets(Tester t) {
    this.setNeighbors();
    c1.isHighest = true;
    ForbiddenIslandWorld w = new ForbiddenIslandWorld(utils.arrayListToIList(this.onecell), 30);
    t.checkExpect(w.generateTargets().size(), 5);
  }

  // tests the target image method
  void testTargetImage(Tester t) {
    t.checkExpect(t1.targetImage(), new FromFileImage("Webp.net-resizeimage (5).png"));
    t.checkExpect(s.targetImage(), new FromFileImage("Webp.net-resizeimage (6).png"));
    t.checkExpect(h.targetImage(), new FromFileImage("Webp.net-resizeimage (4).png"));
  }

  // tests the player image method
  void testPlayerImage(Tester t) {
    t.checkExpect(p.playerImage(), new FromFileImage("Webp.net-resizeimage (3).png"));

  }

  // tests the end world method
  void testWorldEnds(Tester t) {
    c4.isHighest = true;
    ForbiddenIslandWorld w = new ForbiddenIslandWorld(new ConsList<Cell>(c1,
        new ConsList<Cell>(c2, new ConsList<Cell>(c3, new ConsList<Cell>(c4, new MtList<Cell>())))),
        0);
    w.player = new Player(c3, 5, false);

    t.checkExpect(w.worldEnds(), new WorldEnd(false, w.makeScene()));

    w.player.drowned = true;
    WorldScene loseScene = w.getEmptyScene();
    loseScene.placeImageXY(new TextImage("U lose ;(", 100, FontStyle.BOLD_ITALIC, Color.red), 250,
        250);
    loseScene.placeImageXY(new TextImage("Score: " + w.score, 50, FontStyle.BOLD_ITALIC, Color.red),
        250, 400);

    t.checkExpect(w.worldEnds(), new WorldEnd(true, loseScene));

    WorldScene winScene = w.getEmptyScene();
    winScene.placeImageXY(new TextImage("U win ;)", 100, FontStyle.BOLD_ITALIC, Color.red), 250,
        250);
    winScene.placeImageXY(new TextImage("Score: " + w.score, 50, FontStyle.BOLD_ITALIC, Color.red),
        250, 400);
    w.player = new Player(c3, 5, false);
    w.waterHeight = 0;
    w.player.drowned = false;
    w.player.updateCell(c4);
    w.heli.isBoarded = true;

    t.checkExpect(w.worldEnds(), new WorldEnd(true, winScene));

  }

  // tests the has ocean neighbor method
  void testHasOceanNeighbor(Tester t) {
    this.setNeighbors();
    t.checkExpect(this.c14.hasOceanNeighbor(), true);
    t.checkExpect(this.c15.hasOceanNeighbor(), true);
    t.checkExpect(this.c16.hasOceanNeighbor(), true);
    t.checkExpect(this.c17.hasOceanNeighbor(), true);
    t.checkExpect(this.c18.hasOceanNeighbor(), true);
    t.checkExpect(this.c19.hasOceanNeighbor(), false);

  }

  // tests the flood method
  void testFlood(Tester t) {
    this.setNeighbors();
    ForbiddenIslandWorld w = new ForbiddenIslandWorld(utils.arrayListToIList(loc), 4);

    t.checkExpect(c19.isFlooded, false);
    t.checkExpect(c15.isFlooded, false);
    t.checkExpect(c16.isFlooded, false);
    w.flood();
    t.checkExpect(c14.isFlooded, true);
    t.checkExpect(c19.isFlooded, false);
    t.checkExpect(c16.isFlooded, true);

  }

  // tests the loot method
  void testLoot(Tester t) {
    initTargets();
    Player p1 = new Player(c4, 0, false);
    Player2 p2 = new Player2(c3, 0, false);
    Helicopter heli = new Helicopter(c4);
    Swimsuit s = new Swimsuit(c4);
    t.checkExpect(p1.hasSwimsuit, false);
    p1.loot(this.targets, s, heli);
    t.checkExpect(s.cell.x, -10);
    t.checkExpect(s.cell.y, -10);
    t.checkExpect(p1.hasSwimsuit, true);
    p1.updateCell(c2);
    p1.loot(targets, s, heli);
    t.checkExpect(p1.items, 2);
    t.checkExpect(p1.hasSwimsuit, true);
    t.checkExpect(targets.size(), 3);
    p1.updateCell(c3);
    p1.loot(targets, s, heli);
    t.checkExpect(targets.size(), 2);
    t.checkExpect(p1.items, 3);

  }

  // tests the floodCell method
  void testFloodCell(Tester t) {
    this.setNeighbors();
    t.checkExpect(c15.isFlooded, false);
    c14.floodCell(76);
    t.checkExpect(c14.isFlooded, true);
    t.checkExpect(c15.isFlooded, false);
    c15.floodCell(0);
    t.checkExpect(c15.isFlooded, false);
  }

  // tests the contact method
  void testContact(Tester t) {
    initTargets();
    Player p1 = new Player(c2, 0, false);
    t.checkExpect(targets.size(), 5);
    t.checkExpect(p1.cell.contact(targets), true);
    t.checkExpect(targets.size(), 4);
    p1.updateCell(c4);
    t.checkExpect(p1.cell.contact(targets), true);
    p1.updateCell(c3);
    t.checkExpect(p1.cell.contact(targets), true);
    t.checkExpect(targets.size(), 2);

  }

  // tests the draw tile method
  void testDrawTile(Tester t) {
    t.checkExpect(c1.drawTile(0),
        new HexagonImage(5.08695652173913, "solid", new Color(180, 75, 75)));
    t.checkExpect(c12.drawTile(0),
        new HexagonImage(5.08695652173913, "solid", new Color(0, 0, 155)));
    t.checkExpect(c2.drawTile(5),
        new HexagonImage(5.08695652173913, "solid", new Color(220, 75, 75)));
    t.checkExpect(c13.drawTile(10),
        new HexagonImage(5.08695652173913, "solid", new Color(0, 0, 230)));
  }

  // tests the has next and next method
  void testHasNextAndNext(Tester t) {
    t.checkExpect(iter2.hasNext(), false);
    t.checkExpect(iter.hasNext(), true);
    t.checkExpect(iter.next(), 1);
    t.checkExpect(iter.hasNext(), true);
    t.checkExpect(iter.next(), 2);
    t.checkExpect(iter.hasNext(), true);
    t.checkExpect(iter.next(), 3);
    t.checkExpect(iter.hasNext(), true);
    t.checkExpect(iter.next(), 4);
    t.checkExpect(iter.hasNext(), true);
    t.checkExpect(iter.next(), 5);
    t.checkExpect(iter.hasNext(), false);
  }

  // tests the is ocean method
  void testIsOcean(Tester t) {
    t.checkExpect(utils.makeCellList(utils.makeMountain(false, 0), 0).get(0).get(0).isOcean(),
        true);
    t.checkExpect(utils.makeCellList(utils.makeMountain(false, 0), 0).get(20).get(20).isOcean(),
        false);
    t.checkExpect(utils.makeCellList(utils.makeMountain(false, 0), 0).get(32).get(32).isOcean(),
        false);
    t.checkExpect(utils.makeCellList(utils.makeMountain(true, 0), 0).get(64).get(0).isOcean(),
        true);

  }

  // tests the make mountain method
  void testMakeMountain(Tester t) {

    t.checkExpect(utils.makeMountain(false, 0).size(), 65);
    t.checkExpect(utils.makeMountain(false, 0).get(0).size(), 65);
    t.checkExpect(utils.makeMountain(false, 0).get(0).contains(0.0), true);
    t.checkExpect(utils.makeMountain(false, 0).get(64).contains(0.0), true);
    t.checkExpect(
        utils.makeMountain(false, 0).get(32).contains((double) ForbiddenIslandWorld.ISLAND_SIZE),
        true);

    t.checkExpect(utils.makeMountain(true, 0).size(), 65);
    t.checkExpect(utils.makeMountain(true, 0).get(0).size(), 65);
    t.checkExpect(utils.makeMountain(true, 0).get(0).contains(0.0), true);
    t.checkExpect(utils.makeMountain(true, 0).get(64).contains(0.0), true);
    t.checkExpect(
        utils.makeMountain(true, 0).get(32).contains((double) ForbiddenIslandWorld.ISLAND_SIZE),
        true);

  }

  // tests the make cell list method
  void testMakeCellList(Tester t) {

    t.checkExpect(utils.makeCellList(utils.makeMountain(false, 0), 1).size(), 65);
    t.checkExpect(utils.makeCellList(utils.makeMountain(false, 0), 1).get(0).size(), 65);
    t.checkExpect(
        utils.makeCellList(utils.makeMountain(false, 0), 1).get(0).get(0) instanceof OceanCell,
        true);
    t.checkExpect(
        utils.makeCellList(utils.makeMountain(false, 0), 1).get(32).get(32) instanceof Cell, true);

    t.checkExpect(utils.makeCellList(utils.makeMountain(true, 0), 1).size(), 65);
    t.checkExpect(utils.makeCellList(utils.makeMountain(true, 0), 1).get(0).size(), 65);
    t.checkExpect(
        utils.makeCellList(utils.makeMountain(true, 0), 1).get(0).get(0) instanceof OceanCell,
        true);
    t.checkExpect(utils.makeCellList(utils.makeMountain(true, 0), 1).get(0).get(0) instanceof Cell,
        true);

  }

  // tests the init terrain method
  void testInitTerrain(Tester t) {
    t.checkExpect(utils.initTerrain().size(), 65);
    t.checkExpect(utils.initTerrain().get(0).size(), 65);
  }

  // tests the smooth method
  void testSmooth(Tester t) {
    ArrayList<ArrayList<Double>> listOfColumns = utils.makeMountain(true, 0);
    int originalSize = listOfColumns.size();
    utils.smooth(listOfColumns, 0, 0, listOfColumns.size() - 1, listOfColumns.size() - 1);
    t.checkExpect(listOfColumns.size() == originalSize, true);
    t.checkNumRange(listOfColumns.get(32).get(0), 0, 67);
    t.checkNumRange(listOfColumns.get(64).get(0), 0, 67);
    t.checkExpect(listOfColumns.get(0).contains(0.0), true);
  }

  // tests the fixNeighbors method

  void testFixNeighbors(Tester t) {
    ArrayList<ArrayList<Cell>> listOfColumns = utils.makeCellList(utils.makeMountain(true, 0), 0);
    int originalSize = listOfColumns.size();
    utils.fixNeighbors(listOfColumns);
    t.checkExpect(listOfColumns.size(), originalSize);
    t.checkExpect(listOfColumns.get(0).get(0).topRight, listOfColumns.get(0).get(0));
    t.checkExpect(listOfColumns.get(0).get(0).bottomRight, listOfColumns.get(1).get(0));
    t.checkExpect(listOfColumns.get(0).get(0).top, listOfColumns.get(0).get(0));
    t.checkExpect(listOfColumns.get(0).get(0).bottom, listOfColumns.get(0).get(1));
    t.checkExpect(listOfColumns.get(1).get(1).bottomRight, listOfColumns.get(2).get(2));
    t.checkExpect(listOfColumns.get(1).get(1).bottomLeft, listOfColumns.get(0).get(2));
    t.checkExpect(listOfColumns.get(1).get(1).top, listOfColumns.get(1).get(0));
    t.checkExpect(listOfColumns.get(1).get(1).bottom, listOfColumns.get(1).get(2));
    t.checkExpect(listOfColumns.get(1).get(2).topRight, listOfColumns.get(2).get(2));
    t.checkExpect(listOfColumns.get(1).get(2).bottomLeft, listOfColumns.get(0).get(3));
  }

  // tests the arrayListToIList method
  void testArrayListToIList(Tester t) {

    t.checkExpect(
        utils.arrayListToIList(utils.makeCellList(utils.makeMountain(false, 0), 1)).size(), 4225);

    t.checkExpect(utils.arrayListToIList(utils.makeCellList(utils.makeMountain(true, 0), 1)).size(),
        4225);

    t.checkExpect(this.normalWorld.board.size(), 4225);
  }

  // tests the isCons() method
  void testIsCons(Tester t) {
    IList<Integer> mt = new MtList<Integer>();
    IList<Integer> cons = new ConsList<Integer>(1, new ConsList<Integer>(1, mt));
    t.checkExpect(mt.isCons(), false);
    t.checkExpect(cons.isCons(), true);
  }

  // tests the asCons() method
  void testAsCons(Tester t) {
    IList<Integer> mt = new MtList<Integer>();
    IList<Integer> cons = new ConsList<Integer>(1, new ConsList<Integer>(1, mt));
    t.checkException(new IllegalArgumentException("MtList cannot be a ConsList"), this.Mt,
        "asCons");
    t.checkExpect(cons.asCons(), cons);

  }

  // tests the randomCell() method
  void testRandomCell(Tester t) {
    IList<Cell> mt = new MtList<Cell>();
    IList<Cell> cons = new ConsList<Cell>(c10, new ConsList<Cell>(c2, mt));
    t.checkException(new IllegalArgumentException("Board can't be empty"), this.testBoard,
        "randomCell");

    t.checkExpect(cons.randomCell(), c2);
  }

  // tests the landCells() method
  void testLandCells(Tester t) {
    IList<Cell> mt = new MtList<Cell>();
    IList<Cell> cons = new ConsList<Cell>(c1, new ConsList<Cell>(c2, mt));
    t.checkException(new IllegalArgumentException("Board can't be empty"), this.testBoard2,
        "landCells");
    t.checkExpect(cons.landCells().size(), new ArrayList<Cell>(Arrays.asList(c1, c2)).size());

  }

  // tests the Remove method for swimsuits
  void testRemove(Tester t) {
    Swimsuit s = new Swimsuit(c1);
    t.checkExpect(s.cell.x, c1.x);
    s.remove();
    t.checkExpect(s.cell.x, -10);
  }

  // tests the activateSwimsuit method
  void testActivateSwimsuit(Tester t) {
    Player p1 = new Player(c1, 0, false);
    t.checkExpect(p1.hasSwimsuit, false);
    p1.activateSwimsuit();
    t.checkExpect(p1.hasSwimsuit, true);
  }

  // tests the pickUpItem method
  void testPickUpItem(Tester t) {
    Player p1 = new Player(c1, 0, false);
    t.checkExpect(p1.items == 0, true);
    p1.pickupItem();
    t.checkExpect(p1.items == 1, true);
    t.checkExpect(p1.items == 0, false);
    p1.pickupItem();
    t.checkExpect(p1.items == 2, true);
    t.checkExpect(p1.items == 1, false);
  }

  // tests the updateCell method
  void testUpdateCell(Tester t) {
    Player p1 = new Player(c1, 0, false);
    t.checkExpect(p1.cell, c1);
    p1.updateCell(c2);
    t.checkExpect(p1.cell, c2);
    t.checkExpect(p1.cell == c1, false);
  }

  // tests the drown method
  void testDrown(Tester t) {
    Player p1 = new Player(c10, 0, false);
    t.checkExpect(p1.drowned, false);
    p1.drown();
    t.checkExpect(p1.drowned, true);
  }

  // tests the helicopter method board()
  void testBoard(Tester t) {
    Helicopter h = new Helicopter(c1);
    t.checkExpect(h.isBoarded, false);
    h.board();
    t.checkExpect(h.isBoarded, true);
  }

  // tests the crashland() method

  void testCrashland(Tester t) {
    ArrayList<ArrayList<Cell>> hlist = utils.makeCellList(utils.makeMountain(false, 0), 0);
    ForbiddenIslandWorld w = new ForbiddenIslandWorld(
        utils.arrayListToIList(utils.makeCellList(utils.makeMountain(false, 0), 0)), 0);
    Helicopter h = w.crashLand();
    t.checkExpect(h.cell, hlist.get(32).get(32));
  }

  // tests the size method of ILists<T>
  void testSize(Tester t) {
    t.checkExpect(this.Mt.size(), 0);
    t.checkExpect(this.stuff.size(), 5);
  }

  void testWorld(Tester t) {
    // this.randomWorld.bigBang(520, 520, .05);
    this.normalWorld.bigBang(520, 520, .3);
    // this.terrain.bigBang(520, 520, .05);

  }

}
