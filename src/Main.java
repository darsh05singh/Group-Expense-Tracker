import java.io.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.LocalDate;


public class Main {
    public static void main(String[] args) {

        File tripFile = new File("TripData.ser");
        if (!tripFile.exists()) {
            IO.println("Welcome to the Group Expense Tracker\n");

            String tripName = IO.readln("Enter the trip name: ");
            while (tripName.equals("")) {
                IO.println("Nothing entered, try again");
                tripName = IO.readln("Enter the trip name: ");
            }


            int numMembers = 0;
            while (true) {
                try {
                    numMembers = Integer.parseInt(IO.readln("Please enter the number of members in the trip: "));
                    if (numMembers <= 1) {
                        throw new IllegalArgumentException();
                    }
                    break;

                } catch (NumberFormatException e) {
                    IO.println("Wrong input, please enter a positive whole number greater than one");
                } catch (IllegalArgumentException e) {
                    IO.println("Please enter a positive whole number greater than one");
                }
            }

            Map<Integer, String> memberMap = new HashMap<>();

            for (int i = 1; i <= numMembers; i++) {
                String name = IO.readln("Please enter member " + i + " name: ");
                while (name.equals("")) {
                    IO.println("Nothing entered, try again");
                    name = IO.readln("Please enter member " + i + " name: ");
                }
                memberMap.put(i, name);
            }

            Trip trip = new Trip(tripName, memberMap);


            while (true) {
                IO.println();
                IO.println("Please select one of the following options:\n 1) Add Expense\n 2) Add Payment\n 3) View Balance Summary\n 4) End Trip\n 5) Save and Exit");

                int menuSelection = 0;
                while (true) {
                    try {
                        menuSelection = Integer.parseInt(IO.readln("Enter the integer next to your selection: "));
                        if (menuSelection < 1 || menuSelection > 5) {
                            throw new IllegalArgumentException();
                        }
                        break;
                    } catch (NumberFormatException e) {
                        IO.println("Wrong input, please try again");
                    } catch (IllegalArgumentException e) {
                        IO.println("Please a number between 1 and 5 for your selected option");
                    }
                }

                if (menuSelection == 1) {

                    double expenseAmount = 0;
                    IO.println();
                    while (true) {
                        try {
                            expenseAmount = Double.parseDouble(IO.readln("Enter the expense amount: $"));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Please enter a valid number. Ensure no commas are present.");
                        }

                    }
                    IO.println();
                    IO.println("Below are the trip members");
                    memberMap.forEach((id, name) -> {
                        IO.println("Name: " + name + " ID: " + id);
                    });
                    IO.println();

                    int numSplit = 0;
                    while (true) {
                        try {
                            numSplit = Integer.parseInt(IO.readln("Among how many members was the expense split (including the payer): "));

                            if (numSplit <= 0 || numSplit > numMembers) {
                                throw new IllegalArgumentException();
                            }
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Wrong input, please try again");
                        } catch (IllegalArgumentException e) {
                            IO.println("Out of range, please enter an integer in range");
                        }
                    }
                    IO.println();

                    List<Integer> sharedList = new ArrayList<>();
                    IO.println("Enter the ID for each member involved in the split (including the payer): ");
                    for (int i = 1; i <= numSplit; i++) {

                        while (true) {
                            try {
                                int splitMember = Integer.parseInt(IO.readln("Enter ID #" + i + " : "));
                                sharedList.add(splitMember);
                                break;
                            } catch (NumberFormatException e) {
                                IO.println("Wrong input, please try again");
                            }
                        }
                    }
                    IO.println();

                    int payerNumber = 0;

                    while (true) {
                        try {
                            payerNumber = Integer.parseInt(IO.readln("Enter the ID for the payer: "));

                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Wrong input, please try again");
                        }
                    }
                    IO.println();


                    int convertedExpense = (int) (expenseAmount * 100);
                    LocalDate date = null;

                    while (true) {
                        IO.println("Please enter the date of the expense in the following format YYYY-MM-DD");
                        try {
                            date = LocalDate.parse(IO.readln("Enter the date: "));
                            break;
                        } catch (DateTimeParseException e) {
                            IO.println("The format is wrong, try again");
                        }
                    }
                    IO.println();
                    Expense expense = new Expense(payerNumber, sharedList, convertedExpense, date);
                    boolean result = trip.addExpense(expense);

                } else if (menuSelection == 2) {
                    IO.println();
                    IO.println("Below are the trip members");
                    memberMap.forEach((id, name) -> {
                        IO.println("Name: " + name + " ID: " + id);
                    });
                    IO.println();

                    IO.println("Enter below the IDs for the payer and the receiver");
                    int payerID = 0;
                    int receiverID = 0;
                    while (true) {
                        try {
                            payerID = Integer.parseInt(IO.readln("Payer ID: "));
                            receiverID = Integer.parseInt(IO.readln("Receiver ID: "));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Invalid input, please try again");
                        }

                    }
                    IO.println();

                    double paymentAmount = 0;

                    while (true) {
                        try {
                            paymentAmount = Double.parseDouble(IO.readln("Enter the payment amount: $"));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Please enter a valid number. Ensure no commas are present.");
                        }
                    }
                    IO.println();

                    int convertedPayment = (int) (paymentAmount * 100);


                    LocalDate date = null;

                    while (true) {
                        IO.println("Please enter the date of the payment in the following format YYYY-MM-DD");
                        try {
                            date = LocalDate.parse(IO.readln("Enter the date: "));
                            break;
                        } catch (DateTimeParseException e) {
                            IO.println("The format is wrong, try again");
                        }
                    }
                    IO.println();

                    Payment payment = new Payment(payerID, receiverID, convertedPayment, date);
                    boolean result = trip.addPayment(payment);

                } else if (menuSelection == 3) {
                    IO.println();
                    IO.println();
                    IO.println("The current balances are:");
                    trip.displayBalance();

                } else if (menuSelection == 4) {
                    IO.println();
                    trip.finalSummary();
                    break;

                } else if (menuSelection == 5) {

                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tripFile))) {
                        oos.writeObject(trip);
                        oos.flush();
                    }
                    catch (IOException e) {

                    }

                    break;

                }

            }
        }
        else {
            Trip trip = null;

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tripFile))) {
                trip = (Trip) ois.readObject();


            }
            catch (ClassNotFoundException e) {

            }
            catch (EOFException e) {

            }
            catch (IOException e) {
                e.printStackTrace();

            }

            IO.println("Welcome Back to the Group Expense Tracker");
            IO.println("Here is the current balance summary for the trip: " + trip.getTripName());
            IO.println();
            IO.println();
            IO.println("The current balances are:");
            trip.displayBalance();

            int numMembers = trip.getMemberIDs().size();

            while (true) {
                IO.println();
                IO.println("Please select one of the following options:\n 1) Add Expense\n 2) Add Payment\n 3) View Balance Summary\n 4) End Trip\n 5) Save and Exit");

                int menuSelection = 0;
                while (true) {
                    try {
                        menuSelection = Integer.parseInt(IO.readln("Enter the integer next to your selection: "));
                        if (menuSelection < 1 || menuSelection > 5) {
                            throw new IllegalArgumentException();
                        }
                        break;
                    } catch (NumberFormatException e) {
                        IO.println("Wrong input, please try again");
                    } catch (IllegalArgumentException e) {
                        IO.println("Please a number between 1 and 5 for your selected option");
                    }
                }

                if (menuSelection == 1) {

                    double expenseAmount = 0;
                    IO.println();
                    while (true) {
                        try {
                            expenseAmount = Double.parseDouble(IO.readln("Enter the expense amount: $"));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Please enter a valid number. Ensure no commas are present.");
                        }

                    }
                    IO.println();
                    IO.println("Below are the trip members");
                    trip.getMemberIDs().forEach((id, name) -> {
                        IO.println("Name: " + name + " ID: " + id);
                    });
                    IO.println();

                    int numSplit = 0;
                    while (true) {
                        try {
                            numSplit = Integer.parseInt(IO.readln("Among how many members was the expense split (including the payer): "));

                            if (numSplit <= 0 || numSplit > numMembers) {
                                throw new IllegalArgumentException();
                            }
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Wrong input, please try again");
                        } catch (IllegalArgumentException e) {
                            IO.println("Out of range, please enter an integer in range");
                        }
                    }
                    IO.println();

                    List<Integer> sharedList = new ArrayList<>();
                    IO.println("Enter the ID for each member involved in the split (including the payer): ");
                    for (int i = 1; i <= numSplit; i++) {

                        while (true) {
                            try {
                                int splitMember = Integer.parseInt(IO.readln("Enter ID #" + i + " : "));
                                sharedList.add(splitMember);
                                break;
                            } catch (NumberFormatException e) {
                                IO.println("Wrong input, please try again");
                            }
                        }
                    }
                    IO.println();

                    int payerNumber = 0;

                    while (true) {
                        try {
                            payerNumber = Integer.parseInt(IO.readln("Enter the ID for the payer: "));

                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Wrong input, please try again");
                        }
                    }
                    IO.println();


                    int convertedExpense = (int) (expenseAmount * 100);
                    LocalDate date = null;

                    while (true) {
                        IO.println("Please enter the date of the expense in the following format YYYY-MM-DD");
                        try {
                            date = LocalDate.parse(IO.readln("Enter the date: "));
                            break;
                        } catch (DateTimeParseException e) {
                            IO.println("The format is wrong, try again");
                        }
                    }
                    IO.println();
                    Expense expense = new Expense(payerNumber, sharedList, convertedExpense, date);
                    boolean result = trip.addExpense(expense);

                } else if (menuSelection == 2) {
                    IO.println();
                    IO.println("Below are the trip members");
                    trip.getMemberIDs().forEach((id, name) -> {
                        IO.println("Name: " + name + " ID: " + id);
                    });
                    IO.println();

                    IO.println("Enter below the IDs for the payer and the receiver");
                    int payerID = 0;
                    int receiverID = 0;
                    while (true) {
                        try {
                            payerID = Integer.parseInt(IO.readln("Payer ID: "));
                            receiverID = Integer.parseInt(IO.readln("Receiver ID: "));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Invalid input, please try again");
                        }

                    }
                    IO.println();

                    double paymentAmount = 0;

                    while (true) {
                        try {
                            paymentAmount = Double.parseDouble(IO.readln("Enter the payment amount: $"));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Please enter a valid number. Ensure no commas are present.");
                        }
                    }
                    IO.println();

                    int convertedPayment = (int) (paymentAmount * 100);


                    LocalDate date = null;

                    while (true) {
                        IO.println("Please enter the date of the payment in the following format YYYY-MM-DD");
                        try {
                            date = LocalDate.parse(IO.readln("Enter the date: "));
                            break;
                        } catch (DateTimeParseException e) {
                            IO.println("The format is wrong, try again");
                        }
                    }
                    IO.println();

                    Payment payment = new Payment(payerID, receiverID, convertedPayment, date);
                    boolean result = trip.addPayment(payment);

                } else if (menuSelection == 3) {
                    IO.println();
                    IO.println();
                    IO.println("The current balances are:");
                    trip.displayBalance();

                } else if (menuSelection == 4) {
                    IO.println();
                    trip.finalSummary();
                    tripFile.delete();
                    break;

                } else if (menuSelection == 5) {

                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tripFile))) {
                        oos.writeObject(trip);
                        oos.flush();
                    }
                    catch (IOException e) {

                    }

                    break;

                }

            }



        }







    }

}



