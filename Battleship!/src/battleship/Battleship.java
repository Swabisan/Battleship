/*
 * Battleship! a board game in java.
 * Goal: To use what I learned from CSC 210 to create the board game "Battleship" as a java program.
 * To do:
 * - add display to aid ship placement [DONE]
 * - label axis on display [DONE]
 * - optimize user-input-checks [MOSTLY FIXED] *(Need to add checks to protect against input "1 a" and to spot OoBE errors sooner)
 * - [Critical Bug] Program hangs halfway through a game [FIXED]
 * - pass random through params to save reasources [NOT DONE]
 * - 
 */
package battleship;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Michael Swanson
 */
public class Battleship {

    public static void main(String[] args) {
        
        Scanner input = new Scanner(System.in);
        Random rnd = new Random();

        boolean greeting = true;    // these three booleans are the params for the message method
        boolean placementInstructions = false;
        boolean gameplayInstructions = false;
        message(greeting, placementInstructions, gameplayInstructions);
        greeting = false;

        char mapUserShips[][] = new char[10][10];                   // generate gameboards
        char mapUserStrike[][] = new char[10][10];
        char mapComp[][] = new char[10][10];

        placementInstructions = true;
        message(greeting, placementInstructions, gameplayInstructions);
        placementInstructions = false;

        populateWithWater(mapUserShips, mapUserStrike, mapComp);    // fills board with water
        showMaps(mapUserShips, false);
        populateWithShips(mapUserShips, input);                     // user's ship placement
        populateWithShipsComp(mapComp, rnd);                             // computer's ship placement

        gameplayInstructions = true;
        message(greeting, placementInstructions, gameplayInstructions);

        int userHit = 0;            // a bunch of variables for win condition calculations
        int compHit = 0;
        boolean didUserHit;
        boolean didCompHit;
        do {                                                        // gameplay continues until someone wins
            didUserHit = userStrike(mapComp, mapUserShips, mapUserStrike, input);
            didCompHit = compStrike(mapUserShips, rnd);
            if (didUserHit) {                                       // takes return boolean and adds one to score based on return value
                userHit++;
                didUserHit = false; // resets value
            } else if (didCompHit) {
                compHit++;
                didCompHit = false;
            }
            System.out.println("Hits required for victory: " + (17 - userHit) + "\n");
        } while (userHit < 17 && compHit < 17);                      // *Note: the biggest gamebreaking bug in this program was "||" vs "&&"
        if (userHit == 17) {                                         // win condition
            System.out.println("All enemy ships have been sunk. You won!");
        } else if (compHit == 17) {
            System.out.println("All of your ships have sunk. Game over.");
        }
    }

    public static void message(boolean greeting, boolean placementInstructions, boolean gameplayInstructions) {

        if (greeting) {
            System.out.println("Welcome to Battleship!");
        } else if (placementInstructions) {
            System.out.println("\n\nTo place a ship, enter a coordinate, followed by a direction.\n"
                    + "You will not be able to palce a ship that:\n"
                    + "1. Goes out of bounds. [Input must be within a 10x10 coordinate plane: \"X 1-10\", \"Y 1-10\"]\n"
                    + "2. Collides with any other ships.\n\n");
        } else if (gameplayInstructions) {
            System.out.println("\n\nThe battle has now started!\nThe game is over when one side has lost of of their ships.\n"
                    + "To attack, enter a coordinate, if an enemy ship is present, it's a hit!\n"
                    + "A ship is sunk once all of it's occupying coordinates have been hit.\n\n");
        }
    }

    public static void populateWithWater(char[][] mapUserShips, char[][] mapUserStrike, char[][] mapComp) {

        for (char[] row : mapUserShips) {                                       // *Note: is there a way to do this in less code?
            Arrays.fill(row, '~');
        }
        for (char[] row : mapUserStrike) {
            Arrays.fill(row, '~');
        }
        for (char[] row : mapComp) {
            Arrays.fill(row, '~');
            // System.out.println(Arrays.toString(row));                        // *Note: remove "//" to print array for debug
        }
    }

