/*
 * Battleship! a board game in java.
 * Goal: To use what I learned from CSC 210 to create the board game "Battleship" as a java program.
 * To do:
 * - add display to aid ship placement [DONE]
 * - label axis on display [DONE]
 * - optimize user-input-checks [MOSTLY FIXED] *(Need to add checks to protect against input "1 a" and to spot OoBE errors sooner)
 * - [Critical Bug] Program hangs halfway through a game [FIXED]
 * - pass random through params to save reasources [DONE]
 * - check legallity
 * - **optimize: split into more methods
 * - **optimize: remove unnessisary params
 * - fix placement bugs [FIXED]
 * - add x-y axis indicators
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

        greetingMessage();
        placementInstructions();

        char mapUserShips[][] = new char[10][10];                               // generate gameboards
        char mapUserStrike[][] = new char[10][10];
        char mapComp[][] = new char[10][10];

        populateWithWater(mapUserShips);                                        // fills board with water
        populateWithWater(mapUserStrike);
        populateWithWater(mapComp);

        showMaps(mapUserShips, false);                                          // ****Note: remove boolean param
        populateWithShipsUser(mapUserShips, input);                             // user's ship placement
        populateWithShipsComp(mapComp, rnd);                                    // computer's ship placement

        gameplayInstructions();

        int userHit = 0;                                                        // a bunch of variables for win condition calculations
        int compHit = 0;
        boolean didUserHit;
        boolean didCompHit;
        do {                                                                    // gameplay continues until someone wins
            didUserHit = userStrike(mapComp, mapUserShips, mapUserStrike, input);
            didCompHit = compStrike(mapUserShips, rnd);
            if (didUserHit) {                                                   // takes return boolean and adds one to score based on return value
                userHit++;
                didUserHit = false;                                             // resets value
            } else if (didCompHit) {
                compHit++;
                didCompHit = false;
            }
            System.out.println("Hits until user victory: " + (17 - userHit));
            System.out.println("Hits until CPU  victory: " + (17 - compHit) + "\n");

        } while (userHit < 17 && compHit < 17);                                 // *Note: the biggest gamebreaking bug in this program was "||" vs "&&"
        if (userHit == 17) {                                                    // win condition
            System.out.println("All enemy ships have been sunk. You won!");
        } else if (compHit == 17) {
            System.out.println("All of your ships have sunk. Game over.");
        }
    }

    public static void greetingMessage() {

        System.out.println("Welcome to Battleship!");
    }

    public static void placementInstructions() {

        System.out.println("\n\nTo place a ship, enter a coordinate [X Y], followed by a direction.\n"
                + "You will not be able to place a ship that:\n"
                + "1. Goes out of bounds. [Input must be within a 10x10 coordinate plane: \"X 1-10\", \"Y 1-10\"]\n"
                + "2. Collides with any other ships.\n\n");
    }

    public static void gameplayInstructions() {

        System.out.println("\n\nThe battle has now started!\nThe game is over when one side has lost of of their ships.\n"
                + "To attack, enter a coordinate, if an enemy ship is present, it's a hit!\n"
                + "A ship is sunk once all of it's occupying coordinates have been hit.\n\n");
    }

    public static void populateWithWater(char[][] map) {

        for (char[] row : map) {
            Arrays.fill(row, '~');
        }
    }

    public static void populateWithShipsUser(char[][] map, Scanner input) {     // ****Note: split into methods & combine w/ comp for things that are similar e.i collision

        String ship = "";                                                       // string to determine which ship we are about to place *Note: convert to S.O.P
        String shipDirection = "";                                              // determine direction of ship placement
        boolean placingShips = true;
        int i = 0;                                                              // general purpose for loop counter
        // Determines what ship user is placing
        int numberOfShips = 5;
        int sizeOfCurrentShip;

        // splash message w/ instructions
        System.out.println("\nYou have 5 Ships, 1 Carrier [5], 1 Battleship [4], 1 Cruisers [3], 1 Submarine [3], and 1 Destroyer [2].");
        System.out.println("To place ships, enter a coordinate [x y], and then enter a direction.\n\n");

        while (placingShips) {                                                  // loops through every ship that needs to be placed
            if (numberOfShips == 2) {
                sizeOfCurrentShip = 3;                                          // Most ships have (length = order number) except for these
            } else if (numberOfShips == 1) {                                    // which have lengths that are different from their order.
                sizeOfCurrentShip = 2;
            } else {
                sizeOfCurrentShip = numberOfShips;
            }

            // converts int "currentShip" to string "ship"
            // this is to help the user know what ship they are placing
            switch (numberOfShips) {
                case 1:
                    ship = "destroyer [2]";
                    break;
                case 2:
                    ship = "submarine [3]";
                    break;
                case 3:
                    ship = "cruiser [3]";
                    break;
                case 4:
                    ship = "battleship [4]";
                    break;
                case 5:
                    ship = "carrier [5]";
                    break;
            }

            // Determines where the ship is
            int userInputX = 0;                                                 // index variable for row
            int userInputY = 0;                                                 // index variable for column
            boolean noError = true;

            do {                                                                // case specific check **
                System.out.print("Enter coordinate for " + ship + ": ");        // tells user which ship they are placing

                if (input.hasNextInt()) {                                       // data type check *
                    userInputX = input.nextInt();                               // row
                    if (input.hasNextInt()) {
                        userInputY = input.nextInt();                           // column

                        userInputX--;
                        userInputY--;                                           // off by one rule
                        if (userInputX >= 0 && userInputX <= 9 && userInputY >= 0 && userInputY <= 9) {
                            if (map[userInputX][userInputY] == '~') {
                                noError = false;
                            } else {
                                System.out.println("That location is already occupied.\n");
                                input.nextLine();
                            }
                        } else {
                            System.out.println("That location is out of bounds.\n");
                            input.nextLine();
                        }
                    } else {
                        System.out.println("Invalid entry, coordinates must be entered as a form of 'x y'.\n");
                        input.nextLine();
                    }
                } else {                                                        // *
                    System.out.println("Invalid entry, coordinates must be entered as a form of 'x y'.\n");
                    input.nextLine();
                }
            } while (noError);                                                  // **

            boolean collision;
            do {
                int tempY = userInputY;
                int tempX = userInputX;
                collision = false;                                              // *note: loop logic backwards from ship coordinate placement
                System.out.print("Enter direction [up, down, left, right]: ");  // where the ship is facing
                if (input.hasNext()) {
                    shipDirection = input.next();

                    if ("right".equals(shipDirection) && (userInputY + sizeOfCurrentShip) < 9) {          // if else/ else if series
                        for (i = 0; i < sizeOfCurrentShip; i++) {               // loop checks ship length to scan for possible collisions
                            if (map[userInputX][tempY] == 'O') {
                                collision = true;                               // signals an error
                                numberOfShips++;
                                break;
                            }
                            tempY++;                                            // if no error, loop will continue to check
                        }
                        if (!(collision)) {
                            tempY = userInputY;
                            for (i = 0; i < sizeOfCurrentShip; i++) {           // loop adds ship lengths amount of space in the direction facing to be true
                                map[userInputX][tempY] = 'O';
                                tempY++;
                            }
                        }
                    } else if ("left".equals(shipDirection) && (userInputY - sizeOfCurrentShip) >= -1) {  // *Note: I don't know why -1 works
                        for (i = 0; i < sizeOfCurrentShip; i++) {               // loop checks ship length to scan for possible collisions
                            if (map[userInputX][tempY] == 'O') {
                                collision = true;
                                numberOfShips++;
                                break;
                            }
                            tempY--;
                        }
                        if (!(collision)) {
                            tempY = userInputY;
                            for (i = 0; i < sizeOfCurrentShip; i++) {           // loop adds ship lengths amount of space in the direction facing to be true
                                map[userInputX][tempY] = 'O';
                                tempY--;
                            }
                        }
                    } else if ("up".equals(shipDirection) && (userInputX - sizeOfCurrentShip) >= -1) {
                        for (i = 0; i < sizeOfCurrentShip; i++) {               // loop checks ship length to scan for possible collisions
                            if (map[tempX][userInputY] == 'O') {
                                collision = true;
                                numberOfShips++;
                                break;
                            }
                            tempX--;
                        }
                        if (!(collision)) {
                            tempX = userInputX;
                            userInputX += sizeOfCurrentShip;
                            for (i = 0; i < sizeOfCurrentShip; i++) {           // loop adds ship lengths amount of space in the direction facing to be true
                                map[tempX][userInputY] = 'O';
                                tempX--;
                            }
                        }
                    } else if ("down".equals(shipDirection) && (userInputX + sizeOfCurrentShip) < 9) {
                        for (i = 0; i < sizeOfCurrentShip; i++) {               // loop checks ship length to scan for possible collisions
                            if (map[tempX][userInputY] == 'O') {
                                collision = true;
                                numberOfShips++;
                                break;
                            }
                            tempX++;
                        }
                        if (!(collision)) {
                            tempX = userInputX;
                            userInputX -= sizeOfCurrentShip;
                            for (i = 0; i < sizeOfCurrentShip; i++) {           // loop adds ship lengths amount of space in the direction facing to be true
                                map[tempX][userInputY] = 'O';
                                tempX++;
                            }
                        }
                    } else {
                        System.out.println("Error!\n"
                                + "Either you entered an invalid direction, or a part of the ship is out of bounds.\n"
                                + "Acceptable directional inputs are 'up', 'down', 'left', or 'right'.\n"
                                + "Coordinates are typed in the form of 'Row# Column#'.\n");
                        numberOfShips++;
                        input.nextLine();
                    }
                } else {
                    System.out.println("Error!\n"
                            + "Acceptable directional inputs are 'up', 'down', 'left', or 'right'.\n");
                    numberOfShips++;
                    input.nextLine();
                }

                if (collision) {
                    System.out.println("Error!\n"
                            + "That location is already occupied.\n");
                    input.nextLine();
                }
            } while (collision);

            numberOfShips--;
            if (numberOfShips == 0) {                                           // ends once user has placed all ships
                placingShips = false;
            }

            showMaps(map, false);
        }
    }                                                                           // *NOTE: loop back to coordinates if bad input

    public static void populateWithShipsComp(char[][] map, Random rnd) {

        int shipDirection;                                                      // determine direction of ship placement
        boolean placingShips = true;
        // misc variables
        int i = 0;                                                              // misc counter variable
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
            int userInputX = 0;                                                 // index variable for row
            int userInputY = 0;                                                 // index variable for column
            boolean checkpointA = true;
            boolean collision = false;

            while (checkpointA) {

                userInputX = rnd.nextInt(10);                                   // row
                userInputY = rnd.nextInt(10);                                   // column
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
                            collision = true;                                   // signals an error
                        } else {
                            userInputY++;                                       // if no error, loop will continue to check
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
                    break;                                                      // break statement if there's a collision or if there's an out of bounds exception
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
        boolean notClear;                                                       // loop that prevents cpu from selecting an invalid coordinate
        boolean userHit = false;                                                // return value tracking hits
        do {
            notClear = false;
            if (input.hasNextInt()) {
                inputX = input.nextInt();
                inputY = input.nextInt();
                inputX--;
                inputY--;                                                       // off by one rule
                if (inputX < 0 || inputX > 9 || inputY < 0 || inputY > 9) {
                    System.out.println("That location is out of bounds.\n");
                    notClear = true;
                    input.nextLine();
                } else if (map[inputX][inputY] == 'O') {                        // conditional clauses
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
        do {                                                                    // mirrors userStrike w/out scanner input
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

        if (strikeMap) {
            System.out.println("    |-----Strike Map------\n    | 1 2 3 4 5 6 7 8 9 10");
        } else {
            System.out.println("    |-----Your Map------\n    | 1 2 3 4 5 6 7 8 9 10");
        }
        for (int i = 0; i < mapUserStrike.length; i++) {                        // displays user board
            if (i < 9) {
                System.out.print("  " + (i + 1) + " | ");                       // ****Note: Implement printf
            } else {
                System.out.print(" " + (i + 1) + " | ");
            }
            // loop and display sub-arrays
            for (int j = 0; j < mapUserStrike[i].length; j++) {
                System.out.print(mapUserStrike[i][j] + " ");
            }
            System.out.println();
        }
    }
}
