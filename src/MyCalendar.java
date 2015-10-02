/**
 * 
 */
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MyCalendar
{ 
   boolean running = true;
   
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
    * 
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
   
   public void processMainEvent(Scheduler p)
   {
      Scanner in = new Scanner(System.in);
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
            in.close();
            running = false;
            break;
         default:
            break;
      }
   }
   
   public void deleteEvent(Scanner in, Scheduler p)
   {
      System.out.printf("Which event(s) would you like to delete?\n");
      System.out.printf("[S]elected or [A]ll ?");
      System.out.printf(">> ");
      
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
   
   public void goToEvent(Scanner in, Scheduler p)
   {
      System.out.printf("Enter a date to view in the format, MM/DD/YYYY\n");
      System.out.printf(">> ");
      String input = in.nextLine();
      
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
   }
   
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
    * 
    * @return user input.
    */
   public char parseInput(Scanner in)
   {
      char ch = 'x';
      // Receive input.
      System.out.printf(">> ");
      // TODO Carriage return with empty data throws NullPointerException
      ch = in.findInLine(".").charAt(0);
      
      // Flush any additional input.
      in.nextLine();
      
      // Display user selection.
      System.out.printf("%c\n\n", ch);
      
      return ch;
   }
   
   /**
    *    
    */
   public void run()
   {    
      GregorianCalendar gc = new GregorianCalendar();
      Scheduler p = new Scheduler(gc, "events.txt");
      
      while (running) {   
         p.printCalendar();
         printMenu();
         processMainEvent(p);
      }
      
      exit();
   }
   
   /*
    * Exits the application.
    */
   public void exit()
   {
      System.out.println("Goodbye.");
      System.exit(0);
   }
   
   /**
    * 
    * @param args
    */
   public static void main(String[] args)
   {
      MyCalendar mc = new MyCalendar();
      mc.printHeader();
      mc.run();
   }
}
