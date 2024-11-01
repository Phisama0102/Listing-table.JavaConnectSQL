import javax.swing.*; // นำเข้าคลาสจาก Swing เพื่อสร้างส่วนติดต่อผู้ใช้แบบกราฟิก
import javax.swing.table.DefaultTableModel; // นำเข้าคลาส DefaultTableModel สำหรับจัดการโมเดลของตาราง
import java.awt.*; // นำเข้าคลาสจาก AWT สำหรับการสร้างและจัดการ UI
import java.awt.event.ActionEvent; // นำเข้าคลาสสำหรับเหตุการณ์ ActionEvent
import java.awt.event.ActionListener; // นำเข้าคลาสสำหรับสร้างตัวฟังการทำงานของเหตุการณ์
import java.sql.*; // นำเข้าคลาสสำหรับการทำงานกับฐานข้อมูล SQL

public class Data extends JFrame { // สร้างคลาส Data ที่สืบทอดจาก JFrame (หน้าต่างหลักของโปรแกรม)
    private JTable table; // สร้างตัวแปร table เพื่อแสดงข้อมูลในตาราง
    private DefaultTableModel model; // สร้างตัวแปร model สำหรับจัดการโมเดลของตาราง
    private JButton loadButton, clearButton, searchButton; // สร้างปุ่มสำหรับโหลดข้อมูล, ล้างข้อมูล, และค้นหาข้อมูล
    private JTextField inputField, searchField; // สร้างช่องป้อนข้อมูลสำหรับเพิ่มและค้นหาข้อมูล