    public static void populateWithShips(char[][] map, Scanner input) {

        String ship = "";                                                       // string to determine which ship we are about to place
        String shipDirection = "";                                              // determine direction of ship placement
        boolean placingShips = true;
        // hella variables
        int i = 0;              // general purpose counter *Note: totally used everywhere, probably bad convention but idgaf
        // Determines what ship user is placing
        int numberofShips = 5;
        int currentShip;

        // splash message w/ instructions
        System.out.println("\nYou have 5 Ships, 1 Carrier [5], 1 Battleship [4], 2 Cruisers [3], and 1 Destroyer [2].");
        System.out.println("To place ships, enter a coordinate [x y], and then  enter a direction.\n\n");

        while (placingShips) {                  // loops through every ship that needs to be placed
            if (numberofShips == 2) {
                currentShip = 3;                // Most ships have (length = order number) exxcept for these
            } else if (numberofShips == 1) {    // which have lengths that are different from their order.
                currentShip = 2;
            } else {
                currentShip = numberofShips;
            }

            // converts int "currentShip" to string "ship"
            // this is to help the user know what ship they are placing
            switch (numberofShips) {
                case 1:
                    ship = "destroyer";
                    break;
                case 2:
                    ship = "submarine";
                    break;
                case 3:
                    ship = "cruiser";
                    break;
                case 4:
                    ship = "battleship";
                    break;
                case 5:
                    ship = "carrier";
                    break;
            }

            // Determines where the ship is
            int userInputX = 0;     // index variable for row
            int userInputY = 0;     // index variable for column
            boolean noError = true;

            do {                            // case specific check **
                System.out.print("Enter coordinate for " + ship + ": ");            // tells user which ship they are placing
                if (input.hasNextInt()) {   // data type check *
                    userInputX = input.nextInt();   // row
                    userInputY = input.nextInt();   // column
                    userInputX--;
                    userInputY--;                   // off by one rule
                    if (userInputX >= 0 && userInputX <= 9 && userInputY >= 0 && userInputY <= 9) {
                        if (map[userInputX][userInputY] == '~') {
                            noError = false;
                        } else {
                            System.out.println("That location is already occupied.\n");
                            input.nextLine();
                        }
                    } else if (userInputX < 0 || userInputX > 9 || userInputY < 0 || userInputY > 9) {
                        System.out.println("That location is out of bounds.\n");
                        input.nextLine();
                    }
                } else {                    // *
                    System.out.println("Invalid entry, coordinates must be entered as a form of 'x y'.\n");
                    input.next();
                }
            } while (noError);                // **
            boolean collision;
            do {
                collision = false;                                      // *note: loop logic backwards from ship coordinate placement
                System.out.print("Enter direction: ");  // where the ship is facing
                if (input.hasNext()) {
                    shipDirection = input.next();
                }
                if ("right".equals(shipDirection) && (userInputY + currentShip) < 10) {    // if else/ else if series
                    for (i = 0; i < currentShip; i++) {                         // loop checks ship length to scan for possible collisions
                        if (map[userInputX][userInputY] == 'O') {
                            collision = true;   // signals an error
                        } else {
                            userInputY++;       // if no error, loop will continue to check
                        }
                    }
                    if (!(collision)) {
                        userInputY -= currentShip;                              // input coordinate was increased during scan, subtracts increased ammount
                        for (i = 0; i < currentShip; i++) {                     // loop adds ship lengths amount of space in the direction facing to be true
                            map[userInputX][userInputY] = 'O';
                            userInputY++;
                        }
                    }
                } else if ("left".equals(shipDirection) && (userInputY - currentShip) >= 0) {
                    for (i = 0; i < currentShip; i++) {                         // loop checks ship length to scan for possible collisions
                        if (map[userInputX][userInputY] == 'O') {
                            collision = true;
                        } else {
                            userInputY--;
                        }
                    }
                    if (!(collision)) {
                        userInputY += currentShip;
                        for (i = 0; i < currentShip; i++) {                     // loop adds ship lengths amount of space in the direction facing to be true
                            map[userInputX][userInputY] = 'O';
                            userInputY--;
                        }
                    }
                } else if ("up".equals(shipDirection) && (userInputX - currentShip) >= 0) {
                    for (i = 0; i < currentShip; i++) {                         // loop checks ship length to scan for possible collisions
                        if (map[userInputX][userInputY] == 'O') {
                            collision = true;
                        } else {
                            userInputX--;
                        }
                    }
                    if (!(collision)) {
                        userInputX += currentShip;
                        for (i = 0; i < currentShip; i++) {                     // loop adds ship lengths amount of space in the direction facing to be true
                            map[userInputX][userInputY] = 'O';
                            userInputX--;
                        }
                    }
                } else if ("down".equals(shipDirection) && (userInputX + currentShip) < 10) {
                    for (i = 0; i < currentShip; i++) {                         // loop checks ship length to scan for possible collisions
                        if (map[userInputX][userInputY] == 'O') {
                            collision = true;
                        } else {
                            userInputX++;
                        }
                    }
                    if (!(collision)) {
                        userInputX -= currentShip;
                        for (i = 0; i < currentShip; i++) {                     // loop adds ship lengths amount of space in the direction facing to be true
                            map[userInputX][userInputY] = 'O';
                            userInputX++;
                        }
                    }
                } else {
                    System.out.println("The length of a ship cannot exceed the 10x10 map.\n"
                            + "Acceptable directional inputs are 'up', 'down', 'left', or 'right'.\n");
                    input.nextLine();
                    numberofShips++;    // quick fix for a bug that skips the current ship after an error
                }
                if (collision) {
                    System.out.println("That location is already occupied.\n");
                    input.nextLine();
                    break;
                } else if (--numberofShips == 0) {                              // ends once user has placed all ships
                    placingShips = false;
                }
            } while (collision);
            showMaps(map, false);
        }
    }

