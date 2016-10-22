package battleship;
/*

 * Michael Swanson
 * Personal Sideproject: Javaship! ... (Started 10/ 10/ 16)

 For this game I want to create a terminal based recreation of the boardgame
 "Battleship". The game must have:
 * user input [x]
 * user commands/ option menu [x]
 * CPU player
 * option to switch between CPU player and Human Opponent
 * a 10x10 grid map [x]
 * a way to make gameplay more innovative than just guesing points
 * ability to place ships [x]
 * ships of various sizes [x]
 * ship health
 * a UI that informs the player of everything that they need to know
 * fix out of bounds on direction
 * fix bug where doesnt notice conflicting coordinate
 */

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Battleship {

    public static void body(placement[] args) {

    }

    public static void main(String[] args) {
        // option menu variables/ pregame
        int userOption;     // user option inputs
        boolean userCheck = true;       // option menu conditional

        // general gameplay variables
        int turnNumber = 0; // counts turns
        boolean playerTurn = true;      // player turn if true

        boolean victory = false;
        boolean defeat = false;

        // relevent single player game
        boolean condition1 = false;     // single player game

        int cpuChoiceX;     // cpu inputs
        int cpuChoiceY;

        // relevent two player game *not implemented yet
        boolean condition2 = false;

        // misc variables
        int i = 0;          // misc counter variable

        String whoseTurn = "";
        String turn = "Turn " + turnNumber + "! " + whoseTurn + " move.";

        // --- Switches
        if (playerTurn == true) {
            whoseTurn = "Your";
        } else {
            whoseTurn = "CPU's";
        }

        // --- User Input
        System.out.println("Welcome to Battleship!, by Michael Swanson\nThe objective is to seek and destroy your opponent's ships.\n\n\n");    // welcome message
        Scanner input = new Scanner(System.in); // scanner
        while (userCheck) { // locked gate
            System.out.print("Single Player (1)\nTwo Player (2)\nHow to Play (3)\nExit (4)\n\nEnter: ");
            if (input.hasNextInt()) {
                userOption = input.nextInt();   // user selects option by typing corrosponding number
                if (userOption >= 1 && userOption <= 2) {
                    userCheck = false;      // unlocks gate
                    if (userOption == 1) {
                        condition1 = true;  // starts game
                    } else if (userOption == 2) {
                        condition2 = true;
                    }
                } else if (userOption == 3) {       // prints game instructions
                    System.out.println("\n\nINSERT INSTRUCTIONS HERE\n\n");
                    input.nextLine();
                } else if (userOption == 4) {       // exits program
                    System.exit(0);
                }
            } else {                        // the "if the user is a potato" clause
                System.out.println("INVALID INPUT: Please enter a whole number.");
                input.nextLine();
            }
        }

        // --- Generate Gameboard
        if (condition1) {                       // single player game

            boolean placingShips = true;

            String ship = "";   // string to determine which ship we are about to place
            String userDirection = "";      // determine direction of ship placement

            // MAP GENERATION
            boolean[][] mapUser = new boolean[10][10];  // user's gameboard
            for (boolean[] row : mapUser) {
                Arrays.fill(row, false);
                // System.out.println(Arrays.toString(row));
            }

            // SHIP PLACEMENT
            // Determines what ship user is placing
            int numberofShips = 5;
            int currentShip;

            // splash message w/ instructions
            System.out.println("\nYou have 5 Ships, 1 Carrier [5], 1 Battleship [4], 2 Cruisers [3], and 1 Destroyer [2].");
            System.out.println("To place ships, enter a coordinate [x y], and then  enter a direction.\n\n");

            while (placingShips) {
                if (numberofShips == 2) {
                    currentShip = 3;
                } else if (numberofShips == 1) {
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
                boolean checkpointA = true;
                boolean collision = false;

                while (checkpointA) {

                    System.out.print("Enter coordinate for " + ship + ": ");    // tells user which ship they are placing
                    if (input.hasNextInt()) {   // user inputs value of "x y"
                        userInputX = input.nextInt();   // row
                        userInputY = input.nextInt();   // column
                        if (userInputX >= 0 && userInputX <= 9 && userInputY >= 0 && userInputY <= 9) {
                            if (mapUser[userInputX][userInputY] == false) {
                                checkpointA = false;
                            } else {
                                System.out.println("That location is already occupied.\n");
                                input.nextLine();
                            }
                        } else if (userInputX < 0 || userInputX > 9 || userInputY < 0 || userInputY > 9) {
                            System.out.println("That location is out of bounds.\n");
                            input.nextLine();
                        }
                    } else {
                        System.out.println("Invalid entry, coordinates must be entered as a form of 'x y'.\n");
                        input.nextLine();
                    }
                }
                collision = true;
                while (collision) {
                    collision = false;
                    System.out.print("Enter direction: ");                  // where the ship is facing
                    if (input.hasNext()) {
                        userDirection = input.next();
                    }
                    if ("up".equals(userDirection)) {                   // if else/ else if series
                        for (i = 0; i < currentShip; i++) {                 // loop checks ship length to scan for possible collisions
                            if (userInputX >= 0 || userInputX <= 10 || userInputY >= 0 || userInputY <= 10) {
                                if (mapUser[userInputX][userInputY] == true) {
                                    collision = true;       // signals an error
                                } else {
                                    userInputY++;           // if no error, loop will continue to check
                                }
                            }
                        }
                        if (!(collision)) {
                            userInputY -= currentShip;      // input coordinate was increased during scan, subtracts increased ammount
                            for (i = 0; i < currentShip; i++) {                 // loop adds ship lengths amount of space in the direction facing to be true
                                if (userInputX >= 0 || userInputX <= 10 || userInputY >= 0 || userInputY <= 10) {
                                    mapUser[userInputX][userInputY] = true;
                                    userInputY++;
                                }
                            }
                        }
                    } else if ("down".equals(userDirection)) {
                        for (i = 0; i < currentShip; i++) {                 // loop checks ship length to scan for possible collisions
                            if (mapUser[userInputX][userInputY] == true) {
                                collision = true;
                            } else {
                                userInputY--;
                            }
                        }
                        if (!(collision)) {
                            userInputY += currentShip;
                            for (i = 0; i < currentShip; i++) {                 // loop adds ship lengths amount of space in the direction facing to be true
                                mapUser[userInputX][userInputY] = true;
                                userInputY--;
                            }
                        }
                    } else if ("left".equals(userDirection)) {
                        for (i = 0; i < currentShip; i++) {                 // loop checks ship length to scan for possible collisions
                            if (mapUser[userInputX][userInputY] == true) {
                                collision = true;
                            } else {
                                userInputX--;
                            }
                        }
                        if (!(collision)) {
                            userInputX += currentShip;
                            for (i = 0; i < currentShip; i++) {                 // loop adds ship lengths amount of space in the direction facing to be true
                                mapUser[userInputX][userInputY] = true;
                                userInputX--;
                            }
                        }
                    } else if ("right".equals(userDirection)) {
                        for (i = 0; i < currentShip; i++) {                 // loop checks ship length to scan for possible collisions
                            if (mapUser[userInputX][userInputY] == true) {
                                collision = true;
                            } else {
                                userInputX++;
                            }
                        }
                        if (!(collision)) {
                            userInputX -= currentShip;
                            for (i = 0; i < currentShip; i++) {                 // loop adds ship lengths amount of space in the direction facing to be true
                                mapUser[userInputX][userInputY] = true;
                                userInputX++;
                            }
                        }
                    } else {
                        System.out.println("Acceptable inputs are 'up', 'down', 'left', or 'right'.");
                        input.nextLine();
                    }
                    if (collision) {
                        System.out.println("That location is already occupied.\n");
                        input.nextLine();
                        mapUser[userInputX][userInputY] = false;
                        break;
                    } else if (--numberofShips == 0) {             // ends once user has placed all ships
                        placingShips = false;
                    }
                }
            }

        }
    }
}
