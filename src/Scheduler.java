import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.io.IOException;

/**
   COPYRIGHT (C) 2015 Scot Matson. All Rights Reserved.

   Class to Simulate a Scheduler.

   Solves CS151 homework assignment #2

   @author Scot Matson

   @version 1.00 2015/10/02
 */
public class Scheduler
{  
   private final String EVENTS_FILE;
   Calendar calendar;
   private View view;
   private Map<Date, List<Event>> events;

   /**
    * Constructor method,
    * Provides a default filename.
    * @param calendar a Calendar object.
    */
   public Scheduler(Calendar calendar)
   {
      this.EVENTS_FILE = "events.txt";
      this.view = View.MONTH;
      
      // TreeMap will keep Dates sorted.
      this.events = new TreeMap<Date, List<Event>>();
      
      // Default calendar set for 'today'.
      this.calendar = calendar;
   }
   
   /**
    * Constructor method.
    * @param calendar a Calendar object.
    * @param filename filename for saving/loading Events.
    */
   public Scheduler(Calendar calendar, String filename)
   {
      this.EVENTS_FILE = filename;
      this.view = View.MONTH;
      // TreeMap will keep Dates sorted.
      this.events = new TreeMap<>();      
      this.calendar = calendar;
   }
   
   /**
    * Loads events from a text file and turns them into
    * event objects. Must be formatted as,
    * [time in milliseconds]
    * [time in milliseconds]
    * [name]
    * with no empty lines.
    * 
    */
   public void loadEvents()
   {
      // Load events from the file
      // and turn them into objects
      FileReader fr = null;
      BufferedReader br = null;
      try 
      {
         fr = new FileReader(new File(EVENTS_FILE));
         br = new BufferedReader(fr);
         String line;
         
         // Because this implementation is fairly trivial we want to prevent duplicate
         // data from being created. Clearing the map is the simplest way to go about
         // this.
         deleteAllEvents();
         
         // File is stored as 3 line data blocks.
         while ((line = br.readLine()) != null)
         {
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(Long.parseLong(line));
            
            line = br.readLine();
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(Long.parseLong(line));
            
            line = br.readLine();
            String title = line;
            
            this.addEvent(start, end, title);
         }
         
         System.out.printf("File has successfully been loaded.");
      }
      catch (FileNotFoundException fnfe)
      {
         //fnfe.printStackTrace();
         System.out.printf("File does not exist.\n");
      }
      catch (IOException ioe)
      {
         ioe.printStackTrace();
      }
      finally
      {
         try
         {
            fr.close();
            br.close();
         }
         catch (NullPointerException npe)
         {
            System.out.printf("No data found.\n\n");
         }
         catch (IOException ioe)
         {
            ioe.printStackTrace();
         }
      }
   }
   
   /**
    * Schedules a new event.
    * @param start date/time when the event starts.
    * @param end date/time when the event ends.
    * @param title name of the event.
    */
   public void addEvent(Calendar start, Calendar end, String title)
   {
      Event e = new Event(start, end, title);

      // Format used for storing events [KEY]
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      Date eventDate = null;
      try
      {
         // The start date defines the position in the events HashMap
         eventDate = sdf.parse(sdf.format(e.getStart().getTime()));
      }
      catch (ParseException pe)
      {
         pe.printStackTrace();
      }
      
      // Locate the [KEY] position if it exists, otherwise create a new position.
      if (events.containsKey(eventDate))
      {
         // Retrieve the list of events for this date and add the new event.
         List<Event> eventList = events.get(eventDate);
         eventList.add(e);
         
         // Resort the list.
         Collections.sort(eventList, new Comparator<Event>()
               {
                  public int compare(Event a, Event b)
                  {
                     return a.compareTo(b);
                  }
               });
      }
      else 
      {
         // If the key does not exist, we need a new event list to hold
         // events for this date.
         List<Event> eventList = new ArrayList<>();
         eventList.add(e);
         events.put(eventDate, eventList);
      }
   }
   
   /**
    * Sets the calendar to a given date and displays
    * the day along with events, if any.
    * @param c Calendar object set with the go-to date.
    */
   public void goTo(Calendar c)
   {
      this.calendar = c;
      this.printCalendarByDay();
   }

