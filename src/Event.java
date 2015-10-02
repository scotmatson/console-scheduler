import java.util.Calendar;

public class Event implements Comparable<Event>
{
   private Calendar start;
   private Calendar end;
   private String title;
   
   public Event(Calendar start, Calendar end, String title)
   {
      this.start = start;
      this.end = end;
      this.title = title;
   }
   
   /**
    * Returns the start date/time of the event.
    * 
    * @return an event Date.
    */
   public Calendar getStart()
   {
      return this.start;
   }
   
   /**
    * Returns the end date/time of the event.
    * 
    * @return an event Date.
    */
   public Calendar getEnd()
   {
      return this.end;
   }
   
   /**
    * Returns the title of the event.
    * 
    * @return the event title.
    */
   public String getTitle()
   {
      return this.title;
   }

   @Override
   public int compareTo(Event c)
   {
      return this.getStart().compareTo(c.getStart());
   }
}
