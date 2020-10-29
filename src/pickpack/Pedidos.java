
package pickpack;


import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pedidos extends javax.swing.JFrame {
    
    String titles[] = {"#","Seller", "Tipo Envio", "Canal", "# Orden", "Orden Marketful", "Descripcion", "Seller SKU", "Cantidad Anunciada", "Cantidad Surtida", "Posicion", "", ""};
    DefaultTableModel model = new DefaultTableModel();
    HttpRequest request = new HttpRequest();

    public Pedidos() {
        initComponents();
        this.setTitle("Pick & Pack");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTable();
    }
    
    public final void setTable(){
        model = new DefaultTableModel(null,titles){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        JButton btn1 = new JButton("Registrar");
        JButton btn2 = new JButton("Calcular");
        tableData.setModel(model);
        tableData.setDefaultRenderer(Object.class, new Render());
        tableData.setRowHeight(30);
        try {
            StringBuilder response = request.getPedidos();
            JSONObject res = new JSONObject(response.toString());
            JSONArray los_pedidos = res.getJSONArray("pedidos");
            System.out.println(los_pedidos.length());
            System.out.println(los_pedidos);
            
//            JSONObject elemento = los_pedidos.getJSONObject(0);
//            System.out.println(elemento);
//            System.out.println(elemento.getString("seller_sku"));
            for (int i = 0; i < los_pedidos.length(); i++) {
                JSONObject elemento = los_pedidos.getJSONObject(i);
                Object row[] = new Object[13];
                row[0] = String.valueOf(i+1);
                row[1] = elemento.getString("seller_name");
                row[2] = elemento.getString("tipo_envio");
                row[3] = elemento.getString("canal");
                if("null".equals(elemento.getString("canal"))){
                    row[3] = "Shopify";
                }
                row[4] = elemento.getString("shopi_order_name");
                row[5] = elemento.getString("shopi_order_id");
                row[6] = elemento.getString("descripcion");
                row[7] = elemento.getString("seller_sku");
                row[8] = elemento.getString("cantidad_pedido");
                row[9] = elemento.getString("cantidad_sacado");
                row[10] = elemento.getString("posicion_1_name");
                row[11] = btn1;
                row[12] = btn2;             
//                model.addRow(new Object[]{ "wfw", "title1", "start", "stop", "pause", "status", "status", "status", "status", "status", "status" });
                model.addRow(row);
            }
        } catch (Exception ex) {
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
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