   /**
    * Lists all scheduled events.
    */
   public void listAllEvents()
   {
      if (events.isEmpty())
      {
         System.out.printf("There are currently no events scheduled.\n");
      }
      else 
      {
         Iterator<Map.Entry<Date, List<Event>>> it = events.entrySet().iterator();
         int yearHeader = -1;
         while (it.hasNext())
         {
            Map.Entry<Date, List<Event>> pair = (Map.Entry<Date, List<Event>>) it.next();
            
            // Prints a YEAR header for each list of Calendar Events.
            Calendar keyYear = Calendar.getInstance();
            keyYear.setTime(pair.getKey());
            if (yearHeader != keyYear.get(Calendar.YEAR))
            {
               yearHeader = keyYear.get(Calendar.YEAR);
               System.out.printf("%d\n", yearHeader);
            }
            
            for (Event e : pair.getValue())
            {
               SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM dd");
               String date = sdf.format(e.getStart().getTime());
               
               sdf.applyPattern("HH:mm");
               String start = sdf.format(e.getStart().getTime());
               String end = sdf.format(e.getEnd().getTime());
               System.out.printf("  %s %s - %s %s\n", date, start, end, e.getTitle());
            }
         }
      }
      System.out.printf("\n");
   }
   
   /**
    * Lists all events for the given date.
    * @param date the set date.
    */
   public void listDayEvents(Date date)
   {
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      try
      {
         // Ensure Date object is in the proper format to
         // compare against the map [KEY].
         date = sdf.parse(sdf.format(date));
      }
      catch (ParseException pe)
      {
         pe.printStackTrace();
      }
      
      sdf.applyPattern("HH:mm");
      if (events.containsKey(date))
      {
         List<Event> eventList = events.get(date);
         for (Event e : eventList)
         {
            String start = sdf.format(e.getStart().getTime());
            String end = sdf.format(e.getEnd().getTime());
            System.out.printf("%s %s - %s\n", e.getTitle(), start, end);
         }
         System.out.printf("\n");
      }
      else
      {
         System.out.printf("  No events scheduled for today.\n\n");
      }
   }
   
   /**
    * Deletes all scheduled events.
    */
   public void deleteAllEvents()
   {
      events.clear();
   }
   
   /**
    * Deletes all scheduled events for a given date.
    * @param d event date to delete.
    */
   public void deleteDayEvents(Date d)
   {
      if (events.containsKey(d))
      {
         events.remove(d);
      }
      else
      {
         System.out.printf("No events scheduled for that date.\n\n");
      }
   }

   /**
    * Saves all events to a text file.
    */
   public void saveEvents()
   {
      FileWriter fw = null;
      try
      {
         fw = new FileWriter(new File(EVENTS_FILE));
         StringBuilder sb = new StringBuilder();
         
         Iterator<Map.Entry<Date, List<Event>>> it = events.entrySet().iterator();
         while (it.hasNext())
         {
            Map.Entry<Date, List<Event>> pair = (Map.Entry<Date, List<Event>>) it.next();
            for (Event e : pair.getValue())
            {
               sb.append(e.getStart().getTimeInMillis() + "\n");
               sb.append(e.getEnd().getTimeInMillis() + "\n");
               sb.append(e.getTitle());
               if (pair.getValue().indexOf(e) < pair.getValue().size()-1 || it.hasNext())
               {
                  sb.append("\n");
               }
            }
            it.remove();
         }
         sb.trimToSize();
         fw.write(sb.toString());
      }
      catch (IOException ioe)
      {
         ioe.printStackTrace();
      }
      finally
      {
         try
         {
            fw.close();
         }
         catch (IOException ioe)
         {
            ioe.printStackTrace();
         }
      }
   }
   
