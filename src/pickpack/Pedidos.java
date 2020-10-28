
package pickpack;


import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;

public class Pedidos extends javax.swing.JFrame {
    
    String titles[] = {"Seller", "Canal", "# Orden", "Orden Marketful", "Descripcion", "Seller SKU", "Cantidad Anunciada", "Cantidad Surtida", "Posicion", "Registrar", "Calcular"};
    DefaultTableModel model = new DefaultTableModel();

    public Pedidos() {
        initComponents();
        model = new DefaultTableModel(null,titles){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tableData.setModel(model);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        for (int count = 1; count <= 10; count++) {
            model.addRow(new Object[]{ "wfw", "title1", "start", "stop", "pause", "status", "status", "status", "status", "status", "status" });
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableData = new javax.swing.JTable();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jTabbedPane3 = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tableData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableData);

        jTabbedPane2.addTab("Pedidos Pendientes", jScrollPane1);

        jTabbedPane1.addTab("Surtir Pedidos", jTabbedPane2);
        jTabbedPane1.addTab("Ver pedidos", jTabbedPane4);
        jTabbedPane1.addTab("Otro", jTabbedPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Pedidos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTable tableData;
    // End of variables declaration//GEN-END:variables
}