    public static void populateWithShipsComp(char[][] map, Random rnd) {

        int shipDirection;      // determine direction of ship placement
        boolean placingShips = true;
        // misc variables
        int i = 0;                      // misc counter variable
        // SHIP PLACEMENT
        // Determines what ship user is placing
        int numberofShips = 5;
        int currentShip;

        while (placingShips) {
            if (numberofShips == 2) {
                currentShip = 3;
            } else if (numberofShips == 1) {
                currentShip = 2;
            } else {
                currentShip = numberofShips;
            }

            // Determines where the ship is
            int userInputX = 0;         // index variable for row
            int userInputY = 0;         // index variable for column
            boolean checkpointA = true;
            boolean collision = false;

            while (checkpointA) {

                userInputX = rnd.nextInt(10);   // row
                userInputY = rnd.nextInt(10);   // column
                if (map[userInputX][userInputY] == '~') {
                    checkpointA = false;
                }
            }
            collision = true;
            while (collision) {
                collision = false;
                shipDirection = rnd.nextInt(4);
                if (shipDirection == 0 && (userInputY + currentShip) < 10) {                                                     // if else/ else if series
                    for (i = 0; i < currentShip; i++) {                         // loop checks ship length to scan for possible collisions
                        if (map[userInputX][userInputY] == 'O') {
                            collision = true;   // signals an error
                        } else {
                            userInputY++;       // if no error, loop will continue to check
                        }
                    }
                    if (!(collision)) {
                        userInputY -= currentShip;                              // input coordinate was increased during scan, subtracts increased ammount
                        for (i = 0; i < currentShip; i++) {                     // loop adds ship lengths amount of space in the direction facing to be true
                            map[userInputX][userInputY] = 'O';
                            userInputY++;
                        }
                    }
                } else if (shipDirection == 1 && (userInputY - currentShip) >= 0) {
                    for (i = 0; i < currentShip; i++) {                         // loop checks ship length to scan for possible collisions
                        if (map[userInputX][userInputY] == 'O') {
                            collision = true;
                        } else {
                            userInputY--;
                        }
                    }
                    if (!(collision)) {
                        userInputY += currentShip;
                        for (i = 0; i < currentShip; i++) {                     // loop adds ship lengths amount of space in the direction facing to be true
                            map[userInputX][userInputY] = 'O';
                            userInputY--;
                        }
                    }
                } else if (shipDirection == 2 && (userInputX - currentShip) >= 0) {
                    for (i = 0; i < currentShip; i++) {                         // loop checks ship length to scan for possible collisions
                        if (map[userInputX][userInputY] == 'O') {
                            collision = true;
                        } else {
                            userInputX--;
                        }
                    }
                    if (!(collision)) {
                        userInputX += currentShip;
                        for (i = 0; i < currentShip; i++) {                     // loop adds ship lengths amount of space in the direction facing to be true
                            map[userInputX][userInputY] = 'O';
                            userInputX--;
                        }
                    }
                } else if (shipDirection == 3 && (userInputX + currentShip) < 10) {
                    for (i = 0; i < currentShip; i++) {                         // loop checks ship length to scan for possible collisions
                        if (map[userInputX][userInputY] == 'O') {
                            collision = true;
                        } else {
                            userInputX++;
                        }
                    }
                    if (!(collision)) {
                        userInputX -= currentShip;
                        for (i = 0; i < currentShip; i++) {                     // loop adds ship lengths amount of space in the direction facing to be true
                            map[userInputX][userInputY] = 'O';
                            userInputX++;
                        }
                    }
                } else {
                    break;  // break statement if there's a collision or if there's an out of bounds exception
                }
                if (collision) {
                    break;
                } else if (--numberofShips == 0) {                              // ends once comp has placed all ships
                    placingShips = false;
                }
            }
        }
    }

