import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:hospital.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDepartment(String name) {
        String sql = "INSERT INTO Department (name, numPatients) VALUES (?, 0)";
        try (PreparedStatement add_conn = connection.prepareStatement(sql)) {
            add_conn.setString(1, "Отделение" + name);
            add_conn.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
    }

    public void removeDepartment(int departmentId) {
        String sql = "DELETE FROM Department WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            pstmt.executeUpdate();
            System.out.println("Отделение удалено: " + departmentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPatient(int departmentId, String name, int age, String gender) {
        String insertPatientSQL = "INSERT INTO Patient (departmentId, name, age, gender) VALUES (?, ?, ?, ?)";
        String addPatientDepartmentSQL = "UPDATE Department SET numPatients = numPatients + 1 WHERE id = ?";
        try {
            connection.setAutoCommit(false);
            // добавляем пациента
            PreparedStatement pstmt = connection.prepareStatement(insertPatientSQL);
            pstmt.setInt(1, departmentId);
            pstmt.setString(2, name);
            pstmt.setInt(3, age);
            pstmt.setString(4, gender);
            pstmt.executeUpdate();
            // добавляем пациента в отделение
            PreparedStatement pstmt_dep = connection.prepareStatement(addPatientDepartmentSQL);
            pstmt_dep.setInt(1, departmentId);
            pstmt_dep.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("Пациент добавлен " + name);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void removePatient(int patientId) {
        String deletePatientSQL = "DELETE FROM Patient WHERE id = ?";
        String updateDepartmentSQL = "UPDATE Department SET numPatients = numPatients - 1 WHERE id = (SELECT departmentId FROM Patient WHERE id = ?)";
        try {
            connection.setAutoCommit(false);

            PreparedStatement pstmt1 = connection.prepareStatement(deletePatientSQL);
            pstmt1.setInt(1, patientId);
            pstmt1.executeUpdate();

            PreparedStatement pstmt2 = connection.prepareStatement(updateDepartmentSQL);
            pstmt2.setInt(1, patientId);
            pstmt2.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("Пациент удален: " + patientId);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void editDepartment(int departmentId, String newName) {
        String sql = "UPDATE Department SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, departmentId);
            pstmt.executeUpdate();
            System.out.println("Информация об отделении отредактирована: " + departmentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editPatient(int patientId, String newName, int newAge, String newGender) {
        String sql = "UPDATE Patient SET name = ?, age = ?, gender = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setString(3, newGender);
            pstmt.setInt(4, patientId);
            pstmt.executeUpdate();
            System.out.println("Информация о пациенте отредактирована: " + patientId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void RenderDepartments() {
        String sql = "SELECT id, name, numPatients FROM Department";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rendDepSet = pstmt.executeQuery()) {
            System.out.println("Список отделений:");
            while (rendDepSet.next()) {
                int id = rendDepSet.getInt("id");
                String name = rendDepSet.getString("name");
                int numPatients = rendDepSet.getInt("numPatients");
                System.out.println("ID: " + id + ", Имя: " + name + ", Количество пациентов: " + numPatients);
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
    }

    public void RenderPatients() {
        String sql = "SELECT p.id, p.name, p.age, p.gender, d.name AS department_name FROM Patient p INNER JOIN Department d ON p.departmentId = d.id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet resultSet = pstmt.executeQuery()) {
            System.out.println("Список пациентов:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                String departmentName = resultSet.getString("department_name");
                System.out.println("ID: " + id + ", Имя: " + name + ", Возраст: " + age + ", Пол: " + gender + ", Отделение: " + departmentName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