   /**
    * Prints the calendar month but does not yet take into account EVENTS!!!
    */
   private void printCalendarByMonth()
   {  
      // Clone the stored calendar to generate the calendar output,
      // this will help maintain state of the stored calendar object.
      Calendar cal = Calendar.getInstance();
      cal.setTime(calendar.getTime());
      
      // Set the bounds for the current month.
      cal.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
      int lastDate = cal.getActualMaximum(Calendar.DATE);
            
      // Store the month and year for the header.
      // * Locale.getDefault() returns the locale for the Java environment,
      //   not the system environment. Ideally we would want the system locale.
      String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
      int year = cal.get(Calendar.YEAR);
      
      // Print calendar header.
      System.out.printf(" %s %s\n", month, year);
      System.out.printf("%s\n", "Su Mo Tu We Th Fr Sa");
      
      // Sunday(1) - Saturday(7)
      int weekdayIndex;
      // Get the day of the week that the month starts on.
      int firstWeekday = cal.get(Calendar.DAY_OF_WEEK);
      
      // Buffer first weekday of the month
      for (weekdayIndex = 1; weekdayIndex < firstWeekday; ++weekdayIndex)
      {
         System.out.print("   ");
      }
      
      // Set counter for row wrapping.
      weekdayIndex = firstWeekday;
      for (int i = 1; i <= lastDate; ++i, ++weekdayIndex, cal.add(Calendar.DATE, 1))
      {
         Date calendarDay = cal.getTime();
         SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
         try
         {
            // Formatting for [KEY] comparison.
            calendarDay = sdf.parse(sdf.format(calendarDay));
         }
         catch (ParseException pe)
         {
            pe.printStackTrace();
         }
         
         // Line spacing w/ event flag.
         if (events.containsKey(calendarDay))
         {
            if (weekdayIndex % 7 == 0)
            {
               System.out.printf("[%2d]\n", i);
            }
            else {
               System.out.printf("[%2d] ", i);
            }
         }
         else
         {
            // Line spacing.
            if (weekdayIndex % 7 == 0)
            {
               System.out.printf("%2d\n", i);
            }
            else 
            {
               System.out.printf("%2d ", i);
            }
         }
      }
      System.out.printf("\n");
   }
   
   /**
    * Prints the current day and all associated events.
    */
   private void printCalendarByDay()
   {
      // This should be starting on today's date.
      // print month view is altering the original Calendar object
      // need to use a tmp value.
      
      // Set calendar to today's date.
      String[] daysOfTheWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
      
      int day = calendar.get(Calendar.DAY_OF_WEEK);
      String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
      int date = calendar.get(Calendar.DAY_OF_MONTH);
      int year = calendar.get(Calendar.YEAR);

      // Print Day, Month, Year format
      System.out.printf("%s, %s %d, %d\n", daysOfTheWeek[day-1], month, date, year);
      listDayEvents(calendar.getTime());
   }
   
   /**
    *   Prints the calendar based upon the set view.
    */
   public void printCalendar()
   {
      switch (view)
      {
         case MONTH:
            this.printCalendarByMonth();
            break;
         case DAY:
            this.printCalendarByDay();
            break;
         default:
            System.out.printf("View mode not set, unable to display calendar.");
            break;
      }
   }
   
   /**
    * Displays the previous calendar month or
    * day of the week depending on the 
    * current view.
    */
   public void next()
   {
      switch(view)
      {
         case MONTH:
            calendar.add(Calendar.MONTH, 1);
            break;
         case DAY:
            calendar.add(Calendar.DATE, 1);
            break;
         default:
            System.out.printf("View mode not set, unable to display calendar.");
            break;
      }
   }
   
   /**
    * Displays the previous calendar month or
    * day of the week depending on the 
    * current view.
    */
   public void previous()
   {
      switch(view)
      {
         case MONTH:
            calendar.add(Calendar.MONTH, -1);
            break;
         case DAY:
            calendar.add(Calendar.DATE, -1);
            break;
         default:
            System.out.printf("View mode not set, unable to display calendar.");
            break;
      }
   }
   
   /**
    * Sets the calendar view-mode to month.
    */
   public void setMonthView()
   {
      view = View.MONTH;
   }
   
   /**
    * Sets calendar view-mode to day.
    */
   public void setDayView()
   {
      view = View.DAY;
   }
}

/**
 * Predefined view settings which determine how
 * the calendar is presented
 */
enum View
{
   MONTH, DAY
}