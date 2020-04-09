import java.io.IOException;
import java.sql.Time;

import java.util.List;

public class Main {


    public static void main(String[] args) {
        Calendar c1 = new Calendar();
        Calendar c2 = new Calendar();
        int duration;
        Utils u= new Utils();
        List<String> list = u.readFromFile();
        System.out.println(list);
        c1.calendar = u.setCalendar(list.get(0));
        System.out.println(c1.calendar);
        c2.calendar = u.setCalendar(list.get(2));
        System.out.println(c2.calendar);
        c1.beginning = u.setLimits(list.get(1)).get(0);
        c1.end = u.setLimits(list.get(1)).get(1);
        c2.beginning = u.setLimits(list.get(3)).get(0);
        c2.end = u.setLimits(list.get(3)).get(1);
        duration = u.setDuration(list.get(4));
        List<List<Time>> possibleM = u.possibleMeetings(c1, c2, duration);
        try {
            u.writeToFile(possibleM);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