    public static boolean userStrike(char[][] map, char[][] mapUserShips, char[][] mapUserStrike, Scanner input) {

        showMaps(mapUserStrike, true);  // displays gameboard
        showMaps(mapUserShips, false);

        int inputY;
        int inputX;
        // *Note: much of this program was learning 1000 ways to write a scanner input
        System.out.print("\n\nEnter a coordinate: ");
        boolean notClear;           // loop that prevents cpu from selecting an invalid coordinate
        boolean userHit = false;    // return value tracking hits
        do {
            notClear = false;
            if (input.hasNextInt()) {
                inputX = input.nextInt();
                inputY = input.nextInt();
                inputX--;
                inputY--;                                       // off by one rule
                if (inputX < 0 || inputX > 9 || inputY < 0 || inputY > 9) {
                    System.out.println("That location is out of bounds.\n");
                    notClear = true;
                    input.nextLine();
                } else if (map[inputX][inputY] == 'O') {    // conditional clauses
                    map[inputX][inputY] = 'X';
                    System.out.println("A hit!\n");
                    mapUserStrike[inputX][inputY] = 'X';
                    userHit = true;
                } else if (map[inputX][inputY] == 'X' || map[inputX][inputY] == 'M') {
                    System.out.println("You've already fired at this location.\n");
                    notClear = true;
                    input.nextLine();
                } else if (map[inputX][inputY] == '~') {
                    map[inputX][inputY] = 'M';
                    System.out.println("Miss!\n");
                    mapUserStrike[inputX][inputY] = 'M';
                } else {
                    notClear = true;
                    input.nextLine();
                }
            }
        } while (notClear);
        return userHit;
    }

    public static boolean compStrike(char[][] map, Random rnd) {

        boolean notClear;
        boolean compHit = false;
        do {                        // mirrors userStrike w/out scanner input
            notClear = false;
            int inputY = rnd.nextInt(10);
            int inputX = rnd.nextInt(10);
            if (inputX < 0 || inputX > 9 || inputY < 0 || inputY > 9) {
                notClear = true;
            } else if (map[inputX][inputY] == 'O') {
                map[inputX][inputY] = 'X';
                compHit = true;
            } else if (map[inputX][inputY] == 'X' || map[inputX][inputY] == 'M') {
                notClear = true;
            } else if (map[inputX][inputY] == '~') {
                map[inputX][inputY] = 'M';
            } else {
                notClear = true;
            }
        } while (notClear);
        return compHit;
    }

    public static void showMaps(char[][] mapUserStrike, boolean strikeMap) {

        String name = "";       // determines map type
        if (strikeMap) {
            name = "Strike Map";
        } else {
            name = "Your Ships";
        }                       // hella formatting
        System.out.println("    |-----" + name + "------\n    | 1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < mapUserStrike.length; i++) {                 // displays user board
            if (i < 9) {
                System.out.print("  " + (i + 1) + " | ");
            } else {
                System.out.print(" " + (i + 1) + " | ");
            }
            // loop and display sub-arrays.
            char[] sub = mapUserStrike[i];
            for (int x = 0; x < sub.length; x++) {
                System.out.print(sub[x] + " ");
            }
            System.out.println();
        }
    }
}
