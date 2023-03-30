package community.solace.mc.idea.plugin.ui.common;

import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.function.Consumer;

/**
 * JScrollPane with a JTable in its viewport.
 * JTable is customized for right-click and double-click behaviour:
 * - The right-click will correctly select the table row
 * - A JPopupMenu is expected for the right-click
 * - Double-click behaviour is provided via the doubleClickHandler
 * - The entire selected row will be passed into the doubleClickHandler as a String array
 */
public class SolaceTable extends JScrollPane {
    private final JTable table;
    public SolaceTable(TableModel tableModel, JPopupMenu popupMenu, Consumer<String[]> doubleClickHandler) {
        table = new JBTable() {
            @Override
            public boolean editCellAt(int row, int column, EventObject e) {
                return false;
            }
        };

        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r >= 0 && r < tableModel.getRowCount()) {
                    table.setRowSelectionInterval(r, r);

                    if (e.getButton() == MouseEvent.BUTTON3) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        String[] row = new String[table.getColumnCount()];

                        for (int i = 0; i < table.getColumnCount(); i++) {
                            row[i] = table.getValueAt(table.getSelectedRow(), i).toString();
                        }

                        doubleClickHandler.accept(row);
                    }
                } else {
                    table.clearSelection();
                }
            }
        });

        setViewportView(table);
    }

    public String getValueForSelectedRow(int index) {
        return table.getValueAt(table.getSelectedRow(), index).toString();
    }

    public JTable getTable() {
        return table;
    }
}
