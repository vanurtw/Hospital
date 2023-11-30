import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Загрузка драйвера SQLite
            Class.forName("org.sqlite.JDBC");

            //подключения к БД
            String dbUrl = "jdbc:sqlite:hospital.db";
            connection = DriverManager.getConnection(dbUrl);

            if (connection != null) {
                System.out.println("БД подключена");
                DatabaseManager dbManager = new DatabaseManager();
                //добавление отделения
                dbManager.addDepartment("1");
                dbManager.addDepartment("2");
                dbManager.addDepartment("3");
                //добавление пациента
                dbManager.addPatient(1, "Пупкин Иван", 120, "Мужской");
                dbManager.addPatient(1, "Попов Алекс", 18, "Мужской");
                dbManager.addPatient(2, "Клава", 20, "Женский");
                // вывод отделениев и пациентов
                dbManager.RenderDepartments();
                dbManager.RenderPatients();
                // изменение данных о отделении и пациенте
                dbManager.editDepartment(2, "новое им отделения 2");
                dbManager.editPatient(3, "Новое имя Клавы", 40, "Женский");
                //удаление отделений и пациентов
                dbManager.removeDepartment(3);
                dbManager.removePatient(2);

                dbManager.RenderDepartments();
                dbManager.RenderPatients();

                connection.close();
            } else {
                System.out.println("Не удалось подключиться к БД");
            }
        } catch (ClassNotFoundException exc) {
            System.err.println("Драйвер SQLite не найден.");
            exc.printStackTrace();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    System.err.println("БД закрыта");
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
            }
        }
    }
}
