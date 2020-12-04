
package pickpack;


import com.placeholder.PlaceHolder;
import java.awt.Color;
import java.awt.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pedidos extends javax.swing.JFrame {
    
    String titles[] = {"#","Seller", "Tipo Envio", "Canal", "# Orden", "Orden Marketful", "Descripcion", "Seller SKU", "Cantidad Anunciada", "Cantidad Surtida", "Posicion", "Surtir", "Calcular"};
    DefaultTableModel model = new DefaultTableModel();
    File out = new File("prueba_etiquetas.pdf");
    HttpRequest request = new HttpRequest();
    public String url_etiquetas = null;
    public String user_name = "";
    JTextField textBox=new JTextField();
    Sesion sesion = new Sesion();
    String pedido_id = "";
    Boolean es_consolidado = false;
    Integer cantidad_pedido = 0;
    Integer cantidad_anunciada = 0;
    Integer cantidad_surtida = 0;
    Boolean session_activa = false;
    String pedido_iniciado = null;
    Integer row_active = null;
    ColorCelda c = new ColorCelda();
    
    ArrayList<String> lista = new ArrayList<>();
    

    public Pedidos(Sesion session) {
        this.sesion = session;
        System.out.println("Session Actual en Login");
        System.out.println(sesion.getSessionToken());
        System.out.println(sesion.getSessionUserName());
        System.out.println(sesion.getSessionUserId());
        initComponents();
        progressBar.setVisible(false);
        if(this.sesion.getSessionUserName() == null){
            userWelcome.setText("Ninguna Sesion Activa");
             this.session_activa = false;
        }else{
            userWelcome.setText("Hola, " +this.sesion.getSessionUserName());
            this.session_activa = true;
        }
        sellerSKU.setEditable(false);
        sellerSKU.setBackground(null);
        sellerSKU.setBorder(null);
        positionName.setEditable(false);
        positionName.setBackground(null);
        positionName.setBorder(null);
        PlaceHolder holder = new PlaceHolder(positionText, "Codigo Posicion");
        new PlaceHolder(skuText, "SKU/EAN");
        new PlaceHolder(orderText, "Order MKTF");
        
        donwloadPdf.setEnabled(false);
        serrarSesion.setEnabled(false);
        cancelarSurtido.setEnabled(false);
        this.setTitle("Pick & Pack");
        this.positionText.setEditable(false);
        this.skuText.setEditable(false);
        this.orderText.setEditable(false);
        
        this.positionText.setEnabled(false);
        this.skuText.setEnabled(false);
        this.orderText.setEnabled(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTable();
        this.jTabbedPane1.setEnabledAt(1, false);
        this.jTabbedPane1.setEnabledAt(2, false);
        tableData.setSelectionBackground(java.awt.Color.BLUE);
        tableData.setSelectionForeground(java.awt.Color.white);
//        int la_columna = tableData.convertColumnIndexToView(tableData.getColumn("Seller").getModelIndex());
//        System.out.println("la columna es" +la_columna);
//        tableData.getColumnModel().getColumn(la_columna).setCellRenderer(c);
//        tableData
//        int la_columna2 = tableData.convertColumnIndexToView(tableData.getColumn("Calcular").getModelIndex());
//        tableData.getColumnModel().getColumn(la_columna2).setCellRenderer(c);
//        int la_column1 = tableData.convertColumnIndexToView(tableData.getColumn("Surtir").getModelIndex());
//        tableData.getColumnModel().getColumn(0).setCellRenderer(c);
        for (int i = 0; i <= 12; i++) {
            tableData.getColumnModel().getColumn(i).setCellRenderer(c);
        }
    }
    
    public final void setTable(){
        lista.clear();
        model = new DefaultTableModel(null,titles){
            @Override
            public boolean isCellEditable(int row, int column){
                if(column == 10){
                   return true;
                }else{
                   return false;
                }
//                return false;
            }
        };
        JButton btn1 = new JButton("Surtir");
        btn1.setName("surtir_pedido");
        JButton btn2 = new JButton("Calcular");
        btn2.setName("calcular");
        JTextField txt1 = new JTextField();
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
                lista.add(elemento.getString("id"));
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
//                JTextField jt = new JTextField();
                row[11] = btn1;
                row[12] = btn2;             
//                model.addRow(new Object[]{ "wfw", "title1", "start", "stop", "pause", "status", "status", "status", "status", "status", "status" });
                model.addRow(row);
            }
            for (int i = 0; i <= 12; i++) {
                tableData.getColumnModel().getColumn(0).setCellRenderer(c);
            }
        } catch (Exception ex) {
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
             JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 102 - Error al consultar los pedidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
//        TableColumn soprtColumn=tableData.getColumnModel().getColumn(11);
//        soprtColumn.setCellEditor(new DefaultCellEditor (textBox));
//        tableData.setCellSelectionEnabled(false);
//        
//        
//        textBox.addKeyListener(new KeyAdapter(){
//            public void keyTyped(KeyEvent e){
//                if(2 != 2){
//                    textBox.setEditable(false);
//                    textBox.setBackground(Color.WHITE);
//                    JOptionPane.showMessageDialog(null,"String Type Entry Not Allowed");
//                }else{
//                    textBox.setEditable(true);
//                    PlaceHolder holder = new PlaceHolder(textBox, "Seller SKU");
//                    int column = tableData.getColumnModel().getColumnIndexAtX(textBox.getX());
//                    int row = textBox.getY()/tableData.getRowHeight();
//                    System.out.println("Typing in "+ row+ ","+column);
//                }
//            }
//        });
    }
    
//    public void() funcion actualizar posicion de pedido

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogEtiqueta = new javax.swing.JDialog();
        jFileChooser1 = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        numOrden = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ordenMKTF = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cantidadSurtida = new javax.swing.JLabel();
        positionText = new javax.swing.JTextField();
        skuText = new javax.swing.JTextField();
        orderText = new javax.swing.JTextField();
        cancelarSurtido = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        sellerSKU = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        positionName = new javax.swing.JTextPane();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        sellerName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableData = new javax.swing.JTable();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        userWelcome = new javax.swing.JLabel();
        btnActualizarPedidos = new javax.swing.JButton();
        btnGenerarPdf = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        generarPedidos = new javax.swing.JButton();
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

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Surtiendo el Pedido:");

        jLabel3.setText("# Orden:");

        numOrden.setText("       ");

        jLabel5.setText("Orden MKTF:");

        ordenMKTF.setText("       ");

        jLabel2.setText("Seller SKU:");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Surtiendo de Posicion:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Cantidad Surtida:");

        cantidadSurtida.setText("      ");

        positionText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                positionTextKeyPressed(evt);
            }
        });

        skuText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                skuTextKeyPressed(evt);
            }
        });

        orderText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                orderTextKeyPressed(evt);
            }
        });

        cancelarSurtido.setText("Cancelar Surtido");

        jScrollPane2.setViewportView(sellerSKU);

        jScrollPane3.setViewportView(positionName);

        jLabel4.setText("Scanea la posicion.");

        jLabel6.setText("Scanea el producto");

        jLabel7.setText("Scanea la orden");

        jLabel8.setText("Seller:");

        sellerName.setText("      ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(numOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ordenMKTF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(sellerName, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(33, 33, 33)))
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cantidadSurtida, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(72, 72, 72))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(positionText, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(skuText, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(orderText, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                                .addComponent(cancelarSurtido)))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(cantidadSurtida)))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(sellerName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(numOrden))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(ordenMKTF))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(positionText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(skuText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(orderText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelarSurtido))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        tableData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableDataMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableData);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 109, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("", jPanel1);

        jTabbedPane1.addTab("Surtir Pedidos", jTabbedPane2);
        jTabbedPane1.addTab("Ver pedidos", jTabbedPane4);
        jTabbedPane1.addTab("Otro", jTabbedPane3);

        userWelcome.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        userWelcome.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userWelcome.setText("Marketful");

        btnActualizarPedidos.setText("Actualizar Pedidos");
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

        jButton1.setText("Descargar PDF");
        jButton1.setEnabled(false);

        generarPedidos.setText("Generar Pedidos");
        generarPedidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarPedidosActionPerformed(evt);
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
            .addComponent(jTabbedPane1)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnActualizarPedidos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGenerarPdf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userWelcome, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(generarPedidos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userWelcome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnActualizarPedidos)
                        .addComponent(btnGenerarPdf)
                        .addComponent(jButton1)
                        .addComponent(generarPedidos)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnActualizarPedidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarPedidosActionPerformed
        setTable();
        JOptionPane.showMessageDialog(dialogEtiqueta, "Pedidos Actualizados", "Succes", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnActualizarPedidosActionPerformed
    
    
    private void btnGenerarPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarPdfActionPerformed
        this.url_etiquetas = null;
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
            progressBar.setValue(0);
           progressBar.setVisible(true);
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
        if(this.url_etiquetas !=  null){
            new Thread(new Download(this.url_etiquetas, this.out)).start();
        }else{
            JOptionPane.showMessageDialog(dialogEtiqueta, "Ningun Documento PDF Encontrado", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_donwloadPdfActionPerformed

    private void tableDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableDataMouseClicked
        int column = tableData.getColumnModel().getColumnIndexAtX(evt.getX());	
        int row = evt.getY()/tableData.getRowHeight();	
        this.row_active = row;
        int rec = this.tableData.getSelectedRow();
       
//        System.out.println( tableData.getValueAt( rec, tableData.getColumn("# Orden").getModelIndex() ));
//        System.out.println("....");
//        System.out.println(tableData.getValueAt(row,tableData.convertColumnIndexToView(tableData.getColumn("# Orden").getModelIndex())));
        
        
        if(row < tableData.getRowCount() && row >= 0 && column < tableData.getColumnCount() && column >= 0 ){	
            Object value = tableData.getValueAt(row, column);	
            if(value instanceof JButton){	
                ((JButton)value).doClick();	
                JButton boton = (JButton) value;	
                if("calcular".equals(boton.getName())){	
                    System.out.println("Clic en btn calcular row: " + row + ", Column: "+column);	
                }	
            }
            
            if(value instanceof JButton){	
                ((JButton)value).doClick();	
                JButton boton = (JButton) value;	
                if("surtir_pedido".equals(boton.getName())){
                    this.cantidad_surtida = 0;
                    this.positionText.setText("");
                    this.skuText.setText("");
                    this.orderText.setText("");
                    this.positionText.setEditable(false);
                    this.skuText.setEditable(false);
                    this.orderText.setEditable(false);
                    PlaceHolder holder = new PlaceHolder(positionText, "Codigo Posicion");
                    new PlaceHolder(skuText, "SKU/EAN");
                    new PlaceHolder(orderText, "Order MKTF");
                    this.positionText.setEnabled(false);
                    this.skuText.setEnabled(false);
                    this.orderText.setEnabled(false);
                    String seller_name = tableData.getValueAt(row,tableData.convertColumnIndexToView(tableData.getColumn("Seller").getModelIndex())).toString();
                    String num_order = tableData.getValueAt(row,tableData.convertColumnIndexToView(tableData.getColumn("# Orden").getModelIndex())).toString();
                    String order_mktf = tableData.getValueAt(row,tableData.convertColumnIndexToView(tableData.getColumn("Orden Marketful").getModelIndex())).toString();
                    String seller_sku = tableData.getValueAt(row,tableData.convertColumnIndexToView(tableData.getColumn("Seller SKU").getModelIndex())).toString();
                    String posicion = tableData.getValueAt(row,tableData.convertColumnIndexToView(tableData.getColumn("Posicion").getModelIndex())).toString();
                    String cantidad_surtida = tableData.getValueAt(row,tableData.convertColumnIndexToView(tableData.getColumn("Cantidad Anunciada").getModelIndex())).toString();
                    this.cantidad_anunciada = Integer.parseInt(cantidad_surtida);
                    this.numOrden.setText(num_order);
                    this.sellerName.setText(seller_name);
                    this.ordenMKTF.setText(order_mktf);
                    this.sellerSKU.setText(seller_sku);
                    this.cantidadSurtida.setText("0/"+cantidad_surtida);
                    this.positionText.setEnabled(true);
                    this.positionText.setEditable(true);
                    this.positionText.requestFocus(true);
                    this.pedido_id = this.lista.get(row);  
                }else if("calcular".equals(boton.getName())){
                    this.pedido_id = this.lista.get(row);
                    calcularNuevaPosicion(row);
                }	
            }
            
        }	
    }//GEN-LAST:event_tableDataMouseClicked

    private void orderTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orderTextKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            verificarTrackingPedido();
        }
    }//GEN-LAST:event_orderTextKeyPressed

    private void skuTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_skuTextKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            verificarSKUPedido();
        }
    }//GEN-LAST:event_skuTextKeyPressed

    private void positionTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_positionTextKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            verificarPosition();
        }
    }//GEN-LAST:event_positionTextKeyPressed

    private void generarPedidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarPedidosActionPerformed
        this.generarPedidos.setEnabled(false);
        if(this.session_activa){
            String response;
            try {
                response = request.generarPedidos(this.sesion.getSessionToken());
                if("200".equals(response)){
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Pedidos Generados", "", JOptionPane.INFORMATION_MESSAGE);
                    setTable();
                    this.generarPedidos.setEnabled(true);
                }else{
                    JOptionPane.showMessageDialog(dialogEtiqueta, response, "Alerta", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error interno al generar los pedidos.", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            JOptionPane.showMessageDialog(dialogEtiqueta, "Debes iniciar Sesion para generar pedidos", "Alerta", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_generarPedidosActionPerformed
    
    Thread newThread = new Thread(() -> {
        consultarStatusEtiquetas();
    });
    public void calcularNuevaPosicion(Integer row){
        tableData.setValueAt("Calculando...", this.row_active, tableData.convertColumnIndexToView(tableData.getColumn("Calcular").getModelIndex()) );
        String response;
        try {
            response = request.cambiarPosicion(this.pedido_id);
            if("200".equals(response)){
               JOptionPane.showMessageDialog(dialogEtiqueta, "Posicion Actualizada", "Succes", JOptionPane.INFORMATION_MESSAGE);
               setTable();
            }else{
                JOptionPane.showMessageDialog(dialogEtiqueta, "Error al intentar actualizar la posicion", "Alerta", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error interno al actualizar la posicion del pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public Boolean validarCambio(Integer row){
        System.out.println(this.pedido_iniciado);
        String nuevo_pedido = this.lista.get(row);
        System.out.println(nuevo_pedido);
        Boolean diferente_pedido = false;
        if(this.pedido_iniciado != null && !this.pedido_iniciado.equals(nuevo_pedido)){
            System.out.println("Pedido activo");
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Actualemnte estas surtiendo el Pedido de "+this.ordenMKTF.getText()+ "/"+this.sellerSKU.getText()+ " ¿Deseas Cancelar el progreso de este pedido?","Warning",dialogButton);
            if(dialogResult == JOptionPane.YES_OPTION){
                diferente_pedido = true;
            }
            
        }else{
            System.out.println("Ningun pedido activo");
            diferente_pedido = true;
        }
        return diferente_pedido;
    }
    
    public void verificarPosition(){
        String response[];
        try {
            response = request.verificarPositionName(this.positionText.getText(), this.pedido_id);
            if("200".equals(response[0])){
                this.es_consolidado = false;
                if( Integer.parseInt(response[2]) > 0){
                    this.pedido_iniciado = this.pedido_id;
                    this.cantidad_pedido = Integer.parseInt(response[2]);
                    this.positionName.setText(this.positionText.getText());
                    this.positionText.requestFocus(false);
                    this.positionText.setEditable(false);
                    this.positionText.setEnabled(false);
                    this.positionText.setText("");
                    this.skuText.setEnabled(true);
                    this.skuText.setEditable(true);
                    this.skuText.requestFocus(true);
                }else{
                    this.positionText.setText("");
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Este pedido ya fue surtido", "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            }else if("201".equals(response[0])){
                this.es_consolidado = true;
                this.positionText.setText("");
                if( Integer.parseInt(response[2]) > 0){
                    this.sellerSKU.requestFocus();
                }else{
                    this.positionText.setText("");
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Este pedido ya fue surtido", "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            }else{
                this.positionText.setText("");
                this.positionText.requestFocus(true);
                JOptionPane.showMessageDialog(dialogEtiqueta, response[1], "Alerta", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error interno al consultar la posicion.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            this.positionText.setText("");
            this.positionText.requestFocus(true);
        }
    }
    
    
    public void surtirPedido(){
        String response;
        try {
            response = request.surtirPedido(this.cantidad_surtida, this.pedido_id, this.sesion.getSessionToken());
            if("200".equals(response)){
                JOptionPane.showMessageDialog(dialogEtiqueta, "Pedido Completado", "", JOptionPane.INFORMATION_MESSAGE);
                this.orderText.setEnabled(false);
                this.orderText.setEditable(false);
                this.orderText.requestFocus(false);
                finalizarPedido();
            }else if("201".equals(response)){
                JOptionPane.showMessageDialog(dialogEtiqueta, "Esta orden tiene pedidos aun en BackOrders", "Alerta", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(dialogEtiqueta, response, "Alerta", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error interno al surtir el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void finalizarPedido(){
        tableData.setValueAt(this.cantidad_surtida.toString(), this.row_active, tableData.convertColumnIndexToView(tableData.getColumn("Cantidad Surtida").getModelIndex()) );
        tableData.setValueAt("Pedido Completado", this.row_active, tableData.convertColumnIndexToView(tableData.getColumn("Surtir").getModelIndex()) );
        tableData.setValueAt("✔", this.row_active, tableData.convertColumnIndexToView(tableData.getColumn("Calcular").getModelIndex()) );
//        int la_columna2 = tableData.convertColumnIndexToView(tableData.getColumn("Calcular").getModelIndex());
//        tableData.getColumnModel().getColumn(la_columna2).setCellRenderer(c);
//        int la_column1 = tableData.convertColumnIndexToView(tableData.getColumn("Surtir").getModelIndex());
//        tableData.getColumnModel().getColumn(la_column1).setCellRenderer(c);
        for (int i = 0; i <= 12; i++) {
//            int la_column1 = tableData.convertColumnIndexToView(tableData.getColumn("Surtir").getModelIndex());
            tableData.getColumnModel().getColumn(i).setCellRenderer(c);
        }
    }
    
    public void registrarConsolidado(String consolidado_id){
        String response;
        try {
            response = request.registrarConsolidadoApi(consolidado_id, this.pedido_id, this.sesion.getSessionToken());
            if("200".equals(response)){
                JOptionPane.showMessageDialog(dialogEtiqueta, "Pedido Completado", "", JOptionPane.INFORMATION_MESSAGE);
                this.orderText.setEnabled(false);
                this.orderText.setEditable(false);
                this.orderText.requestFocus(false);
                finalizarPedido();
            }else if("201".equals(response)){
                JOptionPane.showMessageDialog(dialogEtiqueta, "Esta orden tiene pedidos aun en BackOrders", "Alerta", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(dialogEtiqueta, response, "Alerta", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error interno al surtir el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void verificarTrackingPedido(){
        String response;
        try {
            if(this.es_consolidado){
                response = request.verificarConsolidado(this.orderText.getText(), this.pedido_id);
                if("200".equals(response)){
                    if(this.session_activa){
                        this.orderText.setText("");
                        registrarConsolidado(this.orderText.getText());
                    }else{
                        this.orderText.setText("");
                        this.orderText.requestFocus(true);
                        JOptionPane.showMessageDialog(dialogEtiqueta, "Debes iniciar Sesion para surtir pedidos", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }
                }else{
                    this.orderText.setText("");
                    this.orderText.requestFocus(true);
                    JOptionPane.showMessageDialog(dialogEtiqueta, response, "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            }else{
                response = request.verificarTrackingPedido(this.orderText.getText(), this.pedido_id);
                if("200".equals(response)){
                    if(this.session_activa){
                        this.orderText.setText("");
                        surtirPedido();
                    }else{
                        this.orderText.setText("");
                        this.orderText.requestFocus(true);
                        JOptionPane.showMessageDialog(dialogEtiqueta, "Debes iniciar Sesion para surtir pedidos", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }
                }else{
                    this.orderText.setText("");
                    this.orderText.requestFocus(true);
                    JOptionPane.showMessageDialog(dialogEtiqueta, response, "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            }
            
        } catch (Exception ex) {
            this.orderText.setText("");
            this.orderText.requestFocus(true);
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 202 - Error interno al surtir el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void verificarSKUPedido(){
        String response[];
        try {
            if(this.es_consolidado){
                 response = request.verificarPedioRuta(this.skuText.getText(), this.pedido_id);
                if("200".equals(response[0])){
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Este Pedido va en la Ruta "+response[1], "", JOptionPane.INFORMATION_MESSAGE);
                    this.cantidad_surtida = this.cantidad_surtida + 1;
                    this.cantidadSurtida.setText(this.cantidad_surtida+"/"+this.cantidad_pedido);
                    if(this.cantidad_surtida == this.cantidad_anunciada){
                        this.skuText.setText("");
                        this.skuText.setEnabled(false);
                        this.skuText.setEditable(false);
                        this.skuText.requestFocus(false);
                    
                        this.orderText.setEnabled(true);
                        this.orderText.setEditable(true);
                        this.orderText.requestFocus(true);
                    }else{
                        this.skuText.setText("");
                    }
                }else{
                    this.skuText.setText("");
                    this.skuText.requestFocus(true);
                    JOptionPane.showMessageDialog(dialogEtiqueta, response[1], "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            }else{
                response = request.verificarSKUPedido(this.skuText.getText(), this.pedido_id);
                if("200".equals(response[0])){
                    this.cantidad_surtida = this.cantidad_surtida + 1;
                    this.cantidadSurtida.setText(this.cantidad_surtida+"/"+this.cantidad_pedido);
                    if(this.cantidad_surtida == this.cantidad_anunciada){
                        this.skuText.setText("");
                        this.skuText.setEnabled(false);
                        this.skuText.setEditable(false);
                        this.skuText.requestFocus(false);
                    
                        this.orderText.setEnabled(true);
                        this.orderText.setEditable(true);
                        this.orderText.requestFocus(true);
                    }else{
                        this.skuText.requestFocus(true);
                        this.skuText.setText("");
                    }
                }else{
                    this.skuText.setText("");
                    this.skuText.requestFocus(true);
                    JOptionPane.showMessageDialog(dialogEtiqueta, response[1], "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            }
           
        } catch (Exception ex) {
            this.skuText.setText("");
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error interno al consultar el sku.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
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
                progressBar.setValue(progreso);
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
            progressBar.setVisible(false);
            this.url_etiquetas = respuesta_pdf;
            new Thread(new Download(this.url_etiquetas, this.out)).start();
//            JOptionPane.showMessageDialog(dialogEtiqueta, "URL de Etiquetas es" + this.url_etiquetas, "Success", JOptionPane.INFORMATION_MESSAGE);
            btnGenerarPdf.setEnabled(true);
            donwloadPdf.setEnabled(true);
        }else{
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 102 - Error al obtener el documento PDF de las etiquetas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(){
                Sesion nS = new Sesion();
                new Pedidos(nS).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarPedidos;
    private javax.swing.JButton btnGenerarPdf;
    private javax.swing.JButton cancelarSurtido;
    private javax.swing.JLabel cantidadSurtida;
    private javax.swing.JDialog dialogEtiqueta;
    private javax.swing.JMenuItem donwloadPdf;
    private javax.swing.JButton generarPedidos;
    private javax.swing.JButton jButton1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JLabel numOrden;
    private javax.swing.JLabel ordenMKTF;
    private javax.swing.JTextField orderText;
    private javax.swing.JTextPane positionName;
    private javax.swing.JTextField positionText;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel sellerName;
    private javax.swing.JTextPane sellerSKU;
    private javax.swing.JMenuItem serrarSesion;
    private javax.swing.JTextField skuText;
    private javax.swing.JTable tableData;
    private javax.swing.JLabel userWelcome;
    // End of variables declaration//GEN-END:variables
}