    public Data() { // คอนสตรัคเตอร์สำหรับคลาส Data
        setTitle("Database to JTable"); // กำหนดชื่อหน้าต่าง
        setSize(600, 400); // กำหนดขนาดของหน้าต่าง
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // กำหนดให้โปรแกรมปิดเมื่อหน้าต่างถูกปิด
        setLayout(new BorderLayout()); // ตั้งค่าเลย์เอาต์ของหน้าต่างเป็น BorderLayout

        ensureAutoIncrementId(); // เรียกฟังก์ชันเพื่อให้แน่ใจว่าคอลัมน์ Id ในฐานข้อมูลเป็น AUTO_INCREMENT

        model = new DefaultTableModel(); // สร้างโมเดลของตาราง
        model.addColumn("Id"); // เพิ่มคอลัมน์ชื่อ "Id" ในโมเดล
        model.addColumn("FirstName"); // เพิ่มคอลัมน์ชื่อ "FirstName"
        model.addColumn("LastName"); // เพิ่มคอลัมน์ชื่อ "LastName"
        model.addColumn("Age"); // เพิ่มคอลัมน์ชื่อ "Age"

        table = new JTable(model); // สร้างตารางที่ใช้โมเดลที่สร้างไว้
        add(new JScrollPane(table), BorderLayout.CENTER); // เพิ่มตารางลงใน JScrollPane และวางไว้ตรงกลางของหน้าต่าง

        JPanel controlPanel = new JPanel(); // สร้างแผงควบคุมสำหรับวางปุ่มและช่องป้อนข้อมูล
        controlPanel.setLayout(new GridLayout(5, 1, 5, 5)); // ตั้งค่าเลย์เอาต์เป็น GridLayout ที่มี 5 แถว 1 คอลัมน์

        inputField = new JTextField(); // สร้างช่องป้อนข้อมูลสำหรับเพิ่มข้อมูล
        inputField.setToolTipText("Enter data in the format: FirstName LastName Age"); // กำหนดข้อความคำแนะนำสำหรับผู้ใช้
        controlPanel.add(inputField); // เพิ่มช่องป้อนข้อมูลลงในแผงควบคุม

        searchField = new JTextField(); // สร้างช่องป้อนข้อมูลสำหรับการค้นหา
        searchField.setToolTipText("Enter ID to search"); // กำหนดข้อความคำแนะนำสำหรับผู้ใช้
        controlPanel.add(searchField); // เพิ่มช่องป้อนข้อมูลค้นหาลงในแผงควบคุม

        loadButton = new JButton("Load"); // สร้างปุ่มโหลดข้อมูล
        clearButton = new JButton("Clear"); // สร้างปุ่มล้างข้อมูล
        searchButton = new JButton("Search"); // สร้างปุ่มค้นหาข้อมูล

        controlPanel.add(loadButton); // เพิ่มปุ่มโหลดข้อมูลลงในแผงควบคุม
        controlPanel.add(clearButton); // เพิ่มปุ่มล้างข้อมูล
        controlPanel.add(searchButton); // เพิ่มปุ่มค้นหาข้อมูล
        add(controlPanel, BorderLayout.WEST); // วางแผงควบคุมทางด้านซ้ายของหน้าต่าง

        loadButton.addActionListener(new ActionListener() { // เพิ่ม ActionListener ให้ปุ่มโหลดข้อมูล
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputField.getText().isEmpty()) { // ตรวจสอบว่าช่องป้อนข้อมูลว่างหรือไม่
                    loadData(); // ถ้าว่างให้เรียกฟังก์ชันโหลดข้อมูลจากฐานข้อมูล
                } else {
                    addData(); // ถ้าไม่ว่างให้เรียกฟังก์ชันเพิ่มข้อมูลลงในฐานข้อมูล
                }
            }
        });

        clearButton.addActionListener(new ActionListener() { // เพิ่ม ActionListener ให้ปุ่มล้างข้อมูล
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTable(); // เรียกฟังก์ชันล้างข้อมูลในตาราง
            }
        });

        searchButton.addActionListener(new ActionListener() { // เพิ่ม ActionListener ให้ปุ่มค้นหา
            @Override
            public void actionPerformed(ActionEvent e) {
                searchData(); // เรียกฟังก์ชันค้นหาข้อมูลในฐานข้อมูล
            }
        });
    }

    // Method to ensure Id column is auto-increment
    private void ensureAutoIncrementId() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/classroom", "root", "");
            stmt = conn.createStatement();

            // Check if Id column is already AUTO_INCREMENT
            String checkQuery = "SHOW COLUMNS FROM person LIKE 'Id'";
            ResultSet rs = stmt.executeQuery(checkQuery);
            if (rs.next() && rs.getString("Extra").contains("auto_increment")) {
                System.out.println("Id column is already set to auto-increment.");
            } else {
                // Modify the table to set Id as AUTO_INCREMENT if not set
                String alterTableQuery = "ALTER TABLE person MODIFY COLUMN Id INT NOT NULL PRIMARY KEY AUTO_INCREMENT";
                stmt.executeUpdate(alterTableQuery);
                System.out.println("Id column set to auto-increment.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error setting Id column to auto-increment: " + e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to load data from database to JTable
    private void loadData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/classroom", "root", "");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT Id, FirstName, LastName, Age FROM person");
            clearTable(); // Clear existing data in table
            while (rs.next()) {
                int id = rs.getInt("Id");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                int age = rs.getInt("Age");
                model.addRow(new Object[] { id, firstName, lastName, age });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to add data from input field to database
    private void addData() {
        String inputText = inputField.getText();
        String[] data = inputText.split(" ");
        if (data.length != 3) {
            JOptionPane.showMessageDialog(this, "Please enter data in the format: FirstName LastName Age");
            return;
        }

        String firstName = data[0];
        String lastName = data[1];
        int age;
        try {
            age = Integer.parseInt(data[2]);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a number!");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/classroom", "root", "");
            String sql = "INSERT INTO person (FirstName, LastName, Age) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setInt(3, age);
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Data added successfully!");
                inputField.setText(""); // Clear the input field
                loadData(); // Reload data to update the table
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding data: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to clear all rows in JTable
    private void clearTable() {
        model.setRowCount(0);
    }

    // Method to search data by ID and display in JTable
    private void searchData() {
        String searchId = searchField.getText();
        if (searchId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an ID to search.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/classroom", "root", "");
            String sql = "SELECT Id, FirstName, LastName, Age FROM person WHERE Id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(searchId));
            rs = pstmt.executeQuery();

            clearTable(); // Clear the existing data in the table

            if (rs.next()) {
                int id = rs.getInt("Id");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                int age = rs.getInt("Age");
                model.addRow(new Object[] { id, firstName, lastName, age });
            } else {
                JOptionPane.showMessageDialog(this, "No record found with ID: " + searchId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching data: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for ID.");
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Data frame = new Data();
            frame.setVisible(true);
        });
    }
}
