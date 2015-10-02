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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.io.IOException;

enum View 
{
   MONTH, DAY;
}

public class Scheduler
{  
   private final String EVENTS_FILE;
   Calendar calendar;
   private View view;
//   private List<Event> events;
   private Map<Date, List<Event>> events;

   /**
    * Constructor Method.
    * @param calendar a Calendar object.
    */
   public Scheduler(Calendar calendar)
   {
      this.EVENTS_FILE = "events.txt";
      this.view = View.MONTH;
      
      // TreeList will keep Dates sorted for us.
      this.events = new TreeMap<Date, List<Event>>();
      
      // Default calendar set for 'today'.
      // TODO: This is currently being overridden elsewhere.
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
      this.events = new HashMap<>();      
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
      }
      catch (FileNotFoundException fnfe)
      {
         fnfe.printStackTrace();
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
         catch (IOException ioe)
         {
            ioe.printStackTrace();
         }
      }
   }
   
   /**
    * Schedules a new event.
    * 
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
   
   
   public void goTo(Calendar c)
   {
      this.setDayView();
      this.calendar = c;
      this.printCalendarByDay();
   }
   
   // TODO Year needs to act as a header
   // TODO Each event for that year should be listed
   // TODO Maybe make a list all events method
   // and a separate list events method to simply list the events for the set
   // date while in view mode:day
   public void listEvents()
   {
      SimpleDateFormat sdf = new SimpleDateFormat();
      Iterator<Map.Entry<Date, List<Event>>> it = events.entrySet().iterator();
      while (it.hasNext())
      {
         Map.Entry<Date, List<Event>> pair = (Map.Entry<Date, List<Event>>) it.next();
         for (Event e : pair.getValue())
         {
            sdf.applyPattern("EEEE, MMM dd, yyyy");
            // Since events do not span multiple days, we can get away with simply
            // using getStartDate() only at this time.
            String date = sdf.format(e.getStart().getTime());
   
            sdf.applyPattern("HH:mm");
            String start = sdf.format(e.getStart().getTime());
            String end = sdf.format(e.getEnd().getTime());
            
            System.out.printf("%s %s - %s %s\n", date, start, end, e.getTitle());
         }
      }
      System.out.printf("\n");
   }
   
   
   public void deleteEvent()
   {
      
   }

   // TODO On quit this will be called.
   // Should order matter here?
   public void saveEvents()
   {
      // Write events to events.txt
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
      // Should be using a tmp value, currently modifies the original
      // Calendar.
      
      // Set the calendar to the first day of the month we can get the first day of the month.
      calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
      
      // Store the last day of the current month.
      int lastDate = calendar.getActualMaximum(Calendar.DATE);
            
      // Store month/year
      // Locale.getDefault() returns the locale for the Java environment,
      // not the system environment. Ideally we would want the system locale.
      String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
      int year = calendar.get(Calendar.YEAR);
      
      // Print calendar header.
      System.out.printf(" %s %s\n", month, year);
      System.out.printf("%s\n", "Su Mo Tu We Th Fr Sa");
      
      // Sunday(1) - Saturday(7)
      int weekdayIndex;
      // First weekday of the month
      int firstWeekday = calendar.get(Calendar.DAY_OF_WEEK);
      
      // Buffer first weekday of the month
      for (weekdayIndex = 1; weekdayIndex < firstWeekday; ++weekdayIndex)
      {
         System.out.print("   ");
      }
      
      // Set counter for row wrapping.
      weekdayIndex = firstWeekday;
      for (int i = 1; i <= lastDate; ++i, ++weekdayIndex)
      {
         // May be able to wrap dates here for event days.
         if (weekdayIndex % 7 == 0)
         {
            System.out.printf("%2d\n", i);
         }
         else {
            System.out.printf("%2d ", i);
         }
      }
      System.out.printf("\n\n");
   }
   
   /**
    * 
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
      System.out.printf("%s, %s %d, %d\n\n", daysOfTheWeek[day-1], month, date, year);
      // Print all events.
   }
   
   /**
    * 
    */
   public void printCalendar()
   {
      if (view == View.MONTH)
      {
         this.printCalendarByMonth();
      }
      else 
      {
         this.printCalendarByDay();
      }
   }
   
   /**
    * Displays the previous calendar month or
    * day of the week depending on the 
    * current view.
    */
   public void next()
   {
      if (view == View.MONTH)
      {
         this.nextMonth();
      }
      else
      {
         this.nextDay();
      }
   }
   
   /**
    * Displays the previous calendar month or
    * day of the week depending on the 
    * current view.
    */
   public void previous()
   {
      if (view == View.MONTH)
      {
         this.previousMonth();
      }
      else
      {
         this.previousDay();
      }
   }
   
   /**
    * 
    */
   public void setMonthView()
   {
      view = View.MONTH;
   }
   
   /**
    * 
    */
   public void setDayView()
   {
      view = View.DAY;
   }
   
   /**
    * 
    */
   public void nextMonth() {
      calendar.add(Calendar.MONTH, 1);
   }
   
   /**
    * 
    */
   public void previousMonth() {
      calendar.add(Calendar.MONTH, -1);
   }
   
   /**
    * 
    */
   public void nextDay()
   {
      calendar.add(Calendar.DATE, 1);
   }
   
   /**
    * 
    */
   public void previousDay()
   {
      calendar.add(Calendar.DATE, -1);
   }
   
//   class CalendarComparator implements Comparator<Calendar>
//   {
//      @Override
//      public int compare(Calendar a, Calendar b)
//      {
//         return a. 
//      }
//   }
}
