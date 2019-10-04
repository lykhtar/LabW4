package labw4;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
//класс производящий соединение с бд

class ConnectSQL {

    Connection ConnectSQL() {
        Connection connection = null;
        try {
            String url = "jdbc:mysql://localhost:3306/labw4" + "?verifyServerCertificate=false"
                    + "&useSSL=false"
                    + "&requireSSL=false"
                    + "&useLegacyDatetimeCode=false"
                    + "&amp"
                    + "&serverTimezone=UTC";
            String username = "root";
            String password = "root";
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException | SQLException ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }

        return connection;
    }
}
//преподаватели в заданный день в заданной аудитории

class TeacherDay {

    String teachname, dday, classroom;

    public TeacherDay(String teachname, String dday, String classroom) {
        this.dday = dday;
        this.teachname = teachname;
        this.classroom = classroom;
    }

    public String toString() {
        return "Преподаватели, работающе в Субботу в заданной аудитории Д508:" + teachname + "\t" + dday + "\t" + classroom;

    }
}
//Вывести информацию о преподавателях, которые не ведут занятия в заданный день недели

class TeacherNotDay {

    String teachname;

    public TeacherNotDay(String teachname) {
        this.teachname = teachname;
    }

    public String toString() {
        return "Преподаватель, который не ведет занятия в Среду:" + teachname;

    }
}

//Вывести дни недели, в которых проводится заданное количество занятий
class LessonDay {

    String dday;

    public LessonDay(String dday) {
        this.dday = dday;
    }

    public String toString() {
        return "Дни недели, в которых проводится 1 занятие: " + dday;

    }
}
//Вывести дни недели, в которых занято заданное количество аудиторий.
class DayClass {

    String dday;

    public DayClass(String dday) {
        this.dday = dday;
    }

    public String toString() {
        return "дни недели, в которых занято 2 аудитории: " + dday;

    }
}

public class LabW4 {

    public static void main(String[] args) throws SQLException {
        //соединение с бд через отдельный класс
        ConnectSQL conn = new ConnectSQL();
        Connection connection = conn.ConnectSQL();
        //информация о преподавателям работающих в заданный день, в данном случае Суббота
        Statement statement = connection.createStatement();
        ResultSet resSet = statement.executeQuery("SELECT teachers.teachname, dayless.dday, classroom.classroom\n"
                + "FROM labw4.teachers, labw4.dayless , labw4.lessons,labw4.lessteach, labw4.classroom\n"
                + "where dayless.dday=\"Суббота\" and classroom.classroom=\"Д508\"\n"
                + "and dayless.idles=lessons.idles and lessteach.idless=lessons.idles \n"
                + "and lessteach.idteach=teachers.idteache ;");
        while (resSet.next()) {
            String teachname, dday, classroom;
            teachname = resSet.getString("teachname");
            dday = resSet.getString("dday");
            classroom = resSet.getString("classroom");
            TeacherDay teachday = new TeacherDay(teachname, dday, classroom);
            System.out.println(teachday);
        }

        //Вывести информацию о преподавателях, которые не ведут занятия в заданный день недели
        Statement statement1 = connection.createStatement();
        ResultSet resSet1 = statement.executeQuery("SELECT distinct teachers.teachname\n"
                + "FROM labw4.teachers, labw4.dayless , labw4.lessons,labw4.lessteach\n"
                + "where dayless.dday not in (\"Среда\")  and lessteach.idless=lessons.idles and \n"
                + "lessteach.idteach=teachers.idteache and dayless.idles=lessons.idles; ");
        while (resSet1.next()) {
            String teachname;
            teachname = resSet1.getString("teachname");
            TeacherNotDay teachnotday = new TeacherNotDay(teachname);
            System.out.println(teachnotday);
        }

        //Вывести дни недели, в которых проводится заданное количество занятий.
        Statement statement2 = connection.createStatement();
        ResultSet resSet2 = statement.executeQuery("SELECT distinct dayless.dday\n"
                + "FROM labw4.dayless , labw4.lessons,labw4.lessteach,  labw4.mumlec\n"
                + "where mumlec.numlec =\"1\"  and mumlec.idles=lessteach.idless and lessteach.idless=lessons.idles \n"
                + " and dayless.idles=lessons.idles;  ");
        while (resSet2.next()) {
            String dday;
            dday = resSet2.getString("dday");
            LessonDay lessonday = new LessonDay(dday);
            System.out.println(lessonday);
        }

        //Вывести дни недели, в которых занято заданное количество аудиторий
        Statement statement3 = connection.createStatement();
        ResultSet resSet3 = statement.executeQuery("SELECT dayless.dday FROM labw4.dayless "
                + "where ( SELECT COUNT(classroom.classroom) \n"
                + "FROM  labw4.classroom WHERE classroom.idles = dayless.idles ) =2");
        while (resSet3.next()) {
            String dday;
            dday = resSet3.getString("dday");
            DayClass dayclass = new DayClass(dday);
            System.out.println(dayclass);
        }

        statement.close();
        statement1.close();
        statement2.close();
        statement3.close();
        connection.close();
    }

}
//

