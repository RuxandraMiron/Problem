import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public List<String> readFromFile() {  //the function used to read from file and store all the lines in the elements of the list
        try {
            FileInputStream fis = new FileInputStream("File.txt");
            Scanner sc = new Scanner(fis);    //file to be scanned
            String line = null;
            List<String> list = new ArrayList<>();
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                list.add(line);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<List<Time>> setCalendar(String line) { //the function used to get all the hours,which define the beginning and the end of a meeting,
        // and store them as Time Data Types in Lists
        List<List<Time>> f = new ArrayList<>();

        String[] parts = line.split(": ");

        String replace = parts[1].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("'", "").replaceAll(" ", "");

        String[] hours = replace.split(",");
        int n = hours.length;

        int j = 0;

        DateFormat formatter = new SimpleDateFormat("HH:mm");
        while (n / 2 >= 1) {
            List<Time> list1 = new ArrayList<>();

            for (int i = 0; i < 2; i++) {

                Time h = null;
                try {
                    h = new Time(formatter.parse(hours[i + j]).getTime());// obtaining the desired format to store the data
                    list1.add(h);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            f.add(list1);//storing all the meetings in a List
            j = j + 2;
            n = n - 2;

        }
        return f;
    }

    public List<Time> setLimits(String line) { //the function used to get the beginning and the end of the timetable and store them as Time Data Type
        String[] parts = line.split(": ");

        String replace = parts[1].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("'", "").replaceAll(" ", "");

        String[] hours = replace.split(",");
        List<Time> list1 = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        try {
            for (int i = 0; i < 2; i++) {
                Time h = new Time(formatter.parse(hours[i]).getTime());
                list1.add(h);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list1;
    }

    public int setDuration(String line) {// function used to get the minimum duration of one meeting
        String[] parts = line.split(": ");
        int duration = Integer.parseInt(parts[1]);
        return duration;

    }

    public List<List<Time>> possibleMeetings(Calendar c1, Calendar c2, int duration) {
        long durationInMilli = duration * 60000;//duration in milliseconds
        List<List<Time>> list = new ArrayList<>();
        int n = c1.calendar.size();
        int m = c2.calendar.size();
        int i = 0;//index for Calendar1
        int j = 0;//index for Calendar2
        /*I added at the beginning of each Calendars the later hour from the 2 beginnings because it is obvious that there will be no meeting If one of the persons is not available
          I added at the end of the each Calendars the earlier hour from the 2 ends for the same reason
          I dit this in order to also find the meetings that can happen before/after the first/last meeting for each person
        */
        List<Time> listBegC1 = new ArrayList<>();
        List<Time> listBegC2 = new ArrayList<>();

        int x = c1.beginning.compareTo(c2.beginning);
        if (x >= 0) {
            listBegC1.add(c1.beginning);
            listBegC1.add(c1.beginning);
            listBegC2.add(c1.beginning);
            listBegC2.add(c1.beginning);
        } else {
            listBegC1.add(c2.beginning);
            listBegC1.add(c2.beginning);
            listBegC2.add(c2.beginning);
            listBegC2.add(c2.beginning);
        }
        c1.calendar.add(0, listBegC1);
        c2.calendar.add(0, listBegC2);

        List<Time> listEndC1 = new ArrayList<>();
        List<Time> listEndC2 = new ArrayList<>();
        int y = c1.end.compareTo(c2.end);
        if (y <= 0) {
            listEndC1.add(c1.end);
            listEndC1.add(c1.end);
            listEndC2.add(c1.end);
            listEndC2.add(c1.end);
        } else {
            listEndC1.add(c2.end);
            listEndC1.add(c2.end);
            listEndC2.add(c2.end);
            listEndC2.add(c2.end);
        }

        c1.calendar.add(listEndC1);
        c2.calendar.add(listEndC2);

        /* In order to find the desired intervals I went through both calendars and performed cross-comparisons.
         */

        while (i < n + 1 && j < m + 1) {
            List<Time> list1 = new ArrayList<>();
            int a = c1.calendar.get(i).get(1).compareTo(c2.calendar.get(i + 1).get(0));
            int b = c2.calendar.get(j).get(1).compareTo(c1.calendar.get(i + 1).get(0));
            int c = c1.calendar.get(i).get(1).compareTo(c2.calendar.get(j).get(1));
            int d = c1.calendar.get(i + 1).get(0).compareTo(c2.calendar.get(j + 1).get(0));
            int e = c1.calendar.get(i).get(1).compareTo(c1.calendar.get(i + 1).get(0));
            int f = c2.calendar.get(j).get(1).compareTo(c2.calendar.get(j + 1).get(0));


            if (e != 0 && f != 0) { // checking if one person has no meeting that comes right after another

                if (a < 0 && b < 0) {//here I performed the comparison and then I chose the right time that should be added in the List,
                    // verifying if the minimum duration condition is fulfilled
                    if (c < 0)
                        list1.add(c2.calendar.get(j).get(1));
                    else
                        list1.add(c1.calendar.get(i).get(1));

                    if (d < 0)
                        list1.add(c1.calendar.get(i + 1).get(0));
                    else
                        list1.add(c2.calendar.get(j + 1).get(0));
                    i++;
                    j++;
                    if (Math.abs(list1.get(0).getTime() - list1.get(1).getTime()) >= durationInMilli)
                        list.add(list1);
                }
                /*If there doesn't exist an interval between the meetings, then I move forward in the Calendar in which the current meeting ends first  */
                else if (a < 0 && b > 0)
                    i++;
                else if (a > 0 && b < 0)
                    j++;
                else if (a == 0 || b == 0) {
                    i++;
                    j++;

                }

            } else if (a == 0 || b == 0) {
                i++;
                j++;

            } else if (e != 0) {//move forward if the meetings come one after another
                j++;

            } else {//move forward if the meetings come one after another
                i++;

            }

        }
        /* Next, I went through the calendar that still has more than one interval and checked with the remaining meeting of the other calendar
         */
        while (i < n + 1) {
            List<Time> list2 = new ArrayList<>();
            int a = c1.calendar.get(i).get(1).compareTo(c1.calendar.get(i + 1).get(0));
            if (a < 0) {
                list2.add(c1.calendar.get(i).get(1));
                list2.add(c1.calendar.get(i + 1).get(0));
                if (Math.abs(list2.get(0).getTime() - list2.get(1).getTime()) >= durationInMilli)
                    list.add(list2);
            }
            i++;

        }
        while (j < m + 1) {
            List<Time> list2 = new ArrayList<>();
            int a = c2.calendar.get(j).get(1).compareTo(c2.calendar.get(j + 1).get(0));
            if (a < 0) {
                list2.add(c2.calendar.get(j).get(1));
                list2.add(c2.calendar.get(j + 1).get(0));
                if (Math.abs(list2.get(0).getTime() - list2.get(1).getTime()) >= durationInMilli)
                    list.add(list2);
            }
            j++;

        }

        return list;
    }

    public void writeToFile(List<List<Time>> list)
            throws IOException {
        String str = list.toString();
        BufferedWriter writer = new BufferedWriter(new FileWriter("Result.txt"));
        writer.write(str);

        writer.close();
    }
}
