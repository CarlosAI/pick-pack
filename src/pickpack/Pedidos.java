
package pickpack;


import com.placeholder.PlaceHolder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    String titlesEnvios[] = {"Seller", "Canal", "Orden Marketful", "No. Orden", "Cliente", "Paqueteria", "No. Guia", "Fecha Surtido", "Status", "Cancelar", "Ver"};
    DefaultTableModel model = new DefaultTableModel();
    DefaultTableModel modelEnvios = new DefaultTableModel();
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
    
    String filtroSeller = "";
    String filtroOrden = "";
    String filtroNumOrden = "";
    String filtroCarrier = "";
    String filtroCanal = "";
    String filtroStatus = "";
    
    ArrayList<String> lista = new ArrayList<>();
    ArrayList<String> listaEnvios = new ArrayList<>();
    Integer totalPagesEnvios = 0;
    Integer currentPage = 1;
    Boolean se_puede_next = false;
    Boolean se_puede_prev = false;
    

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
        setTableEnvios();
//        this.jTabbedPane1.setEnabledAt(1, false);
//        this.menuSec.setEnabledAt(2, false);
//        tableData.setSelectionBackground(java.awt.Color.BLUE);
//        tableData.setSelectionForeground(java.awt.Color.white);
//        int la_columna = tableData.convertColumnIndexToView(tableData.getColumn("Seller").getModelIndex());
//        System.out.println("la columna es" +la_columna);
//        tableData.getColumnModel().getColumn(la_columna).setCellRenderer(c);
//        tableData
//        int la_columna2 = tableData.convertColumnIndexToView(tableData.getColumn("Calcular").getModelIndex());
//        tableData.getColumnModel().getColumn(la_columna2).setCellRenderer(c);
//        int la_column1 = tableData.convertColumnIndexToView(tableData.getColumn("Surtir").getModelIndex());
//        tableData.getColumnModel().getColumn(0).setCellRenderer(c);
//        for (int i = 0; i <= 12; i++) {
//            tableData.getColumnModel().getColumn(i).setCellRenderer(c);
//        }
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
                tableData.getColumnModel().getColumn(i).setCellRenderer(c);
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
    
        public final void setTableEnvios(){
        listaEnvios.clear();
        modelEnvios = new DefaultTableModel(null,titlesEnvios){
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
        
        envioTabla.setModel(modelEnvios);
        envioTabla.setDefaultRenderer(Object.class, new Render());
        envioTabla.setRowHeight(30);
        JButton btn1 = new JButton("Cancelar Guia");
        btn1.setName("cancelar_guia");
        JButton btn2 = new JButton("Ver");
        btn2.setName("ver_envio");
        JButton btn3 = new JButton("Cancelar Guia");
        btn3.setName("cancelar_guia_disabled");
        btn3.setEnabled(false);
        try {
            StringBuilder response = request.getEnvios(this.filtroSeller, this.filtroOrden, this.filtroNumOrden, this.filtroCarrier, this.filtroCanal, this.currentPage, this.filtroStatus);
            JSONObject res = new JSONObject(response.toString());
            JSONArray los_envios = res.getJSONArray("envios");
            System.out.println(los_envios.length());
            System.out.println(los_envios);
            System.out.println("total paginas es "+res.getString("pages"));
            this.totalPagesEnvios = Integer.parseInt(res.getString("pages"));
            if(this.totalPagesEnvios == 0){
                this.currentPage = 0;
            }
            this.totalPages.setText("Pag. "+this.currentPage+"/"+this.totalPagesEnvios);
            if(this.currentPage > 1){
                pagAnterior.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                se_puede_prev = true;
            }else{
                se_puede_prev = false;
                pagAnterior.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            
            if(this.currentPage != this.totalPagesEnvios){
                nextPage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                se_puede_next = true;
            }else{
                se_puede_next = false;
                nextPage.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            
            for (int i = 0; i < los_envios.length(); i++) {
                
                JSONObject elemento = los_envios.getJSONObject(i);
                
                listaEnvios.add(elemento.getString("id"));
                Object row[] = new Object[11];
                row[0] = elemento.getString("nombre_seller");
                row[1] = elemento.getString("site");
                row[2] = elemento.getString("shopi_order_id");
                row[3] = elemento.getString("shopi_order_name");
                
                row[4] = elemento.getString("nombre_cliente");
                if("null".equals(elemento.getString("numero_de_guia"))){
                    row[5] = "";
                }else{
                     row[5] = elemento.getString("carrier_name");
                }
                if( !"ajuste_cancelada".equals(elemento.getString("status")) && !"null".equals(elemento.getString("status")) ){    
                    if("null".equals(elemento.getString("numero_de_guia"))){
                        row[6] = "";
                    }else{
                        row[6] = elemento.getString("numero_de_guia");
                    }
                }else{
                    row[6] = "";
                }
                
                if("null".equals(elemento.getString("numero_de_guia"))){
                    row[7] = "";
                }else{
                    row[7] = elemento.getString("fecha_generada");
                }
                
                row[8] = elemento.getString("status");
                if("guia_generada".equals(elemento.getString("status"))){
                    row[9] = btn1;
                }else{
                    row[9] = btn3;
                }
                
                row[10] = btn2;           
                modelEnvios.addRow(row);
            }
            for (int i = 0; i <= 10; i++) {
                envioTabla.getColumnModel().getColumn(i).setCellRenderer(c);
            }
        } catch (Exception ex) {
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 102 - Error al consultar los Envios.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
//    public void() funcion actualizar posicion de pedido

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogEtiqueta = new javax.swing.JDialog();
        formConsolidado = new javax.swing.JFrame();
        jLabel16 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jFileChooser1 = new javax.swing.JFileChooser();
        formGuiaExistente = new javax.swing.JFrame();
        jLabel24 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jTextField13 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        tiposEnvio = new javax.swing.JComboBox<>();
        formCrearGuiaFedex = new javax.swing.JFrame();
        jLabel31 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jTextField17 = new javax.swing.JTextField();
        jTextField18 = new javax.swing.JTextField();
        jTextField19 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        cotizarGuia = new javax.swing.JButton();
        jTextField22 = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jSeparator3 = new javax.swing.JSeparator();
        jTextField25 = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel44 = new javax.swing.JLabel();
        jTextField26 = new javax.swing.JTextField();
        jTextField27 = new javax.swing.JTextField();
        jTextField28 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        ConfirmarGuia = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel9 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jTextField42 = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jPanel13 = new javax.swing.JPanel();
        jTextField44 = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jTextField45 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jTextField43 = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        jTextField46 = new javax.swing.JTextField();
        jTextField47 = new javax.swing.JTextField();
        jTextField48 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        formCrearGuia = new javax.swing.JFrame();
        jLabel56 = new javax.swing.JLabel();
        jTextField21 = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jTextField30 = new javax.swing.JTextField();
        jTextField31 = new javax.swing.JTextField();
        jTextField32 = new javax.swing.JTextField();
        jTextField33 = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        cotizarGuia1 = new javax.swing.JButton();
        jTextField34 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        jTextField35 = new javax.swing.JTextField();
        jTextField36 = new javax.swing.JTextField();
        jTextField37 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jTextField38 = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel69 = new javax.swing.JLabel();
        jTextField39 = new javax.swing.JTextField();
        jTextField40 = new javax.swing.JTextField();
        jTextField41 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        ConfirmarGuia1 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jSeparator6 = new javax.swing.JSeparator();
        jPanel11 = new javax.swing.JPanel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        dialogConsolidado = new javax.swing.JDialog();
        jLabel38 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jTextField23 = new javax.swing.JTextField();
        jTextField24 = new javax.swing.JTextField();
        jTextField49 = new javax.swing.JTextField();
        jTextField50 = new javax.swing.JTextField();
        jTextField51 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        menuSec = new javax.swing.JTabbedPane();
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
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        statusFiltro = new javax.swing.JComboBox<>();
        sellerFiltro = new javax.swing.JTextField();
        ordenFiltro = new javax.swing.JTextField();
        numordenFiltro = new javax.swing.JTextField();
        paqueteriaFiltro = new javax.swing.JTextField();
        canalFiltro = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        envioTabla = new javax.swing.JTable();
        pagAnterior = new javax.swing.JLabel();
        totalPages = new javax.swing.JLabel();
        nextPage = new javax.swing.JLabel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        paqueteriaBtn = new javax.swing.JButton();
        consolidadoBtn = new javax.swing.JButton();
        willcallBtn = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        ordenMTFInput = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel12 = new javax.swing.JPanel();
        ampmBtn = new javax.swing.JButton();
        paquexBtn = new javax.swing.JButton();
        fedexBtn = new javax.swing.JButton();
        upsBtn = new javax.swing.JButton();
        dhlBtn = new javax.swing.JButton();
        estafetaBtn = new javax.swing.JButton();
        enviaBtn = new javax.swing.JButton();
        tracusaBtn = new javax.swing.JButton();
        crearGuiaBtn = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        guiaExistenteBtn = new javax.swing.JButton();
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

        formConsolidado.setTitle("Formulario Consolidado");
        formConsolidado.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Mandar a Consolidado");

        jLabel17.setText("Posicion Alm Salida.");

        jLabel18.setText("No. Paquete");

        jLabel19.setText("Peso");

        jLabel20.setText("Alto");

        jLabel21.setText("Ancho");

        jLabel22.setText("Largo");

        jButton6.setText("Confirmar");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout formConsolidadoLayout = new javax.swing.GroupLayout(formConsolidado.getContentPane());
        formConsolidado.getContentPane().setLayout(formConsolidadoLayout);
        formConsolidadoLayout.setHorizontalGroup(
            formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(formConsolidadoLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(101, Short.MAX_VALUE))
        );
        formConsolidadoLayout.setVerticalGroup(
            formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formConsolidadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addGap(37, 37, 37)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        formGuiaExistente.setTitle("Formulario Consolidado");
        formGuiaExistente.setResizable(false);

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Guia Existente");

        jTextField8.setEditable(false);
        jTextField8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel25.setText("Paqueteria");

        jTextField9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField10.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField11.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField12.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField13.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel26.setText("No. Guia");

        jLabel27.setText("Peso");

        jLabel28.setText("Alto");

        jLabel29.setText("Ancho");

        jLabel30.setText("Largo");

        jButton7.setText("Confirmar");

        tiposEnvio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout formGuiaExistenteLayout = new javax.swing.GroupLayout(formGuiaExistente.getContentPane());
        formGuiaExistente.getContentPane().setLayout(formGuiaExistenteLayout);
        formGuiaExistenteLayout.setHorizontalGroup(
            formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(formGuiaExistenteLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(formGuiaExistenteLayout.createSequentialGroup()
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(tiposEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        formGuiaExistenteLayout.setVerticalGroup(
            formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formGuiaExistenteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(tiposEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addGap(37, 37, 37)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        formCrearGuiaFedex.setTitle("Formulario Consolidado");
        formCrearGuiaFedex.setResizable(false);

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Crear Guia FedEx ");

        jTextField14.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel32.setText("Compañia/Seller");

        jTextField15.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField16.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField17.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField18.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField19.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel33.setText("Nombre Contacto");

        jLabel34.setText("Direccion 1");

        jLabel35.setText("Direccion 2");

        jLabel36.setText("Codigo Postal");

        jLabel37.setText("Ciudad");

        cotizarGuia.setText("Cotizar Guia");

        jTextField22.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel39.setText("Estado");

        jLabel40.setText("Numero de Telefono");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField25.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel43.setText("Fecha");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel44.setText("Tipo de Servicio");

        jTextField26.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField27.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField28.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel45.setText("Largo");

        jLabel46.setText("Ancho");

        jLabel47.setText("Alto");

        jLabel48.setText("Dimensiones (cm)");

        ConfirmarGuia.setText("Confirmar");

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Datos de la Orden"));

        jLabel49.setText("Fecha");

        jLabel50.setText("Seller");

        jLabel51.setText("Orden");

        jLabel52.setText("Orden MKTF");

        jLabel53.setText("Cliente");

        jTextPane1.setEditable(false);
        jScrollPane5.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel49)
                            .addComponent(jLabel50)
                            .addComponent(jLabel51)
                            .addComponent(jLabel52)
                            .addComponent(jLabel53))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel53)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Resultados de Cotizacion"));

        jLabel54.setText("Costo de la Guia:");

        jLabel55.setText("$4000");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel54)
                .addGap(18, 18, 18)
                .addComponent(jLabel55)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(jLabel55))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jTextField42.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel81.setText("Numero de Paquetes");

        jTextField44.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel84.setText("Alto");

        jTextField45.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel83.setText("Ancho");

        jLabel85.setText("Dimensiones (cm)");

        jTextField43.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel82.setText("Largo");

        jTextField46.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField47.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField48.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel86.setText("Dimensiones (cm)");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel85)
                    .addComponent(jLabel86))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel82)
                        .addGap(50, 50, 50))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField44, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .addComponent(jTextField48))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel83)
                    .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField43, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84)
                    .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82)
                    .addComponent(jLabel83)
                    .addComponent(jLabel84))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField44, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField43, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel85))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86))
                .addContainerGap(115, Short.MAX_VALUE))
        );

        jScrollPane7.setViewportView(jPanel13);

        javax.swing.GroupLayout formCrearGuiaFedexLayout = new javax.swing.GroupLayout(formCrearGuiaFedex.getContentPane());
        formCrearGuiaFedex.getContentPane().setLayout(formCrearGuiaFedexLayout);
        formCrearGuiaFedexLayout.setHorizontalGroup(
            formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cotizarGuia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel32)
                                            .addComponent(jLabel33)
                                            .addComponent(jLabel34)
                                            .addComponent(jLabel35)
                                            .addComponent(jLabel36)
                                            .addComponent(jLabel37)
                                            .addComponent(jLabel39)
                                            .addComponent(jLabel40))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jTextField22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))))
                                    .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel44)
                                            .addComponent(jLabel48)
                                            .addComponent(jLabel43)
                                            .addComponent(jLabel81))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formCrearGuiaFedexLayout.createSequentialGroup()
                                                    .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                                            .addComponent(jLabel45)
                                                            .addGap(50, 50, 50))
                                                        .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                                            .addComponent(jTextField26)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                                    .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel46))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel47)
                                                        .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(ConfirmarGuia, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formCrearGuiaFedexLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        formCrearGuiaFedexLayout.setVerticalGroup(
            formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel32))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel33))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel34))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel35))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel36))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel37)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formCrearGuiaFedexLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel39))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel40))
                                .addGap(27, 27, 27)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel43))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel44))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel81))
                                .addGap(53, 53, 53)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel45)
                                    .addComponent(jLabel46)
                                    .addComponent(jLabel47))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel48))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                                .addComponent(cotizarGuia, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ConfirmarGuia, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(55, 55, 55)
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4)))
                .addContainerGap())
        );

        formCrearGuia.setTitle("Formulario Consolidado");
        formCrearGuia.setResizable(false);

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setText("Crear Guia ");

        jTextField21.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel57.setText("Seller");

        jTextField29.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField30.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField31.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField32.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField33.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel58.setText("Nombre Contacto");

        jLabel59.setText("Calle");

        jLabel60.setText("Colonia");

        jLabel61.setText("Numero Exterior");

        jLabel62.setText("Numero Interior");

        cotizarGuia1.setText("Cotizar Guia");

        jTextField34.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel63.setText("Ciudad");

        jTextField35.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField36.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField37.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel64.setText("Estado");

        jLabel65.setText("Codigo Postal");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel66.setText("Numero de Telefono");

        jLabel67.setText("Email");

        jTextField38.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel68.setText("Peso (KG)");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel69.setText("Tipo de Paquete");

        jTextField39.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField40.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField41.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel70.setText("Largo");

        jLabel71.setText("Ancho");

        jLabel72.setText("Alto");

        jLabel73.setText("Dimensiones (cm)");

        ConfirmarGuia1.setText("Confirmar");

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Datos de la Orden"));

        jLabel74.setText("Fecha");

        jLabel75.setText("Seller");

        jLabel76.setText("Orden");

        jLabel77.setText("Orden MKTF");

        jLabel78.setText("Cliente");

        jTextPane2.setEditable(false);
        jScrollPane6.setViewportView(jTextPane2);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel74)
                            .addComponent(jLabel75)
                            .addComponent(jLabel76)
                            .addComponent(jLabel77)
                            .addComponent(jLabel78))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel74)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel75)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel77)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel78)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Resultados de Cotizacion"));

        jLabel79.setText("Costo de la Guia:");

        jLabel80.setText("$4000");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel79)
                .addGap(18, 18, 18)
                .addComponent(jLabel80)
                .addContainerGap(178, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel79)
                    .addComponent(jLabel80))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout formCrearGuiaLayout = new javax.swing.GroupLayout(formCrearGuia.getContentPane());
        formCrearGuia.getContentPane().setLayout(formCrearGuiaLayout);
        formCrearGuiaLayout.setHorizontalGroup(
            formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cotizarGuia1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel57)
                                    .addComponent(jLabel58)
                                    .addComponent(jLabel59)
                                    .addComponent(jLabel60)
                                    .addComponent(jLabel61)
                                    .addComponent(jLabel62)
                                    .addComponent(jLabel63)
                                    .addComponent(jLabel64)
                                    .addComponent(jLabel65)
                                    .addComponent(jLabel66)
                                    .addComponent(jLabel67))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jComboBox3, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextField35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))))
                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                .addComponent(jLabel68)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel69)
                                    .addComponent(jLabel73))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formCrearGuiaLayout.createSequentialGroup()
                                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                                .addComponent(jTextField39)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                                .addComponent(jLabel70)
                                                .addGap(50, 50, 50)))
                                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField40, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel71))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel72)
                                            .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addComponent(jSeparator5)
                    .addComponent(ConfirmarGuia1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))
                .addGap(7, 7, 7)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        formCrearGuiaLayout.setVerticalGroup(
            formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel57))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel58))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel59))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel60))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel61))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel62)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formCrearGuiaLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel63))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel64)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel65))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel66))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel67))
                                .addGap(26, 26, 26)
                                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel68))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel69))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel70)
                                    .addComponent(jLabel71)
                                    .addComponent(jLabel72))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField39, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField40, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel73))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                .addComponent(cotizarGuia1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ConfirmarGuia1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator6)))
                .addContainerGap())
        );

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Mandar a Consolidado");

        jLabel41.setText("Posicion Alm Salida.");

        jLabel42.setText("No. Paquete");

        jLabel87.setText("Peso");

        jLabel88.setText("Alto");

        jLabel89.setText("Ancho");

        jLabel90.setText("Largo");

        jButton8.setText("Confirmar");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogConsolidadoLayout = new javax.swing.GroupLayout(dialogConsolidado.getContentPane());
        dialogConsolidado.getContentPane().setLayout(dialogConsolidadoLayout);
        dialogConsolidadoLayout.setHorizontalGroup(
            dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(dialogConsolidadoLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel41)
                    .addComponent(jLabel42)
                    .addComponent(jLabel87)
                    .addComponent(jLabel88)
                    .addComponent(jLabel89)
                    .addComponent(jLabel90))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        dialogConsolidadoLayout.setVerticalGroup(
            dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogConsolidadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel87))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel90))
                .addGap(37, 37, 37)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        menuSec.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                menuSecStateChanged(evt);
            }
        });

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
                        .addGap(0, 289, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("", jPanel1);

        menuSec.addTab("Surtir Pedidos", jTabbedPane2);

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        statusFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Pendientes" }));

        paqueteriaFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paqueteriaFiltroActionPerformed(evt);
            }
        });

        jButton2.setText("Buscar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel9.setText("Seller");

        jLabel11.setText("Orden MKTF");

        jLabel13.setText("No. Orden");

        jLabel14.setText("Paqueteria");

        jLabel15.setText("Canal");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sellerFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ordenFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numordenFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paqueteriaFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(canalFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2))
                    .addComponent(jLabel15))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sellerFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ordenFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numordenFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paqueteriaFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(canalFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        envioTabla.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(envioTabla);

        pagAnterior.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        pagAnterior.setForeground(new java.awt.Color(51, 51, 255));
        pagAnterior.setText("Anterior");
        pagAnterior.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pagAnterior.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pagAnteriorMouseClicked(evt);
            }
        });

        totalPages.setText("Pag. 1/50");

        nextPage.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        nextPage.setForeground(new java.awt.Color(0, 51, 255));
        nextPage.setText("Siguiente");
        nextPage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextPageMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 968, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(pagAnterior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalPages)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nextPage)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pagAnterior)
                    .addComponent(totalPages)
                    .addComponent(nextPage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("", jPanel3);

        menuSec.addTab("Envios", jTabbedPane4);

        jTabbedPane3.setToolTipText("Generar una guia de Envio para una orden");

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Selecciona una Opcion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel8.setPreferredSize(new java.awt.Dimension(12, 191));

        paqueteriaBtn.setText("PAQUETERIA");

        consolidadoBtn.setText("CONSOLIDADO");
        consolidadoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consolidadoBtnActionPerformed(evt);
            }
        });

        willcallBtn.setText("WILL CALL");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(paqueteriaBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(consolidadoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(willcallBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(willcallBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(consolidadoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paqueteriaBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(73, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Escanea la Orden", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(12, 191));

        ordenMTFInput.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        ordenMTFInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ordenMTFInputKeyPressed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel23.setText("Orden Actual: ");

        jCheckBox1.setText("Consolidado");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ordenMTFInput, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(ordenMTFInput, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jCheckBox1)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Selecciona una Paqueteria", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        ampmBtn.setText("AMPM");

        paquexBtn.setText("Paquetexpress");

        fedexBtn.setText("FedEx");

        upsBtn.setText("UPS");

        dhlBtn.setText("DHL");

        estafetaBtn.setText("Estafeta");

        enviaBtn.setText("Envia");

        tracusaBtn.setText("Tracusa");

        crearGuiaBtn.setText("Crear Guia");

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        guiaExistenteBtn.setText("Guia Existente");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ampmBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(dhlBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(paquexBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                    .addComponent(estafetaBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fedexBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                    .addComponent(enviaBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(upsBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tracusaBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(crearGuiaBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(guiaExistenteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addComponent(crearGuiaBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(guiaExistenteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ampmBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(paquexBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fedexBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(upsBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dhlBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(estafetaBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(enviaBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tracusaBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addGap(26, 26, 26)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Ordenes", jPanel5);

        menuSec.addTab("Hacer Envios", null, jTabbedPane3, "Generar Guias de Envio.");

        userWelcome.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        userWelcome.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userWelcome.setText("Marketful");

        btnActualizarPedidos.setText("Actualizar Pedidos");
        btnActualizarPedidos.setToolTipText("Actualiza los pedidos actuales (No se generan nuevos pedidos)");
        btnActualizarPedidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarPedidosActionPerformed(evt);
            }
        });

        btnGenerarPdf.setText("Generar Etiquetas");
        btnGenerarPdf.setToolTipText("Genera el PDF de las Etiquetas");
        btnGenerarPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarPdfActionPerformed(evt);
            }
        });

        jButton1.setText("Descargar PDF");
        jButton1.setEnabled(false);

        generarPedidos.setText("Generar Pedidos");
        generarPedidos.setToolTipText("Generar los pedidos pendientes");
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
            .addComponent(menuSec)
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
                .addComponent(menuSec))
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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.currentPage = 1;
        filtroStatus = this.statusFiltro.getSelectedItem().toString();
        if("Pendientes".equals(filtroStatus)){
            filtroStatus = "sin_guia";
        }else{
            filtroStatus = "";
        }
        filtroSeller = this.sellerFiltro.getText();
        filtroOrden = this.ordenFiltro.getText();
        filtroNumOrden = this.numordenFiltro.getText();
        filtroCarrier = this.paqueteriaFiltro.getText();
        filtroCanal = this.canalFiltro.getText();
        
        this.sellerFiltro.setText("");
        this.ordenFiltro.setText("");
        this.numordenFiltro.setText("");
        this.paqueteriaFiltro.setText("");
        this.canalFiltro.setText("");
        
        setTableEnvios();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void paqueteriaFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paqueteriaFiltroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paqueteriaFiltroActionPerformed

    private void nextPageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextPageMouseClicked
        if(se_puede_next){
            nextPage.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            this.currentPage = this.currentPage + 1;
            setTableEnvios();
        }
    }//GEN-LAST:event_nextPageMouseClicked

    private void pagAnteriorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pagAnteriorMouseClicked
        if(se_puede_prev){
            pagAnterior.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            this.currentPage = this.currentPage - 1;
            setTableEnvios();
        }
        
    }//GEN-LAST:event_pagAnteriorMouseClicked

    private void menuSecStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_menuSecStateChanged
        if(this.menuSec.getSelectedIndex() == 2){
            iniciarHacerEnvios();
        }
    }//GEN-LAST:event_menuSecStateChanged

    private void consolidadoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_consolidadoBtnActionPerformed

        this.dialogConsolidado.setTitle("Formulario para Consolidado");
        this.dialogConsolidado.setSize(470, 450);
        this.dialogConsolidado.setLocationRelativeTo(null);
        this.dialogConsolidado.setAlwaysOnTop (true);
        this.dialogConsolidado.setModalityType (ModalityType.APPLICATION_MODAL);
        this.dialogConsolidado.setVisible(true);
        
    }//GEN-LAST:event_consolidadoBtnActionPerformed
    
    private void ordenMTFInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ordenMTFInputKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            verificarOrden();
        }
    }//GEN-LAST:event_ordenMTFInputKeyPressed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed
    
    Thread newThread = new Thread(() -> {
        consultarStatusEtiquetas();
    });
     
    
    public void verificarOrden(){
        this.ordenMTFInput.setText("");
        this.paqueteriaBtn.setEnabled(true);
        this.consolidadoBtn.setEnabled(true);
        this.willcallBtn.setEnabled(true);
    }
    
    public void iniciarHacerEnvios(){
        this.paqueteriaBtn.setEnabled(false);
        this.consolidadoBtn.setEnabled(false);
        this.willcallBtn.setEnabled(false);
        this.ampmBtn.setEnabled(false);
        this.fedexBtn.setEnabled(false);
        this.paquexBtn.setEnabled(false);
        this.enviaBtn.setEnabled(false);
        this.dhlBtn.setEnabled(false);
        this.estafetaBtn.setEnabled(false);
        this.tracusaBtn.setEnabled(false);
        this.upsBtn.setEnabled(false);
        this.crearGuiaBtn.setEnabled(false);
        this.guiaExistenteBtn.setEnabled(false);
        this.ordenMTFInput.requestFocus(true);
    }
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
    private javax.swing.JButton ConfirmarGuia;
    private javax.swing.JButton ConfirmarGuia1;
    private javax.swing.JButton ampmBtn;
    private javax.swing.JButton btnActualizarPedidos;
    private javax.swing.JButton btnGenerarPdf;
    private javax.swing.JTextField canalFiltro;
    private javax.swing.JButton cancelarSurtido;
    private javax.swing.JLabel cantidadSurtida;
    private javax.swing.JButton consolidadoBtn;
    private javax.swing.JButton cotizarGuia;
    private javax.swing.JButton cotizarGuia1;
    private javax.swing.JButton crearGuiaBtn;
    private javax.swing.JButton dhlBtn;
    private javax.swing.JDialog dialogConsolidado;
    private javax.swing.JDialog dialogEtiqueta;
    private javax.swing.JMenuItem donwloadPdf;
    private javax.swing.JButton enviaBtn;
    private javax.swing.JTable envioTabla;
    private javax.swing.JButton estafetaBtn;
    private javax.swing.JButton fedexBtn;
    private javax.swing.JFrame formConsolidado;
    private javax.swing.JFrame formCrearGuia;
    private javax.swing.JFrame formCrearGuiaFedex;
    private javax.swing.JFrame formGuiaExistente;
    private javax.swing.JButton generarPedidos;
    private javax.swing.JButton guiaExistenteBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField38;
    private javax.swing.JTextField jTextField39;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField40;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField43;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JTabbedPane menuSec;
    private javax.swing.JLabel nextPage;
    private javax.swing.JLabel numOrden;
    private javax.swing.JTextField numordenFiltro;
    private javax.swing.JTextField ordenFiltro;
    private javax.swing.JLabel ordenMKTF;
    private javax.swing.JTextField ordenMTFInput;
    private javax.swing.JTextField orderText;
    private javax.swing.JLabel pagAnterior;
    private javax.swing.JButton paqueteriaBtn;
    private javax.swing.JTextField paqueteriaFiltro;
    private javax.swing.JButton paquexBtn;
    private javax.swing.JTextPane positionName;
    private javax.swing.JTextField positionText;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField sellerFiltro;
    private javax.swing.JLabel sellerName;
    private javax.swing.JTextPane sellerSKU;
    private javax.swing.JMenuItem serrarSesion;
    private javax.swing.JTextField skuText;
    private javax.swing.JComboBox<String> statusFiltro;
    private javax.swing.JTable tableData;
    private javax.swing.JComboBox<String> tiposEnvio;
    private javax.swing.JLabel totalPages;
    private javax.swing.JButton tracusaBtn;
    private javax.swing.JButton upsBtn;
    private javax.swing.JLabel userWelcome;
    private javax.swing.JButton willcallBtn;
    // End of variables declaration//GEN-END:variables
}
