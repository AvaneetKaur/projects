/**
 * Name: Avaneet Kaur
 * Email: avkaur@ucsd.edu
 * PID: A17601239
 * Sources used: tutor: Tim Jiang
 *
 * This file demonstrates 1D arrays by implementing
 * a simple airline reservation system that allows the
 * user to book, cancel, and upgrade tickets. They can
 * also look up a passenger's seat number, print the number
 * of available tickets in each travel class, and print a view
 * of the plane with passenger names.
 */

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
public class AirlineReservation {
    /* Delimiters and Formatters */
    private static final String CSV_DELIMITER = ",";
    private static final String COMMAND_DELIMITER = " ";
    private static final String PLANE_FORMAT = "%d\t | %s | %s \n";

    /* Travel Classes */
    private static final int FIRST_CLASS = 0;
    private static final int BUSINESS_CLASS = 1;
    private static final int ECONOMY_CLASS = 2;
    private static final String[] CLASS_LIST = new String[] {"F", "B", "E"};
    private static final String[] CLASS_FULLNAME_LIST = new String[] {
        "First Class", "Business Class", "Economy Class"};

    /* Commands */
    private static final String[] COMMANDS_LIST = new String[] { "book", 
        "cancel", "lookup", "availabletickets", "upgrade", "print","exit"};
    private static final int BOOK_IDX = 0;
    private static final int CANCEL_IDX = 1;
    private static final int LOOKUP_IDX = 2;
    private static final int AVAI_TICKETS_IDX = 3;
    private static final int UPGRADE_IDX = 4;
    private static final int PRINT_IDX = 5;
    private static final int EXIT_IDX = 6;
    private static final int BOOK_UPGRADE_NUM_ARGS = 3;
    private static final int CANCEL_LOOKUP_NUM_ARGS = 2;

    /* Strings for main */
    private static final String USAGE_HELP =
            "Available commands:\n" +
            "- book <travelClass(F/B/E)> <passengerName>\n" +
            "- book <rowNumber> <passengerName>\n" +
            "- cancel <passengerName>\n" +
            "- lookup <passengerName>\n" +
            "- availabletickets\n" +
            "- upgrade <travelClass(F/B)> <passengerName>\n" +
            "- print\n" +
            "- exit";
    private static final String CMD_INDICATOR = "> ";
    private static final String INVALID_COMMAND = "Invalid command.";
    private static final String INVALID_ARGS = "Invalid number of arguments.";
    private static final String INVALID_ROW = 
        "Invalid row number %d, failed to book.\n";
    private static final String DUPLICATE_BOOK =
        "Passenger %s already has a booking and cannot book multiple seats.\n";
    private static final String BOOK_SUCCESS = 
        "Booked passenger %s successfully.\n";
    private static final String BOOK_FAIL = "Could not book passenger %s.\n";
    private static final String CANCEL_SUCCESS = 
        "Canceled passenger %s's booking successfully.\n";
    private static final String CANCEL_FAIL = 
        "Could not cancel passenger %s's booking, do they have a ticket?\n";
    private static final String UPGRADE_SUCCESS = 
        "Upgraded passenger %s to %s successfully.\n";
    private static final String UPGRADE_FAIL = 
        "Could not upgrade passenger %s to %s.\n";
    private static final String LOOKUP_SUCCESS = 
            "Passenger %s is in row %d.\n";
    private static final String LOOKUP_FAIL = "Could not find passenger %s.\n";
    private static final String AVAILABLE_TICKETS_FORMAT = "%s: %d\n";
    
    /* Static variables - DO NOT add any additional static variables */
    static String [] passengers;
    static int planeRows;
    static int firstClassRows;
    static int businessClassRows;

