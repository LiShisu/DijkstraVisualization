// PreviousTablePanel.java
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class PreviousTablePanel extends JPanel {
    private JTable table;
    private PrevTableModel model;

    public PreviousTablePanel() {
        setLayout(new BorderLayout());
        model = new PrevTableModel();
        table = new JTable(model);
        table.setRowHeight(15);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void updateTable(GraphPanel panel) {
            model.setData(panel.getPreNode());
    }


    class PrevTableModel extends AbstractTableModel {
        private String[] columnNames = {"节点", "前驱节点"};
        private int[] currentData;

        public void setData(int[] data) {
            this.currentData = data;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return currentData == null ? 0 : currentData.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 0) return "节点 " + row;
            return currentData[row] == -1 ? "无" : "节点 " + currentData[row];
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
    }
}