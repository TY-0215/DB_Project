import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class DatabaseSearchGUI {
    private static JTextField userIdField;
    private static JTextField nicknameField;
    private static JTextArea resultArea;
    private static Connection conn;

    private static final Map<String, String> COLUMN_NAME_MAP = new HashMap<String, String>() {{
        put("AREA_NAME", "영지 이름");
        put("SERVER", "서버");
        put("AREA_LEV", "영지 레벨");
        put("GOLD", "보유 골드");
        put("SHILLING", "보유 실링");
        put("MOCOCO", "수집한 모코코 씨앗 개수");
        put("GIANT", "수집한 거인의 심장 개수");
        put("T_LEAF", "수집한 세계수의 잎 개수");
        put("IGNEA", "수집한 이그네아의 증표 개수");
        put("ORPHEUS", "수집한 오르페우스의 별 개수");
        put("ISLAND", "수집한 섬의 마음 개수");
        put("NICKNAME", "보유 캐릭터 닉네임");

        put("ACC_DATE", "접속 날짜");
        put("PLAYTIME", "플레이 시간");
        put("CHAR_LEV", "캐릭터 레벨");
        put("EQUIP_LEV", "장비 레벨");
        put("ACC_IP", "접속 아이피");

        put("RAID_NUM", "완료 레이드 번호");
        put("CLEAR_TIME", "클리어 시간");
        put("MAX_DMG", "해당 레이드 최대 데미지");
        put("MIN_DMG", "해당 레이드 최소 데미지");
        put("AVG_DMG", "해당 레이드 평균 데미지");

        put("TRADE_NUM", "거래 번호");
        put("TRADE_DATE", "거래 진행 일시");
        put("TRADE_ITEM", "거래된 아이템");
    }};


    public static void main(String[] args) {
        JFrame frame = new JFrame("LostArk DATA Search");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "c##oracle_test", "1234");
            System.out.println("DB 연결 완료");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("SQL 실행 에러");
        }
    }

    private static void placeComponents(JPanel panel) {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(null);
        inputPanel.setPreferredSize(new Dimension(300, 500));

        JLabel userLabel = new JLabel("User ID");
        userLabel.setBounds(10, 20, 80, 25);
        inputPanel.add(userLabel);

        userIdField = new JTextField(20);
        userIdField.setBounds(100, 20, 165, 25);
        inputPanel.add(userIdField);

        JButton userButton = new JButton("유저 조회(유저 아이디 입력)");
        userButton.setBounds(10, 60, 255, 25);
        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchById(userIdField.getText());
            }
        });
        inputPanel.add(userButton);

        JLabel nicknameLabel = new JLabel("Nickname");
        nicknameLabel.setBounds(10, 100, 80, 25);
        inputPanel.add(nicknameLabel);

        nicknameField = new JTextField(20);
        nicknameField.setBounds(100, 100, 165, 25);
        inputPanel.add(nicknameField);

        JButton nicknameButton = new JButton("캐릭터 조회(닉네임 입력)");
        nicknameButton.setBounds(10, 140, 255, 25);
        nicknameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchByNickname(nicknameField.getText());
            }
        });
        inputPanel.add(nicknameButton);

        panel.add(inputPanel, BorderLayout.WEST);

        JButton listUsersButton = new JButton("유저 일괄 조회");
        listUsersButton.setBounds(10, 260, 255, 25);
        listUsersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listAllUsers();  // 버튼을 누르면 모든 유저를 출력
            }
        });
        inputPanel.add(listUsersButton);

        JButton resetButton = new JButton("초기화");
        resetButton.setBounds(10, 320, 255, 25);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");  // 초기화 버튼을 누르면 출력창의 내용을 모두 지움
            }
        });
        inputPanel.add(resetButton);

        JButton exitButton = new JButton("종료하기");
        exitButton.setBounds(10, 380, 255, 25);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);  // 종료 버튼을 누르면 프로그램 종료
            }
        });
        inputPanel.add(exitButton);

        resultArea = new JTextArea();
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private static void listAllUsers() {
        resultArea.setText("");
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT USER_ID FROM LA_AREA");
                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(rs.getString("USER_ID"));
                    if (sb.length() > 60) {
                        resultArea.append(sb.toString() + "\n");
                        sb = new StringBuilder();
                    }
                }
                if (sb.length() > 0) {
                    resultArea.append(sb.toString() + "\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void searchById(String userId) {
        resultArea.setText("");
        if (conn != null) {
            try {
                // 영지 테이블 검색
                searchTableByUserId("LA_AREA", new String[]{"AREA_NAME", "SERVER", "AREA_LEV", "GOLD", "SHILLING", "MOCOCO",
                        "GIANT", "T_LEAF", "IGNEA", "ORPHEUS", "ISLAND"}, userId);
                // 캐릭터 테이블 검색
                searchTableByUserId("LA_CHARACTER", new String[]{"NICKNAME"}, userId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void searchByNickname(String nickname) {
        resultArea.setText("");
        if (conn != null) {
            try {
                // 캐릭터 테이블 검색
                searchTableByNickname("LA_CHARACTER", new String[]{"NICKNAME", "ACC_DATE", "PLAYTIME", "CHAR_LEV",
                        "EQUIP_LEV", "ACC_IP", "USER_ID", "AREA_NAME"}, nickname);
                // 거래 테이블 검색
                searchTableByNickname("LA_TRADE", new String[]{"TRADE_NUM", "TRADE_DATE", "TRADE_ITEM"}, nickname);
                // 레이드 테이블 검색
                searchTableByNickname("LA_RAID", new String[]{"RAID_NUM", "CLEAR_TIME", "MAX_DMG", "MIN_DMG", "AVG_DMG"}, nickname);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void searchTableByUserId(String tableName, String[] columnNames, String userId) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE USER_ID = '" + userId + "'");
        if (!rs.next()) {
            resultArea.append("존재하지 않는 데이터입니다.\n\n");
        } else {
            do {
                for (String columnName : columnNames) {
                    String value = rs.getString(columnName);
                    String koreanName = COLUMN_NAME_MAP.getOrDefault(columnName, columnName);
                    resultArea.append(koreanName + ": " + value + "\n");
                }
                resultArea.append("\n");
            } while (rs.next());
        }
    }


    private static void searchTableByNickname(String tableName, String[] columnNames, String nickname) throws SQLException {
        Statement stmt = conn.createStatement();
        String query;
        if ("LA_RAID".equals(tableName)) {
            query = "SELECT RAID_NUM, TO_CHAR(CLEAR_TIME, 'HH24:MI:SS') AS CLEAR_TIME, MAX_DMG, MIN_DMG, AVG_DMG FROM " + tableName + " WHERE NICKNAME = '"
                    + nickname + "'";
        } else {
            query = "SELECT * FROM " + tableName + " WHERE NICKNAME = '" + nickname + "'";
        }
        ResultSet rs = stmt.executeQuery(query);
        if (!rs.next()) {
            resultArea.append("존재하지 않는 데이터입니다.\n\n");
        } else {
            do {
                for (String columnName : columnNames) {
                    String value = rs.getString(columnName);
                    String koreanName = COLUMN_NAME_MAP.getOrDefault(columnName, columnName);
                    if ("CLEAR_TIME".equals(columnName) && value != null) {
                        String[] timeParts = value.split(":");
                        value = timeParts[0] + "시간 " + timeParts[1] + "분 " + timeParts[2] + "초";
                    }
                    resultArea.append(koreanName + ": " + value + "\n");
                }
                resultArea.append("\n");
            } while (rs.next());
        }
    }
}