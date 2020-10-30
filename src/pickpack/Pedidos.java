
package pickpack;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pedidos extends javax.swing.JFrame {
    
    String titles[] = {"#","Seller", "Tipo Envio", "Canal", "# Orden", "Orden Marketful", "Descripcion", "Seller SKU", "Cantidad Anunciada", "Cantidad Surtida", "Posicion", "", ""};
    DefaultTableModel model = new DefaultTableModel();
    HttpRequest request = new HttpRequest();
    public String url_etiquetas = null;
    public String user_name = "";

    public Pedidos(String usuario) {
        this.user_name = usuario;
        initComponents();
        if("none".equals(usuario)){
            userWelcome.setText("Ninguna Sesion Activa");
        }else{
            userWelcome.setText("Hola, " +this.user_name);
        }

        donwloadPdf.setEnabled(false);
        serrarSesion.setEnabled(false);
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
             JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 102 - Error al consultar los pedidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogEtiqueta = new javax.swing.JDialog();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableData = new javax.swing.JTable();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        userWelcome = new javax.swing.JLabel();
        btnActualizarPedidos = new javax.swing.JButton();
        btnGenerarPdf = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        serrarSesion = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        donwloadPdf = new javax.swing.JMenuItem();

        javax.swing.GroupLayout dialogEtiquetaLayout = new javax.swing.GroupLayout(dialogEtiqueta.getContentPane());
        dialogEtiqueta.getContentPane().setLayout(dialogEtiquetaLayout);
        dialogEtiquetaLayout.setHorizontalGroup(
            dialogEtiquetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        dialogEtiquetaLayout.setVerticalGroup(
            dialogEtiquetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

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

        userWelcome.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userWelcome.setText("Marketful");

        btnActualizarPedidos.setText("Actualizar Pedidos");
        btnActualizarPedidos.setEnabled(false);
        btnActualizarPedidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarPedidosActionPerformed(evt);
            }
        });

        btnGenerarPdf.setText("Generar Etiquetas");
        btnGenerarPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarPdfActionPerformed(evt);
            }
        });

        jMenu1.setText("Archivo");

        serrarSesion.setText("Cerrar Sesion");
        jMenu1.add(serrarSesion);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Opciones");

        jMenu3.setText("Descargar PDF's");

        donwloadPdf.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        donwloadPdf.setText("Ultimo Reporte de Etiquetas");
        donwloadPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                donwloadPdfActionPerformed(evt);
            }
        });
        jMenu3.add(donwloadPdf);

        jMenu2.add(jMenu3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnActualizarPedidos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGenerarPdf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(userWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userWelcome)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnActualizarPedidos)
                            .addComponent(btnGenerarPdf))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnActualizarPedidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarPedidosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnActualizarPedidosActionPerformed
    
    
    private void btnGenerarPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarPdfActionPerformed
        btnGenerarPdf.setEnabled(false);
        int respuestaEtiqueta = 500;
        try {
            respuestaEtiqueta = request.setGenerarPedidos();
        } catch (Exception ex) {
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 100 - Error al generar las etiquetas", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        if(respuestaEtiqueta != 200){
            JOptionPane.showMessageDialog(dialogEtiqueta, "Ocurrio un Error al intentar Generar las Etiquetas, intentalo de nuevo", "Error", JOptionPane.ERROR_MESSAGE);
        }else{
            newThread.start();
//            try {
//                TimeUnit.SECONDS.sleep(1);
//                consultarStatusEtiquetas();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
//                JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 101 - Error al consultar el estatus.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
        }
    }//GEN-LAST:event_btnGenerarPdfActionPerformed

    private void donwloadPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_donwloadPdfActionPerformed
        // TODO add your handling code here:
         JOptionPane.showMessageDialog(dialogEtiqueta, "Ningun Documento PDF Encontrado", "Error", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_donwloadPdfActionPerformed
     Thread newThread = new Thread(() -> {
        consultarStatusEtiquetas();
    });
    public void consultarStatusEtiquetas(){
        String respesta_status = "error";
        System.out.println("Vamos a consultar status etiquetas");
        try {
            respesta_status = request.getStatusEtiquetas();
        } catch (Exception ex) {
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 101 - Error al consultar el estatus.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        if(!"error".equals(respesta_status)){
            int progreso = Integer.parseInt(respesta_status);
            System.out.println("Progreso es: "+progreso);
            if(progreso == 500){
                JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 101 - Error al consultar el estatus.", "Error", JOptionPane.ERROR_MESSAGE);
            }else{
                if(progreso < 100){
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        consultarStatusEtiquetas();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 101 - Error al consultar el estatus.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    getEtiquetaPDF();
                }
            }
        }
    }
    
    public void getEtiquetaPDF(){
        String respuesta_pdf = "error";
        System.out.println("Vamos a obtener el pdf");
        try {
            respuesta_pdf = request.getPdfEtiquetas();
        } catch (Exception ex) {
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 101 - Error al consultar el estatus.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("Respuesta pdf es :"+respuesta_pdf);
        if( !"null".equals(respuesta_pdf ) && !"error".equals(respuesta_pdf) ){
            this.url_etiquetas = respuesta_pdf;
//            JOptionPane.showMessageDialog(dialogEtiqueta, "URL de Etiquetas es" + this.url_etiquetas, "Success", JOptionPane.INFORMATION_MESSAGE);
            btnGenerarPdf.setEnabled(true);
            donwloadPdf.setEnabled(true);
        }else{
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 102 - Error al obtener el documento PDF de las etiquetas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Pedidos("none").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarPedidos;
    private javax.swing.JButton btnGenerarPdf;
    private javax.swing.JDialog dialogEtiqueta;
    private javax.swing.JMenuItem donwloadPdf;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JMenuItem serrarSesion;
    private javax.swing.JTable tableData;
    private javax.swing.JLabel userWelcome;
    // End of variables declaration//GEN-END:variables
}