    /**
     * Runs the command-line interface for our Airline Reservation System.
     * Prompts user to enter commands, which correspond to different functions.
     * @param args args[0] contains the filename to the csv input
     * @throws FileNotFoundException if the filename args[0] is not found
     */
    public static void main (String[] args) throws FileNotFoundException {
        //If there are an incorrect num of args, print error message and quit
        if (args.length != 1) {
            System.out.println(INVALID_ARGS);
            return;
        }
        initPassengers(args[0]); // Populate passengers based on csv input file
        System.out.println(USAGE_HELP);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(CMD_INDICATOR);
            String line = scanner.nextLine().trim();

            // Exit
            if (line.toLowerCase().equals(COMMANDS_LIST[EXIT_IDX])) {
                scanner.close();
                return;
            }

            String[] splitLine = line.split(COMMAND_DELIMITER);
            splitLine[0] = splitLine[0].toLowerCase();

            // Check for invalid commands
            boolean validFlag = false;
            for (int i = 0; i < COMMANDS_LIST.length; i++) {
                if (splitLine[0].toLowerCase().equals(COMMANDS_LIST[i])) {
                    validFlag = true;
                }
            }
            if (!validFlag) {
                System.out.println(INVALID_COMMAND);
                continue;
            }

            // Book
            if (splitLine[0].equals(COMMANDS_LIST[BOOK_IDX])) {
                if (splitLine.length < BOOK_UPGRADE_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER,
                        BOOK_UPGRADE_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                try {
                    // book row <passengerName>
                    int row = Integer.parseInt(contents[1]);
                    if (row < 0 || row >= passengers.length) {
                        System.out.printf(INVALID_ROW, row);
                        continue;
                    }
                    // Do not allow duplicate booking
                    boolean isDuplicate = false;
                    for (int i = 0; i < passengers.length; i++) {
                        if (passengerName.equals(passengers[i])) {
                            isDuplicate = true;
                        }
                    }
                    if (isDuplicate) {
                        System.out.printf(DUPLICATE_BOOK, passengerName);
                        continue;
                    }
                    if (book(row, passengerName)) {
                        System.out.printf(BOOK_SUCCESS, passengerName);
                    } else {
                        System.out.printf(BOOK_FAIL, passengerName);
                    }
                } catch (NumberFormatException e) {
                    // book <travelClass(F/B/E)> <passengerName>
                    validFlag = false;
                    contents[1] = contents[1].toUpperCase();
                    for (int i = 0; i < CLASS_LIST.length; i++) {
                        if (CLASS_LIST[i].equals(contents[1])) {
                            validFlag = true;
                        }
                    }
                    if (!validFlag) {
                        System.out.println(INVALID_COMMAND);
                        continue;
                    }
                    // Do not allow duplicate booking
                    boolean isDuplicate = false;
                    for (int i = 0; i < passengers.length; i++) {
                        if (passengerName.equals(passengers[i])) {
                            isDuplicate = true;
                        }
                    }
                    if (isDuplicate) {
                        System.out.printf(DUPLICATE_BOOK, passengerName);
                        continue;
                    }
                    int travelClass = FIRST_CLASS;
                    if (contents[1].equals(CLASS_LIST[BUSINESS_CLASS])) {
                        travelClass = BUSINESS_CLASS;
                    } else if (contents[1].equals(
                                CLASS_LIST[ECONOMY_CLASS])) {
                        travelClass = ECONOMY_CLASS;
                    }
                    if (book(passengerName, travelClass)) {
                        System.out.printf(BOOK_SUCCESS, passengerName);
                    } else {
                        System.out.printf(BOOK_FAIL, passengerName);
                    }
                }
            }

            // Upgrade
            if (splitLine[0].equals(COMMANDS_LIST[UPGRADE_IDX])) {
                if (splitLine.length < BOOK_UPGRADE_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER,
                        BOOK_UPGRADE_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                validFlag = false;
                contents[1] = contents[1].toUpperCase();
                for (int i = 0; i < CLASS_LIST.length; i++) {
                    if (CLASS_LIST[i].equals(contents[1])) {
                        validFlag = true;
                    }
                }
                if (!validFlag) {
                    System.out.println(INVALID_COMMAND);
                    continue;
                }
                int travelClass = FIRST_CLASS;
                if (contents[1].equals(CLASS_LIST[BUSINESS_CLASS])) {
                    travelClass = BUSINESS_CLASS;
                } else if (contents[1].equals(CLASS_LIST[ECONOMY_CLASS])) {
                    travelClass = ECONOMY_CLASS;
                }
                if (upgrade(passengerName, travelClass)) {
                    System.out.printf(UPGRADE_SUCCESS, passengerName,
                            CLASS_FULLNAME_LIST[travelClass]);
                } else {
                    System.out.printf(UPGRADE_FAIL, passengerName,
                            CLASS_FULLNAME_LIST[travelClass]);
                }
            }

            // Cancel
            if (splitLine[0].equals(COMMANDS_LIST[CANCEL_IDX])) {
                if (splitLine.length < CANCEL_LOOKUP_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER,
                        CANCEL_LOOKUP_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                if (cancel(passengerName)) {
                    System.out.printf(CANCEL_SUCCESS, passengerName);
                } else {
                    System.out.printf(CANCEL_FAIL, passengerName);
                }
            }

            // Lookup
            if (splitLine[0].equals(COMMANDS_LIST[LOOKUP_IDX])) {
                if (splitLine.length < CANCEL_LOOKUP_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER,
                        CANCEL_LOOKUP_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                if (lookUp(passengerName) == -1) {
                    System.out.printf(LOOKUP_FAIL, passengerName);
                } else {
                    System.out.printf(LOOKUP_SUCCESS, passengerName,
                            lookUp(passengerName));
                }
            }

            // Available tickets
            if (splitLine[0].equals(COMMANDS_LIST[AVAI_TICKETS_IDX])) {
                int[] numTickets = availableTickets();
                for (int i = 0; i < CLASS_FULLNAME_LIST.length; i++) {
                    System.out.printf(AVAILABLE_TICKETS_FORMAT,
                            CLASS_FULLNAME_LIST[i], numTickets[i]);
                }
            }

            // Print
            if (splitLine[0].equals(COMMANDS_LIST[PRINT_IDX])) {
                printPlane();
            }
        }
    }

    /**
     * Reads input from a CSV file and initializes the static variables
     * passengers, planeRows, firstClassRows, and businessClassRows.
     * @param fileName contains the filename to the csv input
     * @throws FileNotFoundException if the fileName is not found
     */
    private static void initPassengers(String fileName) throws FileNotFoundException {
        File sourceFile = new File(fileName);
        Scanner input = new Scanner(sourceFile);
        input.useDelimiter(",\\r\\n|,\\n|\\r\\n|\\n|,");
        planeRows = input.nextInt();
        firstClassRows = input.nextInt();
        businessClassRows = input.nextInt();
        passengers = new String[planeRows];
        int index = 0;
        while(input.hasNext()){
            index = input.nextInt();
            passengers[index] = input.next();
        }
    }

    /**
     * Finds the plane class corresponding to the given row number.
     * @param row number
     * @return integer value representing plane class (0, 1, 2)
     * @return -1 if row not found
     */
    private static int findClass(int row) {
        int economyClassRows = firstClassRows + businessClassRows;
        if (row >= 0 && row < firstClassRows) {
            return FIRST_CLASS;
        } else if (row >= firstClassRows && row < economyClassRows) {
            return BUSINESS_CLASS;
        } else if (row >= economyClassRows && row < passengers.length) {
            return ECONOMY_CLASS;
        }
        return -1;
    }

    /**
     * Finds the first row of given plane class.
     * @param travelClass type of plane class
     * @return index of first row of given plane class
     * @return -1 if plane class not found
     */
    private static int findFirstRow(int travelClass) {
        int startIndex = 0;
        if(travelClass == FIRST_CLASS){
            return startIndex;
        } else if (travelClass == BUSINESS_CLASS){
            return firstClassRows;
        } else if(travelClass == ECONOMY_CLASS){
            return (firstClassRows + businessClassRows);
        }
        return -1;
    }

    /**
     * Finds the last row of given plane class.
     * @param travelClass type of plane class
     * @return index of last row of given plane class
     * @return -1 if plane class not found
     */
    private static int findLastRow(int travelClass) {
        int offset = 1;
        if(travelClass == 0){
            return firstClassRows - offset;
        } else if (travelClass == 1){
            return (firstClassRows + businessClassRows) - offset;
        } else if(travelClass == 2){
            return passengers.length - offset;
        }
        return -1;
    }

    /**
     * Assigns given passenger to their desired plane class.
     * @param passengerName name of the passenger to be assigned
     * @param travelClass type of plane class
     * @return true if successfully assigned
     * @return false if not able to assign
     */
    public static boolean book(String passengerName, int travelClass) {
        if(passengerName == null){
            return false;
        }
        int offset = 1;
        int numSeats = 0;
        int seat = findFirstRow(travelClass);
        if(travelClass == FIRST_CLASS)
            numSeats = firstClassRows;
        else if (travelClass == BUSINESS_CLASS)
            numSeats = businessClassRows;
        else if (travelClass == ECONOMY_CLASS)
            numSeats = passengers.length
                    - (firstClassRows + businessClassRows);

        for(int i = 0; i < numSeats; i++) {
            if (passengers[seat + i] == null) {
                passengers[seat + i] = passengerName;
                return true;
            }
        }
        if(travelClass == ECONOMY_CLASS
                && passengers[passengers.length - offset] == null){
            passengers[passengers.length - offset] = passengerName;
            return true;
        }
        return false;
    }

    /**
     * Assigns given passenger to their desired number of row.
     * @param passengerName name of the passenger to be assigned
     * @param row number of plane row
     * @return true if successfully assigned
     * @return false if not able to assign
     */
    public static boolean book(int row, String passengerName) {
        if(passengerName == null)
            return false;
        if(passengers[row] == null){
            passengers[row] = passengerName;
            return true;
        } else{
            int seatType = findClass(row);
            int avaiIndex = findFirstRow(seatType);
            int endIndex = findLastRow(seatType);
            while(passengers[avaiIndex] != null && avaiIndex < endIndex){
                avaiIndex++;
            }
            if(passengers[avaiIndex] == null) {
                passengers[avaiIndex] = passengerName;
                return true;
            }
            return false;
        }
    }

    /**
     * Removes given passenger from the plane reservation.
     * @param passengerName name of the passenger to be removed
     * @return true if successfully removed
     * @return false if not able to remove
     */
    public static boolean cancel(String passengerName){
        if(passengerName == null)
            return false;
        for(int i = 0; i < passengers.length; i++){
            if(passengers[i] != null
                    && passengers[i].equals(passengerName)) {
                passengers[i] = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Searches the reservation for given passenger.
     * @param passengerName name of the passenger
     * @return row number of the passenger's seat
     * @return -1 if not able to find passenger
     */
    public static int lookUp(String passengerName) {
        if(passengerName == null)
            return -1;
        for(int i = 0; i < passengers.length; i++){
            if(passengers[i] != null
                    && passengers[i].equals(passengerName))
                return i;
        }
        return -1;
    }

    /**
     * Counts number of empty seats in each plane class.
     * @return int array of length 3 containing empty seats in each class
     */
    public static int[] availableTickets() {
        int [] counter = {0, 0, 0};
        int i, j, k;
        for(i = 0; i <= findLastRow(FIRST_CLASS); i++){
            if(passengers[i] == null)
                counter[0]++;
        }
        for(j = i; j <= findLastRow(BUSINESS_CLASS); j++){
            if(passengers[j] == null)
                counter[1]++;
        }
        for(k = j; k <= findLastRow(ECONOMY_CLASS); k++){
            if(passengers[k] == null)
                counter[2]++;
        }
        return counter;
    }

    /**
     * Changes passenger's seat reservation to an upper plane class
     * than the one they already have.
     * @param passengerName name of the passenger
     * @param upgradeClass plane class to which passenger wants to upgrage
     * @return true if successfully upgraded
     * @return false if not able to upgrade
     */
    public static boolean upgrade(String passengerName, int upgradeClass) {
        boolean found = false;
        int index = 0;
        int noSeats = 0;
        int [] avaiTickets = availableTickets();
        int avaiSeats = avaiTickets[0]
                + avaiTickets[1] + avaiTickets[2];
        if(passengerName == null)
            return false;
        if(avaiSeats == noSeats)
            return false;
        for(int i = 0; i < passengers.length; i++){
            if(passengers[i] != null
                    && passengers[i].equals(passengerName)) {
                found = true;
                index = i;
                break;
            }
        }
        if(found && findClass(index) > upgradeClass){
            passengers[index] = null;
            return book(passengerName, upgradeClass);
        }
        return false;
    }

    /**
     * Prints out the names of each of the passengers according to their booked
     * seat row. No name is printed for an empty (currently available) seat.
     */
    public static void printPlane() {
        for (int i = 0; i < passengers.length; i++) {
            System.out.printf(PLANE_FORMAT, i, CLASS_LIST[findClass(i)], 
                    passengers[i] == null ? "" : passengers[i]);
        }
    }
}
