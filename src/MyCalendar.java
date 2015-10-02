import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
   COPYRIGHT (C) 2015 Scot Matson. All Rights Reserved.

   Classes to interact with a Scheduler.

   Solves CS151 homework assignment #2

   @author Scot Matson

   @version 1.00 2015/10/02
 */
public class MyCalendar
{ 
   
   boolean running = true;
   
   /**
    * Displays the application title screen.
    */
   public void printHeader()
   {
      System.out.printf("%s\n", "###################################");
      System.out.printf("%s %33s\n", "#", "#");
      System.out.printf("%s %27s %5s\n", "#", "Scot Matson presents,", "#");
      System.out.printf("%s %17s %15s\n", "#", "My Calendar", "#");
      System.out.printf("%s %33s\n", "#", "#");
      System.out.printf("%s %26s %6s\n", "#", "A Java console based", "#");
      System.out.printf("%s %23s %9s\n", "#", "digital scheduler","#");
      System.out.printf("%s %33s\n", "#", "#");
      System.out.printf("%s\n\n", "###################################");
   }
   
   /*
    * Displays main menu options
    * and prompts user to make a selection.
    * @return selection result.
    */
   public void printMenu()
   {
      System.out.printf("%s\n", "Please make a selection from the following list of options.");
      System.out.printf("%s, %s, %s, %s, %s, %s, %s\n", 
            "[L]oad",
            "[V]iew by",
            "[C]reate",
            "[G]o to",
            "[E]vent list",
            "[D]elete",
            "[Q]uit");      
   }
   
   /**
    * Performs actions on a scheduler object based upon
    * character inputs.
    * @param p a Scheduler object
    */
   public void processMainEvent(Scanner in, Scheduler p)
   {
      char action = parseInput(in);

      switch (Character.toLowerCase(action))
      {
         case 'l':
            p.loadEvents();
            break;
         case 'v':
            viewCalendar(in, p);
            break;
         case 'c':
            scheduleEvent(in, p);
            break;
         case 'g':
            goToEvent(in, p);
            break;
         case 'e':
            p.listAllEvents();
            break;
         case 'd':
            deleteEvent(in, p);
            break;
         case 'q':
            p.saveEvents();
            running = false;
            break;
         default:
            System.out.printf("Unable to process request.\n\n");
            break;
      }
   }
   
   /**
    * Deletes all events or events for a given day.
    * @param in a Scanner to receive console input.
    * @param p a Scheduler to carry out user requests.
    */
   public void deleteEvent(Scanner in, Scheduler p)
   {
      System.out.printf("Which event(s) would you like to delete?\n");
      System.out.printf("[S]elected or [A]ll ?");
      
      switch(parseInput(in))
      {
         case 's':
            System.out.printf("Enter the date to delete formatted MM/DD/YYYY.\n");
            System.out.printf(">> ");
            String input = in.nextLine();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            try
            {
               p.deleteDayEvents(sdf.parse(input));
            }
            catch (ParseException pe)
            {
               pe.printStackTrace();
            }
            break;
         case 'a':
            p.deleteAllEvents();
            break;
         default:
            System.out.printf("Unknown Input.");
            break;
      }
   }
   
   /**
    * Prints all events for a given day.
    * @param in a Scanner to receive console input.
    * @param p a Scheduler to carry out user requests.
    */
   public void goToEvent(Scanner in, Scheduler p)
   {
      System.out.printf("Enter a date to view in the format, MM/DD/YYYY\n");
      System.out.printf(">> ");
      String input = in.nextLine();
      System.out.printf("\n");
      
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      Calendar c = Calendar.getInstance();
      
      try
      {
         c.setTime(sdf.parse(input));
      }
      catch (ParseException pe)
      {
         pe.printStackTrace();
      }
      
      p.goTo(c);
   }
   
   /**
    * Displays alternate views of the scheduler.
    * @param in a Scanner to receive console input.
    * @param p a Scheduler to carry out user requests.
    */
   public void viewCalendar(Scanner in, Scheduler p)
   {
      System.out.printf("Please select a view format.\n");
      System.out.printf("[M]onth or [D]ay?\n");
      char action = parseInput(in);
      switch(action)
      {
         case 'm':
            p.setMonthView();
            break;
         case 'd':
            p.setDayView();
            break;
         default:
            System.out.println("Unknown input.");
            break;      
      }
      
      do
      {
         p.printCalendar();
         System.out.printf("[P]revious, [N]ext, or [M]ain?\n");
         action = parseInput(in);
         if (action == 'p')
         {
            p.previous();
         }
         if (action == 'n')
         {
            p.next();
         }
      } while (action != 'm');
      
      // Reset to the default view mode.
      p.setMonthView();
   }
   
   /**
    * Schedules new events.
    * @param in a Scanner to receive console input.
    * @param p a Scheduler to carry out user requests.
    */
   public void scheduleEvent(Scanner in, Scheduler p)
   {
      // Event title.
      System.out.printf("Enter the title for your event.\n");
      System.out.printf(">> ");
      String title = in.nextLine();
      
      // Event Start.
      System.out.printf("Enter the date of your event as MM/DD/YYYY\n");
      System.out.printf(">> ");
      String date = in.nextLine();
      
      System.out.printf("Enter the start time of your event using a 24-hour format, HH:MM\n");
      System.out.printf(">> ");
      String input = in.nextLine();
      
      Calendar start = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");     
      try
      {
         start.setTime(sdf.parse(date + " " + input));
      }
      catch (ParseException pe)
      {
         pe.printStackTrace();
      }
      
      // Event End (Date will be the same as start).
      System.out.printf("Enter the end time of your event using a 24-hour format, HH:MM\n");
      System.out.printf(">> ");
      input = in.nextLine();

      Calendar end = Calendar.getInstance();
      try
      {
         end.setTime(sdf.parse(date + " " + input));
      }
      catch (ParseException pe)
      {
         pe.printStackTrace();
      }
      
      p.addEvent(start, end, title);
   }
   
   /**
    * Parses console input and returns the first
    * character.
    * @return user input as character value.
    */
   public char parseInput(Scanner in)
   {
      char ch = '\u0000';
      // Receive input.
      System.out.printf(">> ");
      // TODO Carriage return with empty data throws NullPointerException
      try
      {
         ch = in.findInLine(".").charAt(0);
      }
      catch (NullPointerException npe)
      {
         System.out.printf("Unknown input.");
      }
      
      // Flush any additional input.
      in.nextLine();
      
      // Display user selection.
      System.out.printf("%c\n\n", ch);
      
      return ch;
   }
   
   /**
    * Runs the application in a continues loop
    * until a command to exit has been given.   
    */
   public void run()
   {    
      Scanner in = new Scanner(System.in);
      // Defining calendar type and filename here so later
      // version would allow for user defined arguments.
      GregorianCalendar gc = new GregorianCalendar();
      Scheduler p = new Scheduler(gc, "events.txt");
      
      while (running) {   
         p.printCalendar();
         printMenu();
         processMainEvent(in, p);
      }
      
      exit(in);
   }
   
   /**
    * Exits the application.
    */
   public void exit(Scanner in)
   {
      in.close();
      System.out.println("Goodbye.");
      System.exit(0);
   }
   
   /**
    * The main method.
    * @param args user defined arguments from the command line.
    */
   public static void main(String[] args)
   {
      MyCalendar mc = new MyCalendar();
      mc.printHeader();
      mc.run();
   }
}