
package pickpack;


import com.placeholder.PlaceHolder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pedidos extends javax.swing.JFrame {
    
    String titles[] = {"#","Seller", "Tipo Envio", "Canal", "# Orden", "Orden Marketful", "Descripcion", "Seller SKU", "Cantidad Anunciada", "Cantidad Surtida", "Posicion", "Surtir", "Calcular"};
    String titlesEnvios[] = {"Seller", "Canal", "Orden Marketful", "No. Orden", "Cliente", "Paqueteria", "No. Guia", "Fecha Surtido", "Status", "Cancelar"};
    DefaultTableModel model = new DefaultTableModel();
    DefaultTableModel modelEnvios = new DefaultTableModel();
    File out = new File("Etiquetas/Etiquetas.pdf");
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
    Logger logger = Logger.getLogger("MyLog");
    FileHandler fh;
    
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
    String carrier_nombre_existente = "";
    Boolean candidato_ampm = false;
    Boolean candidato_dostavista = false;
    Boolean paqueteria_diferente = false;
    String el_pais = "sin_pais";
    String order_actual_id = null;
    String listaEstados[] = {"AGUASCALIENTES", "BAJA CALIFORNIA", "BAJA CALIFORNIA SUR", "CAMPECHE", "CHIAPAS", "CHIHUAHUA", "CIUDAD DE MEXICO", "COHAUILA", "COLIMA", "DURANGO", "GUANAJUATO", "GUERRERO", "HIDALGO", "JALISCO", "MEXICO", "MICHOACAN", "MORELOS", "NAYARIT", "NUEVO LEON", "OAXACA", "PUEBLA", "QUERETARO", "QUINTANA ROO", "SAN LUIS POTOSI", "SINALOA", "SONORA", "TABASCO", "TAMAULIPAS", "TLAXCALA", "VERACRUZ", "YUCATAN", "ZACATECAS"};
    String tipo_de_paquete = "";
    String rate_id = "";
    String tipo_envio_actual = "";
    Boolean next_day = false;
    Boolean economico = false;
    Boolean crear_guia_fedex = false;
    String impresoraTermica1 = "";
    String impresoraTermica2 = "";
    String impresoraPDF = "";
    

    public Pedidos(Sesion session) {
        this.sesion = session;
        try {  
            fh = new FileHandler("logs.log");  
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);   
            logger.info("Session Actual en Login");  

        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        logger.info("Session Actual en Login");
        logger.info(sesion.getSessionToken());
        logger.info(sesion.getSessionUserName());
        logger.info(sesion.getSessionUserId().toString());
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
        setTable("","","","");
        setTableEnvios();
        this.dialogEtiqueta.setAlwaysOnTop(true);
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

        ordenMTFInput.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), doHacerEnvios);
        positionText.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), doNombrePosicion);
        skuText.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), doSellerSku);
        orderText.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), doOrdenPaquete);
        
        if(this.sesion.getSessionUserId() == 247){
            this.impresoraTermica1 = "\\\\Laptop-h3109let\\tsc te200";
            this.impresoraTermica2 = "\\\\Laptop-h3109let\\tsc te200";
        }else{
            this.impresoraTermica1 = "TSC TE200";
            this.impresoraTermica2 = "TSC TE200";
        }
    }
    
    Action doHacerEnvios = new AbstractActionImpl(){
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!"".equals(ordenMTFInput.getText().replaceAll("\\s+",""))){
                logger.log(Level.INFO, "Se ingreso la orden {0}", ordenMTFInput.getText());
                iniciarHacerEnvios();
                if(esConsolidadoTextBox.isSelected()){
                    verificarConsolidado();
                }else{
                    verificarOrden(ordenMTFInput.getText().replaceAll("\\s+",""));
                }
            }else{
                JOptionPane.showMessageDialog(dialogEtiqueta, "Ingresa un valor correcto.", "Alerta", JOptionPane.WARNING_MESSAGE);
            }
        }
    };
    
    Action doNombrePosicion = new AbstractActionImpl(){
        @Override
        public void actionPerformed(ActionEvent e) {
            logger.log(Level.INFO, "Se ingreso la posicion {0}", positionText.getText());
            verificarPosition();
        }
    };
    
    Action doSellerSku = new AbstractActionImpl(){
        @Override
        public void actionPerformed(ActionEvent e) {
            logger.log(Level.INFO, "Se ingreso el sku {0}", skuText.getText());
            verificarSKUPedido();
        }
    };
    
    Action doOrdenPaquete = new AbstractActionImpl(){
        @Override
        public void actionPerformed(ActionEvent e) {
            logger.log(Level.INFO, "Se ingreso el paquete {0}", orderText.getText());
            verificarTrackingPedido();
        }
    };
    
    public void setTable(String seller, String seller_sku, String num_order, String orden_mktf){
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
            StringBuilder response = request.getPedidos(seller, seller_sku, num_order, orden_mktf);
            JSONObject res = new JSONObject(response.toString());
            JSONArray los_pedidos = res.getJSONArray("pedidos");
            System.out.println(los_pedidos.length());
            System.out.println(los_pedidos);
            logger.info("Los pedidos son");
            logger.info(los_pedidos.toString());
            
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
    
    public void setTableEnvios(){
        listaEnvios.clear();
        modelEnvios = new DefaultTableModel(null,titlesEnvios){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
//                return false;
            }
        };
        
        envioTabla.setModel(modelEnvios);
        envioTabla.setDefaultRenderer(Object.class, new RenderEnvios());
        envioTabla.setRowHeight(30);
            
        JButton btn3 = new JButton("Cancelar Guia");
        btn3.setName("cancelar_guia_disabled");
        btn3.setEnabled(false);
        try {
            StringBuilder response = request.getEnvios(this.filtroSeller, this.filtroOrden, this.filtroNumOrden, this.filtroCarrier, this.filtroCanal, this.currentPage, this.filtroStatus);
            JSONObject res = new JSONObject(response.toString());
            JSONArray los_envios = res.getJSONArray("envios");
            System.out.println(los_envios.length());
            System.out.println(los_envios);
            logger.info("Los envios son");
            logger.info(los_envios.toString());
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
                Object row[] = new Object[10];
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
                    JButton btnCancelar = new JButton("Cancelar Guia");
                    btnCancelar.setName("cancelar_guia"+elemento.getString("id"));
                    row[9] = btnCancelar;
                }else{
                    row[9] = btn3;
                }       
                modelEnvios.addRow(row);
            }
            for (int i = 0; i <= 9; i++) {
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
        jFileChooser1 = new javax.swing.JFileChooser();
        formConsolidado = new javax.swing.JDialog();
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
        formGuiaExistente = new javax.swing.JDialog();
        jLabel91 = new javax.swing.JLabel();
        carrierNameExistente = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jTextField53 = new javax.swing.JTextField();
        jTextField54 = new javax.swing.JTextField();
        jTextField55 = new javax.swing.JTextField();
        jTextField56 = new javax.swing.JTextField();
        jTextField57 = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        tiposEnvio1 = new javax.swing.JComboBox<>();
        formCrearGuia = new javax.swing.JDialog();
        jLabel98 = new javax.swing.JLabel();
        jTextField58 = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        jTextField59 = new javax.swing.JTextField();
        jTextField60 = new javax.swing.JTextField();
        jTextField61 = new javax.swing.JTextField();
        jTextField62 = new javax.swing.JTextField();
        jTextField63 = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        cotizarGuia2 = new javax.swing.JButton();
        jTextField64 = new javax.swing.JTextField();
        jLabel105 = new javax.swing.JLabel();
        jTextField65 = new javax.swing.JTextField();
        jTextField66 = new javax.swing.JTextField();
        jTextField67 = new javax.swing.JTextField();
        jLabel106 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jTextField68 = new javax.swing.JTextField();
        jLabel110 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jLabel111 = new javax.swing.JLabel();
        jTextField69 = new javax.swing.JTextField();
        jTextField70 = new javax.swing.JTextField();
        jTextField71 = new javax.swing.JTextField();
        jLabel112 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jLabel115 = new javax.swing.JLabel();
        ConfirmarGuia2 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel116 = new javax.swing.JLabel();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        dirTextArea = new javax.swing.JTextArea();
        jSeparator8 = new javax.swing.JSeparator();
        jPanel15 = new javax.swing.JPanel();
        jLabel121 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        formCrearGuiaFedex = new javax.swing.JDialog();
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
        jLabel64 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jSeparator5 = new javax.swing.JSeparator();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel66 = new javax.swing.JLabel();
        jTextField36 = new javax.swing.JTextField();
        jTextField37 = new javax.swing.JTextField();
        jTextField38 = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        ConfirmarGuia1 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jSeparator6 = new javax.swing.JSeparator();
        jPanel11 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jTextField72 = new javax.swing.JTextField();
        kejgbe = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        impresorasConfig = new javax.swing.JDialog();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox<>();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel29 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        cancelar_guia = new javax.swing.JButton();
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
        jPanel7 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
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
        esConsolidadoTextBox = new javax.swing.JCheckBox();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
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
        jMenuItem1 = new javax.swing.JMenuItem();

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

        formConsolidado.setResizable(false);

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Mandar a Consolidado");

        jLabel41.setText("Posicion Alm Salida.");

        jTextField24.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField24KeyTyped(evt);
            }
        });

        jTextField49.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField49KeyTyped(evt);
            }
        });

        jTextField50.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField50KeyTyped(evt);
            }
        });

        jTextField51.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField51KeyTyped(evt);
            }
        });

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

        javax.swing.GroupLayout formConsolidadoLayout = new javax.swing.GroupLayout(formConsolidado.getContentPane());
        formConsolidado.getContentPane().setLayout(formConsolidadoLayout);
        formConsolidadoLayout.setHorizontalGroup(
            formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(formConsolidadoLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel41)
                    .addComponent(jLabel42)
                    .addComponent(jLabel87)
                    .addComponent(jLabel88)
                    .addComponent(jLabel89)
                    .addComponent(jLabel90))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        formConsolidadoLayout.setVerticalGroup(
            formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formConsolidadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel87))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formConsolidadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel90))
                .addGap(37, 37, 37)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        formGuiaExistente.setResizable(false);

        jLabel91.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel91.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel91.setText("Guia Existente");

        carrierNameExistente.setEditable(false);
        carrierNameExistente.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel92.setText("Paqueteria");

        jTextField53.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField54.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField54.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField54KeyTyped(evt);
            }
        });

        jTextField55.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField55.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField55KeyTyped(evt);
            }
        });

        jTextField56.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField56.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField56KeyTyped(evt);
            }
        });

        jTextField57.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField57.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField57KeyTyped(evt);
            }
        });

        jLabel93.setText("No. Guia");

        jLabel94.setText("Peso");

        jLabel95.setText("Alto");

        jLabel96.setText("Ancho");

        jLabel97.setText("Largo");

        jButton9.setText("Confirmar");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout formGuiaExistenteLayout = new javax.swing.GroupLayout(formGuiaExistente.getContentPane());
        formGuiaExistente.getContentPane().setLayout(formGuiaExistenteLayout);
        formGuiaExistenteLayout.setHorizontalGroup(
            formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel91, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(formGuiaExistenteLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel92)
                    .addComponent(jLabel93)
                    .addComponent(jLabel94)
                    .addComponent(jLabel95)
                    .addComponent(jLabel96)
                    .addComponent(jLabel97))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(formGuiaExistenteLayout.createSequentialGroup()
                        .addComponent(carrierNameExistente, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(tiposEnvio1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField53, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField54, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField55, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField56, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField57, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        formGuiaExistenteLayout.setVerticalGroup(
            formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formGuiaExistenteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(carrierNameExistente, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel92)
                    .addComponent(tiposEnvio1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField53, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel93))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField54, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel94))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField55, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel95))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField56, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel96))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGuiaExistenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField57, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel97))
                .addGap(37, 37, 37)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        formCrearGuia.setResizable(false);

        jLabel98.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel98.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel98.setText("Crear Guia ");

        jTextField58.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel99.setText("Seller");

        jTextField59.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField60.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField61.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField62.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField63.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel100.setText("Nombre Contacto");

        jLabel101.setText("Calle");

        jLabel102.setText("Colonia");

        jLabel103.setText("Numero Exterior");

        jLabel104.setText("Numero Interior");

        cotizarGuia2.setText("Cotizar Guia");
        cotizarGuia2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cotizarGuia2ActionPerformed(evt);
            }
        });

        jTextField64.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel105.setText("Ciudad");

        jTextField65.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField65.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField65KeyTyped(evt);
            }
        });

        jTextField66.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField67.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel106.setText("Estado");

        jLabel107.setText("Codigo Postal");

        jLabel108.setText("Numero de Telefono");

        jLabel109.setText("Email");

        jTextField68.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField68.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField68KeyTyped(evt);
            }
        });

        jLabel110.setText("Peso (KG)");

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Otro" }));
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        jLabel111.setText("Tipo de Paquete");

        jTextField69.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField69.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField69KeyTyped(evt);
            }
        });

        jTextField70.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField70.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField70KeyTyped(evt);
            }
        });

        jTextField71.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField71.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField71KeyTyped(evt);
            }
        });

        jLabel112.setText("Largo");

        jLabel113.setText("Ancho");

        jLabel114.setText("Alto");

        jLabel115.setText("Dimensiones (cm)");

        ConfirmarGuia2.setText("Crear Guia");
        ConfirmarGuia2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConfirmarGuia2ActionPerformed(evt);
            }
        });

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Datos de la Orden"));

        jLabel116.setText("Fecha");

        jLabel117.setText("Seller");

        jLabel118.setText("Orden");

        jLabel119.setText("Orden MKTF");

        jLabel120.setText("Cliente");

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 0, 0));
        jLabel18.setText("   ");

        dirTextArea.setColumns(20);
        dirTextArea.setRows(5);
        jScrollPane5.setViewportView(dirTextArea);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addGap(170, 170, 170)
                        .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel116)
                            .addComponent(jLabel117)
                            .addComponent(jLabel118)
                            .addComponent(jLabel119)
                            .addComponent(jLabel120)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 14, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel116)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel117)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel118)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel119)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel120)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Resultados de Cotizacion"));

        jLabel121.setText("Costo de la Guia:");
        jLabel121.setToolTipText("Costo final que tendra la Guia");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel121, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel121, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        jCheckBox1.setText("Asegurar");

        javax.swing.GroupLayout formCrearGuiaLayout = new javax.swing.GroupLayout(formCrearGuia.getContentPane());
        formCrearGuia.getContentPane().setLayout(formCrearGuiaLayout);
        formCrearGuiaLayout.setHorizontalGroup(
            formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel98, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel99)
                                            .addComponent(jLabel100)
                                            .addComponent(jLabel101)
                                            .addComponent(jLabel102)
                                            .addComponent(jLabel103)
                                            .addComponent(jLabel104)
                                            .addComponent(jLabel105)
                                            .addComponent(jLabel106)
                                            .addComponent(jLabel107)
                                            .addComponent(jLabel108)
                                            .addComponent(jLabel109))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField58, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField59, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField60, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField61, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField63, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField64, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField66, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField67, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(jComboBox5, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jTextField65, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))))
                                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                        .addComponent(jLabel110)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextField68, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel111)
                                            .addComponent(jLabel115))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formCrearGuiaLayout.createSequentialGroup()
                                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                                        .addComponent(jTextField69)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                                        .addComponent(jLabel112)
                                                        .addGap(50, 50, 50)))
                                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jTextField70, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel113))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel114)
                                                    .addComponent(jTextField71, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(jCheckBox1)))))
                            .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(cotizarGuia2, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(ConfirmarGuia2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        formCrearGuiaLayout.setVerticalGroup(
            formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator8))
                    .addGroup(formCrearGuiaLayout.createSequentialGroup()
                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField58, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel99))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField59, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel100))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField60, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel101))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField61, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel102))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel103))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField63, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel104))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField64, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel105))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel106)
                                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField65, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel107))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField66, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel108))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField67, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel109))
                                .addGap(26, 26, 26)
                                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField68, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel110))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel111))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel112)
                                    .addComponent(jLabel113)
                                    .addComponent(jLabel114))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField69, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField70, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField71, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel115))
                                .addGap(18, 18, 18)
                                .addComponent(jCheckBox1))
                            .addGroup(formCrearGuiaLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(formCrearGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cotizarGuia2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ConfirmarGuia2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 52, Short.MAX_VALUE)))
                .addContainerGap())
        );

        formCrearGuiaFedex.setResizable(false);

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setText("Crear Guia FedEx ");

        jTextField21.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel57.setText("Compañia/Seller");

        jTextField29.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField30.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField30.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField30KeyTyped(evt);
            }
        });

        jTextField31.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField31.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField31KeyTyped(evt);
            }
        });

        jTextField32.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField32.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField32KeyTyped(evt);
            }
        });

        jTextField33.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel58.setText("Nombre Contacto");

        jLabel59.setText("Direccion 1");

        jLabel60.setText("Direccion 2");

        jLabel61.setText("Codigo Postal");

        jLabel62.setText("Ciudad");

        cotizarGuia1.setText("Cotizar Guia");
        cotizarGuia1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cotizarGuia1ActionPerformed(evt);
            }
        });

        jTextField34.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel63.setText("Estado");

        jLabel64.setText("Numero de Telefono");

        jLabel66.setText("Tipo de Servicio");

        jTextField36.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField36.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField36KeyTyped(evt);
            }
        });

        jTextField37.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField37.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField37KeyTyped(evt);
            }
        });

        jTextField38.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField38.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField38KeyTyped(evt);
            }
        });

        jLabel67.setText("Largo");

        jLabel68.setText("Ancho");

        jLabel69.setText("Alto");

        jLabel70.setText("Dimensiones (cm)");

        ConfirmarGuia1.setText("Confirmar");
        ConfirmarGuia1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConfirmarGuia1ActionPerformed(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Datos de la Orden"));

        jLabel71.setText("Fecha");

        jLabel72.setText("Seller");

        jLabel73.setText("Orden");

        jLabel74.setText("Orden MKTF");

        jLabel75.setText("Cliente");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane7.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel71)
                            .addComponent(jLabel72)
                            .addComponent(jLabel73)
                            .addComponent(jLabel74)
                            .addComponent(jLabel75))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel71)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel72)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel73)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel74)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel75)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Resultados de Cotizacion"));

        jLabel76.setText("Costo de la Guia:");

        jLabel77.setText("  ");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel76)
                .addGap(18, 18, 18)
                .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76)
                    .addComponent(jLabel77))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jTextField72.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextField72.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField72KeyTyped(evt);
            }
        });

        kejgbe.setText("Peso");

        jLabel19.setText("Tipo de Paquete");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Otro" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout formCrearGuiaFedexLayout = new javax.swing.GroupLayout(formCrearGuiaFedex.getContentPane());
        formCrearGuiaFedex.getContentPane().setLayout(formCrearGuiaFedexLayout);
        formCrearGuiaFedexLayout.setHorizontalGroup(
            formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel57)
                                    .addComponent(jLabel58)
                                    .addComponent(jLabel59)
                                    .addComponent(jLabel60)
                                    .addComponent(jLabel61)
                                    .addComponent(jLabel62)
                                    .addComponent(jLabel63)
                                    .addComponent(jLabel64))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jComboBox3, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextField34, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))))
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel66)
                                    .addComponent(jLabel70)
                                    .addComponent(kejgbe)
                                    .addComponent(jLabel19))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formCrearGuiaFedexLayout.createSequentialGroup()
                                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                                .addComponent(jLabel67)
                                                .addGap(50, 50, 50))
                                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                                .addComponent(jTextField36)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel68))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel69)
                                            .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jTextField72)
                                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addComponent(cotizarGuia1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9)
                                .addComponent(ConfirmarGuia1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(18, 18, 18)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        formCrearGuiaFedexLayout.setVerticalGroup(
            formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel57))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel58))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel59))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel60))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel61))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel62)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formCrearGuiaFedexLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel63))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel64))
                                .addGap(27, 27, 27)
                                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel66))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField72, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kejgbe))
                                .addGap(18, 18, 18)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel67)
                                    .addComponent(jLabel68)
                                    .addComponent(jLabel69))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel70))
                                .addGap(18, 18, 18)
                                .addGroup(formCrearGuiaFedexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cotizarGuia1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ConfirmarGuia1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(formCrearGuiaFedexLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)))
                .addContainerGap())
        );

        impresorasConfig.setTitle("Configurar Impresoras");
        impresorasConfig.setResizable(false);

        jLabel25.setText("Guias Termicas 4x6 ");

        jLabel26.setText("(Paquetexpress, AMPM, Fedex)");

        jLabel27.setText("Guais Termicas 4x8");

        jLabel28.setText("Mercadolibre");

        jLabel29.setText("Etiquetas Pedidos");

        jButton4.setText("Guardar Cambios");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Cancelar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout impresorasConfigLayout = new javax.swing.GroupLayout(impresorasConfig.getContentPane());
        impresorasConfig.getContentPane().setLayout(impresorasConfigLayout);
        impresorasConfigLayout.setHorizontalGroup(
            impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(impresorasConfigLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(impresorasConfigLayout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(impresorasConfigLayout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(impresorasConfigLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, impresorasConfigLayout.createSequentialGroup()
                        .addGroup(impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator3)
                            .addGroup(impresorasConfigLayout.createSequentialGroup()
                                .addGroup(impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25)
                                    .addComponent(jLabel26))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                                .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(36, 36, 36))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, impresorasConfigLayout.createSequentialGroup()
                        .addGroup(impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator4)
                            .addGroup(impresorasConfigLayout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(38, 38, 38))))
        );
        impresorasConfigLayout.setVerticalGroup(
            impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(impresorasConfigLayout.createSequentialGroup()
                .addGroup(impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(impresorasConfigLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, impresorasConfigLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)))
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(impresorasConfigLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel29)
                            .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 109, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, impresorasConfigLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(impresorasConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5)
                            .addComponent(jButton4))
                        .addContainerGap())))
        );

        cancelar_guia.setText("jButton6");

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

        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel20.setText("Seller");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel21.setText("Seller SKU");

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel22.setText("Numero de Orden");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel24.setText("Orden MKTF");

        jButton3.setText("Buscar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3))
                    .addComponent(jLabel24))
                .addContainerGap(116, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap(26, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(26, 26, 26)))
                .addGap(35, 35, 35))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(289, 289, 289)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
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
        envioTabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                envioTablaMouseClicked(evt);
            }
        });
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
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("", jPanel3);

        menuSec.addTab("Envios", jTabbedPane4);

        jTabbedPane3.setToolTipText("Generar una guia de Envio para una orden");

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Selecciona una Opcion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel8.setPreferredSize(new java.awt.Dimension(12, 191));

        paqueteriaBtn.setText("PAQUETERIA");
        paqueteriaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paqueteriaBtnActionPerformed(evt);
            }
        });

        consolidadoBtn.setText("CONSOLIDADO");
        consolidadoBtn.setEnabled(false);
        consolidadoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consolidadoBtnActionPerformed(evt);
            }
        });

        willcallBtn.setText("WILL CALL");
        willcallBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                willcallBtnActionPerformed(evt);
            }
        });

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
                .addContainerGap(55, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(willcallBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(consolidadoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paqueteriaBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Escanea la Orden", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(12, 191));

        ordenMTFInput.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel23.setText(" ");

        esConsolidadoTextBox.setText("Consolidado");
        esConsolidadoTextBox.setEnabled(false);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setText("  ");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setText("   ");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ordenMTFInput, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(143, 143, 143)
                        .addComponent(esConsolidadoTextBox)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ordenMTFInput, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(esConsolidadoTextBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Selecciona una Paqueteria", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        ampmBtn.setText("AMPM");
        ampmBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ampmBtnActionPerformed(evt);
            }
        });

        paquexBtn.setText("Paquetexpress");
        paquexBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paquexBtnActionPerformed(evt);
            }
        });

        fedexBtn.setText("FedEx");
        fedexBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fedexBtnActionPerformed(evt);
            }
        });

        upsBtn.setText("UPS");
        upsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upsBtnActionPerformed(evt);
            }
        });

        dhlBtn.setText("DHL");
        dhlBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dhlBtnActionPerformed(evt);
            }
        });

        estafetaBtn.setText("Estafeta");
        estafetaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estafetaBtnActionPerformed(evt);
            }
        });

        enviaBtn.setText("Envia");
        enviaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviaBtnActionPerformed(evt);
            }
        });

        tracusaBtn.setText("Tracusa");
        tracusaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tracusaBtnActionPerformed(evt);
            }
        });

        crearGuiaBtn.setText("Crear Guia");
        crearGuiaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crearGuiaBtnActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        guiaExistenteBtn.setText("Guia Existente");
        guiaExistenteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiaExistenteBtnActionPerformed(evt);
            }
        });

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
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
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addGap(26, 26, 26)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Ordenes", jPanel5);

        menuSec.addTab("Hacer Envios", null, jTabbedPane3, "Generar Guias de Envio.");

        userWelcome.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        userWelcome.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userWelcome.setText("Marketful");

        btnActualizarPedidos.setText("Actualizar Tabla");
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

        jButton1.setText("Imprimir Etiquetas");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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
        donwloadPdf.setEnabled(false);
        donwloadPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                donwloadPdfActionPerformed(evt);
            }
        });
        jMenu3.add(donwloadPdf);

        jMenu2.add(jMenu3);

        jMenuItem1.setText("Configuracion impresoras");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuSec, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnActualizarPedidos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGenerarPdf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generarPedidos)
                .addGap(183, 183, 183)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        setTable("","","","");
        JOptionPane.showMessageDialog(dialogEtiqueta, "Pedidos Actualizados", "Succes", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnActualizarPedidosActionPerformed
    
    
    private void btnGenerarPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarPdfActionPerformed
        if (JOptionPane.showConfirmDialog(dialogEtiqueta, "¿Generar las Etiquetas?", "WARNING",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            logger.info("Vamos a generar las etiquetas");
            this.url_etiquetas = null;
            btnGenerarPdf.setEnabled(false);
            int respuestaEtiqueta = 500;
            try {
                respuestaEtiqueta = request.setGenerarPedidos();
            } catch (Exception ex) {
                logger.log(Level.INFO, "Error al generar las etiquetas fue {0}", respuestaEtiqueta);
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 100 - Error al generar las etiquetas", "Error", JOptionPane.ERROR_MESSAGE);
            }

            if(respuestaEtiqueta != 200){
                logger.log(Level.INFO, "Error al generar las etiquetas fue {0}", respuestaEtiqueta);
                JOptionPane.showMessageDialog(dialogEtiqueta, "Ocurrio un Error al intentar Generar las Etiquetas, intentalo de nuevo", "Error", JOptionPane.ERROR_MESSAGE);
            }else{
               logger.info("Vamos a consultar las etiquetas");
               progressBar.setValue(0);
               progressBar.setVisible(true);

               Thread newThreadEtiquetas = new Thread(() -> {
                    consultarStatusEtiquetas();
               });
               newThreadEtiquetas.start();
            }
        } else {
            logger.info("Dimos que NO en generar etiquetas");
            System.out.println("Sin generar");
        }
        
    }//GEN-LAST:event_btnGenerarPdfActionPerformed

    private void donwloadPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_donwloadPdfActionPerformed
        // TODO add your handling code here:
        logger.info("Vamos a descargar las etiquetas");
        if(this.url_etiquetas !=  null){
            Download desc = new Download(this.url_etiquetas, this.out);
            desc.run();
        }else{
            logger.info("No se encontro el pdf de las etiquetas");
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
                    logger.log(Level.INFO, "Clic en btn calcular row: {0}, Column: {1}", new Object[]{row, column});
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

    private void generarPedidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarPedidosActionPerformed
        this.generarPedidos.setEnabled(false);
        logger.info("Click en Vamos a generar los pedidos");
        if(this.session_activa){
            String response;
            try {
                response = request.generarPedidos(this.sesion.getSessionToken());
                if("200".equals(response)){
                    logger.info("Pedidos generados");
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Pedidos Generados", "", JOptionPane.INFORMATION_MESSAGE);
                    setTable("","","","");
                    this.generarPedidos.setEnabled(true);
                }else{
                    logger.log(Level.INFO, "Error al generar los pedidos {0}", response);
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
        logger.log(Level.INFO, "Vamos a mandatr a consolidado la orden {0}", this.order_actual_id);
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
        
        verificarGuiaPrepagada("Marketful");
        this.ordenMTFInput.requestFocus(true);
        this.formConsolidado.setTitle("Formulario para Consolidado");
        this.formConsolidado.setSize(470, 450);
        this.formConsolidado.setLocationRelativeTo(null);
        this.formConsolidado.setAlwaysOnTop (true);
        this.formConsolidado.setModalityType (ModalityType.APPLICATION_MODAL);
        this.formConsolidado.setVisible(true);
    }//GEN-LAST:event_consolidadoBtnActionPerformed
    
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void paqueteriaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paqueteriaBtnActionPerformed
        logger.log(Level.INFO, "Click en paqueteria para la orden {0}", this.order_actual_id);
        if(this.candidato_ampm){
            this.ampmBtn.setEnabled(true);
        }
        this.fedexBtn.setEnabled(true);
        this.paquexBtn.setEnabled(true);
        this.enviaBtn.setEnabled(true);
        this.dhlBtn.setEnabled(true);
        this.estafetaBtn.setEnabled(true);
        this.tracusaBtn.setEnabled(true);
        this.upsBtn.setEnabled(true);
    }//GEN-LAST:event_paqueteriaBtnActionPerformed

    private void ampmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ampmBtnActionPerformed
        logger.log(Level.INFO, "Click en AMPM para la orden {0}", this.order_actual_id);
        enabledPaquex("AMPM");
        if(!"sin_pais".equals(this.el_pais) && !"mexico".equals(this.el_pais)){
            JOptionPane.showMessageDialog(dialogEtiqueta, "No puedes crear una guia de Envio para un Envio Internacional", "Alerta", JOptionPane.WARNING_MESSAGE);
        }else{
            this.carrier_nombre_existente = "AMPM";
            verificarGuiaPrepagada("AMPM");
            consultarDireccion("AMPM");
            consultaPaquetes();
            this.formCrearGuia.setTitle("Generar Guia AMPM");
            this.jLabel98.setText("Crear Guia AMPM");
            this.formCrearGuia.setSize(880, 815);
            this.formCrearGuia.setLocationRelativeTo(null);
            this.formCrearGuia.setAlwaysOnTop (true);
            this.formCrearGuia.setModalityType (ModalityType.APPLICATION_MODAL);
            this.formCrearGuia.setVisible(true);
            this.crearGuiaBtn.setEnabled(false);
            this.guiaExistenteBtn.setEnabled(false);
        }
        
    }//GEN-LAST:event_ampmBtnActionPerformed

    private void paquexBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paquexBtnActionPerformed
        logger.log(Level.INFO, "Click en Paquetexpress para la orden {0}", this.order_actual_id);
        verificarGuiaPrepagada("Paquetexpress");
        this.carrier_nombre_existente = "Paquetexpress";
        if(!esConsolidadoTextBox.isSelected()){
            this.crearGuiaBtn.setEnabled(true);
        }
        this.guiaExistenteBtn.setEnabled(true);
    }//GEN-LAST:event_paquexBtnActionPerformed

    private void fedexBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fedexBtnActionPerformed
        logger.log(Level.INFO, "Click en Fedex para la orden {0}", this.order_actual_id);
        verificarGuiaPrepagada("FedEx");
        this.carrier_nombre_existente = "FedEx";
        if(!esConsolidadoTextBox.isSelected()){
            this.crearGuiaBtn.setEnabled(true);
        }
        this.guiaExistenteBtn.setEnabled(true);
    }//GEN-LAST:event_fedexBtnActionPerformed

    private void upsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upsBtnActionPerformed
        logger.log(Level.INFO, "Click en UPS para la orden {0}", this.order_actual_id);
        verificarGuiaPrepagada("UPS");
        consultaRates("UPS");
        this.carrierNameExistente.setText("UPS");
        this.formGuiaExistente.setTitle("Guia Existente UPS");
        this.formGuiaExistente.setSize(560, 412);
        this.formGuiaExistente.setLocationRelativeTo(null);
        this.formGuiaExistente.setAlwaysOnTop (true);
//        this.formGuiaExistente.setModalityType (ModalityType.APPLICATION_MODAL);
        this.formGuiaExistente.setVisible(true);
        jButton9.setEnabled(true);
        this.crearGuiaBtn.setEnabled(false);
        this.guiaExistenteBtn.setEnabled(false);
    }//GEN-LAST:event_upsBtnActionPerformed

    private void dhlBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dhlBtnActionPerformed
        logger.log(Level.INFO, "Click en DHL para la orden {0}", this.order_actual_id);
        verificarGuiaPrepagada("DHL");
        consultaRates("DHL");
        this.carrierNameExistente.setText("DHL");
        this.formGuiaExistente.setTitle("Guia Existente DHL");
        this.formGuiaExistente.setSize(560, 412);
        this.formGuiaExistente.setLocationRelativeTo(null);
        this.formGuiaExistente.setAlwaysOnTop (true);
        this.formGuiaExistente.setModalityType (ModalityType.APPLICATION_MODAL);
        this.formGuiaExistente.setVisible(true);
        jButton9.setEnabled(true);
        this.crearGuiaBtn.setEnabled(false);
        this.guiaExistenteBtn.setEnabled(false);
    }//GEN-LAST:event_dhlBtnActionPerformed

    private void estafetaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estafetaBtnActionPerformed
        logger.log(Level.INFO, "Click en Estafeta para la orden {0}", this.order_actual_id);
        verificarGuiaPrepagada("Estafeta");
        this.carrier_nombre_existente = "Estafeta";
        if(!esConsolidadoTextBox.isSelected()){
            this.crearGuiaBtn.setEnabled(true);
        }
        this.guiaExistenteBtn.setEnabled(true);
    }//GEN-LAST:event_estafetaBtnActionPerformed

    private void enviaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviaBtnActionPerformed
        verificarGuiaPrepagada("Envia");
        consultaRates("Envia");
        this.carrierNameExistente.setText("Envia");
        this.formGuiaExistente.setTitle("Guia Existente Envia");
        this.formGuiaExistente.setSize(560, 412);
        this.formGuiaExistente.setLocationRelativeTo(null);
        this.formGuiaExistente.setAlwaysOnTop (true);
        this.formGuiaExistente.setModalityType (ModalityType.APPLICATION_MODAL);
        this.formGuiaExistente.setVisible(true);
        jButton9.setEnabled(true);
        this.crearGuiaBtn.setEnabled(false);
        this.guiaExistenteBtn.setEnabled(false);
    }//GEN-LAST:event_enviaBtnActionPerformed

    private void tracusaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tracusaBtnActionPerformed
        verificarGuiaPrepagada("Tracusa");
        consultaRates("Tracusa");
        this.carrierNameExistente.setText("Tracusa");
        this.formGuiaExistente.setTitle("Guia Existente Tracusa");
        this.formGuiaExistente.setSize(560, 412);
        this.formGuiaExistente.setLocationRelativeTo(null);
        this.formGuiaExistente.setAlwaysOnTop (true);
        this.formGuiaExistente.setModalityType (ModalityType.APPLICATION_MODAL);
        this.formGuiaExistente.setVisible(true);
        jButton9.setEnabled(true);
        this.crearGuiaBtn.setEnabled(false);
        this.guiaExistenteBtn.setEnabled(false);
    }//GEN-LAST:event_tracusaBtnActionPerformed

    private void crearGuiaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crearGuiaBtnActionPerformed
        if(!"sin_pais".equals(this.el_pais) && !"mexico".equals(this.el_pais)){
            JOptionPane.showMessageDialog(dialogEtiqueta, "No puedes crear una guia de Envio para un Envio Internacional", "Alerta", JOptionPane.WARNING_MESSAGE);
        }else{
            if("FedEx".equals(this.carrier_nombre_existente)){
                eneabledFedex();
                consultarDireccion("FedEx");
                consultaPaquetes();
                this.crear_guia_fedex = true;
                consultaRates("FedEx");
                this.formCrearGuiaFedex.setTitle("Generar Guia Fedex");
                jLabel56.setText("Crear Guia FedEx");
                this.formCrearGuiaFedex.setSize(900, 815);
                this.formCrearGuiaFedex.setLocationRelativeTo(null);
                this.formCrearGuiaFedex.setAlwaysOnTop (true);
                this.formCrearGuiaFedex.setModalityType (ModalityType.APPLICATION_MODAL);
                this.formCrearGuiaFedex.setVisible(true);
                this.tipo_de_paquete = "YOUR_PACKAGING";
            }
            System.out.println("La carrier presente es "+this.carrier_nombre_existente);
            if("Estafeta".equals(this.carrier_nombre_existente)){
                eneabledFedex();
                consultarDireccion("FedEx");
                consultaPaquetes();
                this.crear_guia_fedex = true;
                consultaRates("FedEx");
                this.formCrearGuiaFedex.setTitle("Generar Guia Estafeta");
                jLabel56.setText("Crear Guia Estafeta");
                this.formCrearGuiaFedex.setSize(900, 815);
                this.formCrearGuiaFedex.setLocationRelativeTo(null);
                this.formCrearGuiaFedex.setAlwaysOnTop (true);
                this.formCrearGuiaFedex.setModalityType (ModalityType.APPLICATION_MODAL);
                this.formCrearGuiaFedex.setVisible(true);
                this.tipo_de_paquete = "YOUR_PACKAGING";
            }
        
            if("Paquetexpress".equals(this.carrier_nombre_existente)){
                enabledPaquex("paquex");
                consultarDireccion("Paquetexpress");
                consultaPaquetes();
                consultaRates("Paquetexpress");
                this.formCrearGuia.setTitle("Generar Guia Paquetexpress");
                this.jLabel98.setText("Crear Guia Paquetexpress");
                this.formCrearGuia.setSize(880, 815);
                this.formCrearGuia.setLocationRelativeTo(null);
                this.formCrearGuia.setAlwaysOnTop (true);
                this.formCrearGuia.setModalityType (ModalityType.APPLICATION_MODAL);
                this.formCrearGuia.setVisible(true);
            }
        }

    }//GEN-LAST:event_crearGuiaBtnActionPerformed

    private void guiaExistenteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiaExistenteBtnActionPerformed
        
        if("FedEx".equals(this.carrier_nombre_existente)){
            consultaRates("FedEx");
            this.tipo_de_paquete = "YOUR_PACKAGING";
            this.carrierNameExistente.setText("FedEx");
            this.formGuiaExistente.setTitle("Guia Existente FedEx");
            this.formGuiaExistente.setSize(560, 412);
            this.formGuiaExistente.setLocationRelativeTo(null);
            this.formGuiaExistente.setAlwaysOnTop (true);
            this.formGuiaExistente.setModalityType (ModalityType.APPLICATION_MODAL);
            this.formGuiaExistente.setVisible(true);
            jButton9.setEnabled(true);
        }
        
        if("Paquetexpress".equals(this.carrier_nombre_existente)){
            consultaRates("Paquetexpress");
            this.tipo_de_paquete = "ENVELOP";
            this.carrierNameExistente.setText("Paquetexpress");
            this.formGuiaExistente.setTitle("Guia Existente Paquetexpress");
            this.formGuiaExistente.setSize(560, 412);
            this.formGuiaExistente.setLocationRelativeTo(null);
            this.formGuiaExistente.setAlwaysOnTop (true);
            this.formGuiaExistente.setModalityType (ModalityType.APPLICATION_MODAL);
            this.formGuiaExistente.setVisible(true);
            jButton9.setEnabled(true);
        }
        
        if("Estafeta".equals(this.carrier_nombre_existente)){
            consultaRates("Estafeta");
            this.carrierNameExistente.setText("Estafeta");
            this.formGuiaExistente.setTitle("Guia Existente Estafeta");
            this.formGuiaExistente.setSize(560, 412);
            this.formGuiaExistente.setLocationRelativeTo(null);
            this.formGuiaExistente.setAlwaysOnTop (true);
            this.formGuiaExistente.setModalityType (ModalityType.APPLICATION_MODAL);
            this.formGuiaExistente.setVisible(true);
            jButton9.setEnabled(true);
        }
    }//GEN-LAST:event_guiaExistenteBtnActionPerformed

    private void jTextField68KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField68KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField68KeyTyped

    private void jTextField69KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField69KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField69KeyTyped

    private void jTextField70KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField70KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField70KeyTyped

    private void jTextField71KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField71KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField71KeyTyped

    private void jTextField36KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField36KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField36KeyTyped

    private void jTextField37KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField37KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField37KeyTyped

    private void jTextField38KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField38KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField38KeyTyped

    private void jTextField54KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField54KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField54KeyTyped

    private void jTextField55KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField55KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField55KeyTyped

    private void jTextField56KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField56KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField56KeyTyped

    private void jTextField57KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField57KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField57KeyTyped

    private void jTextField24KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField24KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField24KeyTyped

    private void jTextField49KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField49KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField49KeyTyped

    private void jTextField50KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField50KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField50KeyTyped

    private void jTextField51KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField51KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField51KeyTyped

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        String tipo_box = jComboBox6.getSelectedItem().toString();
        System.out.println("Selected es "+tipo_box);
        if(tipo_box.equals("Otro")){
            jTextField69.setText("");
            jTextField70.setText("");
            jTextField71.setText("");
        }else{
            try {
                StringBuilder response = request.consultarBox(tipo_box);
                JSONObject res = new JSONObject(response.toString());
                JSONArray boxes = res.getJSONArray("result");
                String largo = boxes.get(2).toString();
                String ancho = boxes.get(3).toString();
                String alto = boxes.get(4).toString();
                jTextField69.setText(largo);
                jTextField70.setText(ancho);
                jTextField71.setText(alto);
                String tipo_paq = boxes.get(1).toString();
                if(tipo_paq.equals("Sobre")){
                    this.tipo_de_paquete = "ENVELOP";
                }else{
                    this.tipo_de_paquete = "PACKETS";
                }
            } catch (Exception ex) {
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        String tipo_box = jComboBox1.getSelectedItem().toString();
        System.out.println("Selected es "+tipo_box);
        if(tipo_box.equals("Otro")){
            jTextField36.setText("");
            jTextField37.setText("");
            jTextField38.setText("");
        }else{
            try {
                StringBuilder response = request.consultarBox(tipo_box);
                JSONObject res = new JSONObject(response.toString());
                JSONArray boxes = res.getJSONArray("result");
                String largo = boxes.get(2).toString();
                String ancho = boxes.get(3).toString();
                String alto = boxes.get(4).toString();
                jTextField36.setText(largo);
                jTextField37.setText(ancho);
                jTextField38.setText(alto);
                String tipo_paq = boxes.get(1).toString();
            } catch (Exception ex) {
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void cotizarGuia2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cotizarGuia2ActionPerformed
        cotizarGuia2.setEnabled(false);
        formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String largo = jTextField69.getText();
        String ancho = jTextField70.getText();
        String alto = jTextField71.getText() ;
        String tipo_paquete = this.tipo_de_paquete ;
        String peso = jTextField68.getText() ;
        String estado = jComboBox5.getSelectedItem().toString() ;
        String colonia = jTextField61.getText() ;
        String calle = jTextField60.getText() ;
        String no_ext = jTextField62.getText() ;
        String no_int = jTextField63.getText() ;
        String telefono = jTextField66.getText() ;
        String codigo_postal = jTextField65.getText() ;
        String destinatario = jTextField59.getText() ;
        String email = jTextField67.getText() ;
        String municipio = jTextField64.getText() ;
        String asegurado = "false";
        logger.log(Level.INFO, "Vamos a cotizar Fedex para {0}", this.order_actual_id);
        logger.info(largo);
        logger.info(ancho);
        logger.info(alto);
        logger.info(tipo_paquete);
        logger.info(peso);
        logger.info(estado);
        logger.info(colonia);
        logger.info(calle);
        logger.info(no_ext);
        logger.info(no_int);
        logger.info(telefono);
        logger.info(codigo_postal);
        logger.info(destinatario);
        logger.info(email);
        logger.info(municipio);
        if(jCheckBox1.isSelected()){
            asegurado = "true";
        }
        if( !"".equals(largo.replaceAll("\\s+","")) && !"".equals(ancho.replaceAll("\\s+","")) && !"".equals(alto.replaceAll("\\s+","")) && 
            !"".equals(peso.replaceAll("\\s+","")) && !"".equals(estado.replaceAll("\\s+","")) && 
            !"".equals(colonia.replaceAll("\\s+","")) && !"".equals(calle.replaceAll("\\s+","")) && !"".equals(no_ext.replaceAll("\\s+","")) && 
            !"".equals(telefono.replaceAll("\\s+","")) && !"".equals(codigo_postal.replaceAll("\\s+","")) 
            && !"".equals(destinatario.replaceAll("\\s+","")) && !"".equals(email.replaceAll("\\s+","")) && !"".equals(municipio.replaceAll("\\s+","")) ){
            
            try {
                StringBuilder response = request.cotizarPaqex(largo, ancho, alto, tipo_paquete, peso, estado, colonia, calle, no_ext, no_int, telefono, codigo_postal, destinatario, email, municipio, asegurado);
                JSONObject res = new JSONObject(response.toString());
                JSONArray resCotizar = res.getJSONArray("result");
                if(resCotizar.get(0).toString().equals("200")){
                    logger.log(Level.INFO, "Se cotizo bien Fedex {0}", this.order_actual_id);
                    disabledPaquex();
                    formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    this.jLabel121.setText("Costo de la Guia: $"+resCotizar.get(1));
                }else{
                    logger.log(Level.INFO, "Error al cotizar Fedex para{0} {1}", new Object[]{this.order_actual_id, resCotizar.get(1)});
                    formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    cotizarGuia2.setEnabled(true);
                    JOptionPane.showMessageDialog(dialogEtiqueta, resCotizar.get(1), "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                cotizarGuia2.setEnabled(true);
                JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error al cotizar la orden con Paquetexpress", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            cotizarGuia2.setEnabled(true);
            JOptionPane.showMessageDialog(dialogEtiqueta, "Debes completar el formulario", "Alerta", JOptionPane.WARNING_MESSAGE);
        }
       
        
    }//GEN-LAST:event_cotizarGuia2ActionPerformed

    private void ConfirmarGuia2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmarGuia2ActionPerformed
        logger.log(Level.INFO, "Vamos a crear guia para {0}", this.order_actual_id);
        formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ConfirmarGuia2.setEnabled(false);
        String largo = jTextField69.getText();
        String ancho = jTextField70.getText();
        String alto = jTextField71.getText() ;
        String tipo_paquete = this.tipo_de_paquete ;
        String peso = jTextField68.getText() ;
        String estado = jComboBox5.getSelectedItem().toString() ;
        String colonia = jTextField61.getText() ;
        String calle = jTextField60.getText() ;
        String no_ext = jTextField62.getText() ;
        String no_int = jTextField63.getText() ;
        String telefono = jTextField66.getText() ;
        String codigo_postal = jTextField65.getText() ;
        String destinatario = jTextField59.getText() ;
        String email = jTextField67.getText() ;
        String municipio = jTextField64.getText() ;
        String asegurado = "false";
        if(jCheckBox1.isSelected()){
            asegurado = "true";
        }
        if( !"".equals(largo.replaceAll("\\s+","")) && !"".equals(ancho.replaceAll("\\s+","")) && !"".equals(alto.replaceAll("\\s+","")) && 
            !"".equals(peso.replaceAll("\\s+","")) && !"".equals(estado.replaceAll("\\s+","")) && 
            !"".equals(colonia.replaceAll("\\s+","")) && !"".equals(calle.replaceAll("\\s+","")) && !"".equals(no_ext.replaceAll("\\s+","")) && 
            !"".equals(telefono.replaceAll("\\s+","")) && !"".equals(codigo_postal.replaceAll("\\s+","")) 
            && !"".equals(destinatario.replaceAll("\\s+","")) && !"".equals(email.replaceAll("\\s+","")) && !"".equals(municipio.replaceAll("\\s+","")) ){
            
            try {
                if(this.carrier_nombre_existente.equals("AMPM")){
                    logger.log(Level.INFO, "Vamos a crear guia ampm para {0}", this.order_actual_id);
                    String[] responseAmpm = request.crearguiaAmpm(largo, ancho, alto, tipo_paquete, peso, estado, colonia, calle, no_ext, no_int, telefono, codigo_postal, destinatario, email, municipio, this.order_actual_id, this.sesion.getSessionToken());
                    System.out.println(Arrays.toString(responseAmpm));
                    if(responseAmpm[0].equals("200")){
                        logger.info("Se creo bien la guia AMPM");
                        String num_guia = responseAmpm[4];
                        String file_name = responseAmpm[1];
                        String file_url = responseAmpm[2];
                        logger.info(num_guia);
                        logger.info(file_name);
                        logger.info(file_url);
                        logger.info("Vamos a guardar guia");
                        int respuiestaGuia = guardarGuia("Economico", this.order_actual_id, num_guia, this.carrier_nombre_existente, peso, alto, ancho, largo, this.paqueteria_diferente, file_name);
                        if(respuiestaGuia == 200){
                            logger.info("Se guardo bien la guia, vamos a imprimir el PDF");
                            guardarPDF(file_url, file_name);
                            JOptionPane.showMessageDialog(dialogEtiqueta, "La guia se guardo correctamente", "Success", JOptionPane.INFORMATION_MESSAGE);
                            formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            formCrearGuia.setVisible(false);
                            iniciarHacerEnvios();
                        }else{
                            logger.info("Error al guardar la guia");
                            formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            ConfirmarGuia2.setEnabled(true);
                            JOptionPane.showMessageDialog(dialogEtiqueta, "Error al registrar la guia en el Sistema", "Error", JOptionPane.ERROR_MESSAGE);
                        }      
                    }else{
                        logger.log(Level.INFO, "Error al crear AMPM para{0} {1}", new Object[]{this.order_actual_id, responseAmpm[1]});
                        formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        ConfirmarGuia2.setEnabled(true);
                        JOptionPane.showMessageDialog(dialogEtiqueta, responseAmpm[1], "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                if(this.carrier_nombre_existente.equals("Paquetexpress")){
                    logger.log(Level.INFO, "Vamos a crear guia paquetexpress para {0}", this.order_actual_id);
                    StringBuilder response = request.crearguiaPaquex(largo, ancho, alto, tipo_paquete, peso, estado, colonia, calle, no_ext, no_int, telefono, codigo_postal, destinatario, email, municipio, asegurado, this.order_actual_id);
                    if(response != null){
                        JSONObject res = new JSONObject(response.toString());
                        JSONArray resCrear = res.getJSONArray("result");
                        System.out.println(resCrear);
                        if(resCrear.get(0).toString().equals("200")){
                            logger.info("Se creo bien la guia paquex");
                            String num_guia = resCrear.get(3).toString();
                            String file_name = resCrear.get(2).toString();
                            String file_url = resCrear.get(1).toString();
                            logger.info(num_guia);
                            logger.info(file_name);
                            logger.info(file_url);
                            logger.info("Vamos a guardar guia");
                            int respuiestaGuia = guardarGuia("Economico", this.order_actual_id, num_guia, this.carrier_nombre_existente, peso, alto, ancho, largo, this.paqueteria_diferente, file_name);
                            if(respuiestaGuia == 200){
                                guardarPDF(file_url, file_name);
                                JOptionPane.showMessageDialog(dialogEtiqueta, "La guia se guardo correctamente", "Success", JOptionPane.INFORMATION_MESSAGE);
                                iniciarHacerEnvios();
                                formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            }else{
                                logger.info("Error al guardar la guia");
                                ConfirmarGuia2.setEnabled(true);
                                formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                                JOptionPane.showMessageDialog(dialogEtiqueta, "Error al registrar la guia en el Sistema", "Error", JOptionPane.ERROR_MESSAGE);
                            }                    
                        }else{
                            logger.log(Level.INFO, "Error al crear paquex para{0} {1}", new Object[]{this.order_actual_id, resCrear.get(1)});
                            formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            ConfirmarGuia2.setEnabled(true);
                            JOptionPane.showMessageDialog(dialogEtiqueta, resCrear.get(1), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }else{
                        formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        ConfirmarGuia2.setEnabled(true);
                        JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error al crear la guia con "+this.carrier_nombre_existente, "Error", JOptionPane.ERROR_MESSAGE);
                    }    
                }
            } catch (Exception ex) {
                formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                ConfirmarGuia2.setEnabled(true);
                JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error interno al crear la guia con "+this.carrier_nombre_existente, "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            formCrearGuia.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            ConfirmarGuia2.setEnabled(true);
            JOptionPane.showMessageDialog(dialogEtiqueta, "Debes completar el formulario", "Alerta", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_ConfirmarGuia2ActionPerformed

    private void cotizarGuia1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cotizarGuia1ActionPerformed
        formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cotizarGuia1.setEnabled(false);
        this.rate_id = "";
        String largo = jTextField36.getText();
        String ancho = jTextField37.getText();
        String alto = jTextField38.getText() ;
        String tipo_paquete = this.tipo_de_paquete ;
        String tipo_servicio = jComboBox4.getSelectedItem().toString();
        String peso = jTextField72.getText() ;
        String estado = jComboBox3.getSelectedItem().toString() ;
        String seller = jTextField21.getText();
        String line_1 = jTextField30.getText() ;
        String line_2 = jTextField31.getText() ;
        String telefono = jTextField34.getText() ;
        String codigo_postal = jTextField32.getText() ;
        String destinatario = jTextField29.getText() ;
        String ciudad = jTextField33.getText() ;
        String tipo_envio = "STANDARD_OVERNIGHT";
        if(  !"".equals(largo.replaceAll("\\s+","")) && !"".equals(ancho.replaceAll("\\s+","")) && !"".equals(alto.replaceAll("\\s+","")) && 
             !"".equals(peso.replaceAll("\\s+","")) && !"".equals(seller.replaceAll("\\s+","")) && !"".equals(line_1.replaceAll("\\s+","")) && !"".equals(line_2.replaceAll("\\s+","")) && 
             !"".equals(telefono.replaceAll("\\s+","")) && !"".equals(codigo_postal.replaceAll("\\s+","")) && !"".equals(destinatario.replaceAll("\\s+","")) && 
             !"".equals(ciudad.replaceAll("\\s+",""))  ){
            
            if(tipo_servicio.toLowerCase().equals("economico")){
                tipo_envio = "FEDEX_EXPRESS_SAVER";
            }
            try {
                StringBuilder response = request.cotizarFedEx(tipo_envio, largo, ancho, alto, peso, estado, telefono, codigo_postal, destinatario, line_1, line_2, ciudad, this.order_actual_id, this.carrier_nombre_existente);
                JSONObject res = new JSONObject(response.toString());
                JSONArray resCotizar = res.getJSONArray("result");
                if(resCotizar.get(0).toString().equals("200")){
                    disabledFedex();
                    formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    this.jLabel77.setText("$ "+resCotizar.get(1));
                    this.rate_id = resCotizar.get(3).toString();
                }else{
                    formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    cotizarGuia1.setEnabled(true);
                    JOptionPane.showMessageDialog(dialogEtiqueta, resCotizar.get(1), "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                cotizarGuia1.setEnabled(true);
                JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error al cotizar la orden con FedEx", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            cotizarGuia1.setEnabled(true);
            formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(dialogEtiqueta, "Debes completar el formulario", "Alerta", JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_cotizarGuia1ActionPerformed

    private void ConfirmarGuia1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmarGuia1ActionPerformed
        formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String largo = jTextField36.getText();
        String ancho = jTextField37.getText();
        String alto = jTextField38.getText() ;
        String peso = jTextField72.getText() ;
        String tipo_envio = jComboBox4.getSelectedItem().toString();
        try {
            StringBuilder response = request.crearGuiaFedex(this.rate_id, this.order_actual_id, this.carrier_nombre_existente);
            JSONObject res = new JSONObject(response.toString());
            JSONArray resCrear = res.getJSONArray("result");
            System.out.println(resCrear);
            if(resCrear.get(0).toString().equals("200")){
                String num_guia = resCrear.get(1).toString();
                String file_name = resCrear.get(2).toString();
                String file_url = resCrear.get(3).toString();
                String la_pqueteria_new = "FedEx";
                if(this.carrier_nombre_existente.equals("Estafeta")){
                    la_pqueteria_new = "Estafeta";
                }
                int respuiestaGuia = guardarGuia(tipo_envio, this.order_actual_id, num_guia, la_pqueteria_new, peso, alto, ancho, largo, this.paqueteria_diferente, file_name);
                if(respuiestaGuia == 200){
                    guardarPDF(file_url, file_name);
                    JOptionPane.showMessageDialog(dialogEtiqueta, "La guia se guardo correctamente", "Success", JOptionPane.INFORMATION_MESSAGE);
                    iniciarHacerEnvios();
                    formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    formCrearGuiaFedex.setVisible(false);
                }else{formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Error al registrar la guia en el Sistema", "Error", JOptionPane.ERROR_MESSAGE);
                }                    
            }else{
                formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                JOptionPane.showMessageDialog(dialogEtiqueta, resCrear.get(1), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            formCrearGuiaFedex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error al crear la guia FedEx", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ConfirmarGuia1ActionPerformed

    private void willcallBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_willcallBtnActionPerformed
        int respuiestaGuia = guardarGuia("", this.order_actual_id, "will_call", "will_call", "0", "0", "0", "0", this.paqueteria_diferente, "");
        if(respuiestaGuia == 200){
            JOptionPane.showMessageDialog(dialogEtiqueta, "La guia se guardo correctamente", "Success", JOptionPane.INFORMATION_MESSAGE);
            iniciarHacerEnvios();
        }else{
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error al registrar la guia en el Sistema", "Error", JOptionPane.ERROR_MESSAGE);
        }    
    }//GEN-LAST:event_willcallBtnActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        formGuiaExistente.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        jButton9.setEnabled(false);
        String largo = jTextField57.getText();
        String ancho = jTextField56.getText();
        String alto = jTextField55.getText() ;
        String peso = jTextField54.getText() ;
        String carrier_name = carrierNameExistente.getText();
        String num_guia = jTextField53.getText();
        String tipo_envio = tiposEnvio1.getSelectedItem().toString();
        if( !"".equals(largo.replaceAll("\\s+","")) && !"".equals(ancho.replaceAll("\\s+","")) && !"".equals(alto.replaceAll("\\s+","")) && 
            !"".equals(peso.replaceAll("\\s+","")) && !"".equals(carrier_name.replaceAll("\\s+","")) && 
            !"".equals(num_guia.replaceAll("\\s+",""))){
             int respuiestaGuia = guardarGuia(tipo_envio, this.order_actual_id, num_guia, carrier_name, peso, alto, ancho, largo, this.paqueteria_diferente, "");
            
             if(respuiestaGuia == 200){
                JOptionPane.showMessageDialog(dialogEtiqueta, "La guia se guardo correctamente", "Success", JOptionPane.INFORMATION_MESSAGE);
                iniciarHacerEnvios();
                formGuiaExistente.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                formGuiaExistente.setVisible(false);
            }else{
                jButton9.setEnabled(true);
                formGuiaExistente.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                JOptionPane.showMessageDialog(dialogEtiqueta, "Error al registrar la guia en el Sistema", "Error", JOptionPane.ERROR_MESSAGE);
            } 
        }else{
            jButton9.setEnabled(true);
            formGuiaExistente.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(dialogEtiqueta, "Debes completar el formulario", "Alerta", JOptionPane.WARNING_MESSAGE);
        }
          
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (JOptionPane.showConfirmDialog(dialogEtiqueta, "¿Imprimir las etiquetas?", "WARNING",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            imprimirPDF("Etiquetas.pdf");
        } else {
            System.out.println("Sin imprimir");
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField30KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField30KeyTyped
        int caracteres = jTextField30.getText().length();
        if(caracteres > 35){
            jLabel59.setForeground(Color.red);
            jLabel59.setText("Direccion 1 (+35 caracteres)");
        }else{
            jLabel59.setForeground(Color.black);
            jLabel59.setText("Direccion 1");
        }
    }//GEN-LAST:event_jTextField30KeyTyped

    private void jTextField31KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField31KeyTyped
        int caracteres = jTextField31.getText().length();
        if(caracteres > 35){
            jLabel60.setForeground(Color.red);
            jLabel60.setText("Direccion 2 (+35 caracteres)");
        }else{
            jLabel60.setForeground(Color.black);
            jLabel60.setText("Direccion 2");
        }
    }//GEN-LAST:event_jTextField31KeyTyped

    private void jTextField65KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField65KeyTyped
        char validar  = evt.getKeyChar();
        if(!Character.isDigit(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField65KeyTyped

    private void jTextField72KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField72KeyTyped
        char validar  = evt.getKeyChar();
        if(Character.isLetter(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField72KeyTyped

    private void jTextField32KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField32KeyTyped
        char validar  = evt.getKeyChar();
        if(!Character.isDigit(validar)){
            getToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextField32KeyTyped

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String seller = jTextField1.getText();
        String seller_sku = jTextField2.getText();
        String num_orden = jTextField4.getText();
        String orden_mktf = jTextField3.getText();
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        setTable(seller, seller_sku, num_orden, orden_mktf);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        DefaultComboBoxModel termica1 = new DefaultComboBoxModel(new String[] {});
        DefaultComboBoxModel termica2 = new DefaultComboBoxModel(new String[] {});
        DefaultComboBoxModel pdfnormal = new DefaultComboBoxModel(new String[] {});
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Number of print services: " + printServices.length);
        for(PrintService printService : printServices) {
            System.out.println(printService.getName());
            String name = printService.getName();
            termica1.addElement(name);
            termica2.addElement(name);
            pdfnormal.addElement(name);
        }
        
        jComboBox7.setModel(termica1);
        jComboBox2.setModel(termica2);
        jComboBox8.setModel(pdfnormal);
        
        jComboBox7.setSelectedItem(this.impresoraTermica1);
        jComboBox2.setSelectedItem(this.impresoraTermica2);
        jComboBox8.setSelectedItem(this.impresoraPDF);
        
        this.impresorasConfig.setTitle("Generar Guia AMPM");
        this.impresorasConfig.setSize(600, 350);
        this.impresorasConfig.setLocationRelativeTo(null);
        this.impresorasConfig.setAlwaysOnTop (true);
        this.impresorasConfig.setModalityType (ModalityType.APPLICATION_MODAL);
        this.impresorasConfig.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        this.impresoraTermica1 = jComboBox7.getSelectedItem().toString();
        this.impresoraTermica2 = jComboBox2.getSelectedItem().toString();
        this.impresoraPDF = jComboBox8.getSelectedItem().toString();
        System.out.println("Las nuevas impresoras son");
        System.out.println(this.impresoraTermica1);
        System.out.println(this.impresoraTermica2);
        System.out.println(this.impresoraPDF);
        logger.info("Las nuevas impresoras son");
        logger.info(this.impresoraTermica1);
        logger.info(this.impresoraTermica2);
        logger.info(this.impresoraPDF);
        this.impresorasConfig.setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        this.impresorasConfig.setVisible(false); 
    }//GEN-LAST:event_jButton5ActionPerformed

    private void envioTablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_envioTablaMouseClicked
        int column = envioTabla.getColumnModel().getColumnIndexAtX(evt.getX());	
        int row = evt.getY()/envioTabla.getRowHeight();	
        this.row_active = row;
        int rec = this.envioTabla.getSelectedRow();

        if(row < envioTabla.getRowCount() && row >= 0 && column < envioTabla.getColumnCount() && column >= 0 ){	
            Object value = envioTabla.getValueAt(row, column);	
            if(value instanceof JButton){	
                ((JButton)value).doClick();	
                JButton boton = (JButton) value;
                String nombre_btn = boton.getName();
                System.out.println("el boton es "+nombre_btn);
                System.out.println("shipment es "+nombre_btn.replace("cancelar_guia", ""));
                System.out.println(boton.getName());
                if("cancelar_guia".equals(boton.getName())){	
                    System.out.println("Clic en btn calcular row: " + row + ", Column: "+column);
                    logger.log(Level.INFO, "Clic en btn calcular row: {0}, Column: {1}", new Object[]{row, column});
                    logger.info("Vamos a cancelar un Envio");
                }	
            }
            
            if(value instanceof JButton){	
                ((JButton)value).doClick();	
                JButton boton = (JButton) value;	
                if("cancelar_guia".equals(boton.getName())){   
                    String seller_name = envioTabla.getValueAt(row,envioTabla.convertColumnIndexToView(envioTabla.getColumn("Seller").getModelIndex())).toString();
                    String num_order = envioTabla.getValueAt(row,envioTabla.convertColumnIndexToView(envioTabla.getColumn("No. Orden").getModelIndex())).toString();
                    String order_mktf = envioTabla.getValueAt(row,envioTabla.convertColumnIndexToView(envioTabla.getColumn("Orden Marketful").getModelIndex())).toString();
                    if(JOptionPane.showConfirmDialog(dialogEtiqueta, "¿Cancelar la guia de la orden "+num_order+ " de "+seller_name+"?", "WARNING",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        cancelarGuia(num_order);
                    }
                }	
            }
            
        }
    }//GEN-LAST:event_envioTablaMouseClicked
    
    public void cancelarGuia(String order_id){
        
    }
    public void disabledFedex(){
        jTextField36.setEditable(false);
        jTextField37.setEditable(false);
        jTextField38.setEditable(false) ;
        jComboBox4.setEnabled(false);
        jTextField72.setEditable(false) ;
        jComboBox3.setEnabled(false) ;
        jComboBox1.setEnabled(false);
        jTextField21.setEditable(false);
        jTextField30.setEditable(false) ;
        jTextField31.setEditable(false) ;
        jTextField34.setEditable(false) ;
        jTextField32.setEditable(false) ;
        jTextField29.setEditable(false) ;
        jTextField33.setEditable(false) ;
        ConfirmarGuia1.setEnabled(true);
    }
    public void eneabledFedex(){
        jTextField36.setEditable(true);
        jTextField37.setEditable(true);
        jTextField38.setEditable(true) ;
        jComboBox4.setEnabled(true);
        jTextField72.setEditable(true) ;
        jComboBox3.setEnabled(true) ;
        jComboBox1.setEnabled(true);
        jTextField21.setEditable(true);
        jTextField30.setEditable(true) ;
        jTextField31.setEditable(true) ;
        jTextField34.setEditable(true) ;
        jTextField32.setEditable(true) ;
        jTextField29.setEditable(true) ;
        jTextField33.setEditable(true) ;
        ConfirmarGuia1.setEnabled(false);
        cotizarGuia1.setEnabled(true);
        jLabel77.setText("");
    }
    
    public void disabledPaquex(){
        ConfirmarGuia2.setEnabled(true);
        jTextField69.setEditable(false);
        jTextField70.setEditable(false);
        jTextField71.setEditable(false) ;
        jTextField68.setEditable(false) ;
        jComboBox5.setEnabled(false) ;
        jComboBox6.setEnabled(false);
        jTextField61.setEditable(false) ;
        jTextField60.setEditable(false) ;
        jTextField62.setEditable(false) ;
        jTextField63.setEditable(false) ;
        jTextField66.setEditable(false) ;
        jTextField65.setEditable(false) ;
        jTextField59.setEditable(false) ;
        jTextField67.setEditable(false) ;
        jTextField64.setEditable(false) ;
        jTextField58.setEditable(false);
    }
    
    public void enabledPaquex(String carrier){
        jLabel121.setText("");
        jTextField69.setEditable(true);
        jTextField70.setEditable(true);
        jTextField71.setEditable(true) ;
        jTextField68.setEditable(true) ;
        jComboBox5.setEnabled(true) ;
        jComboBox6.setEnabled(true);
        jTextField61.setEditable(true) ;
        jTextField60.setEditable(true) ;
        jTextField62.setEditable(true) ;
        jTextField63.setEditable(true) ;
        jTextField66.setEditable(true) ;
        jTextField65.setEditable(true) ;
        jTextField59.setEditable(true) ;
        jTextField67.setEditable(true) ;
        jTextField64.setEditable(true) ;
        jTextField58.setEditable(true);
        if(carrier.equals("AMPM")){
            ConfirmarGuia2.setEnabled(true);
        }else{
            cotizarGuia2.setEnabled(true);
            ConfirmarGuia2.setEnabled(false);
        }
        
    }
    
    public int guardarGuia(String tipo_envio, String shopi_order_id, String num_guia, String carrier_name, String peso, String alto, String ancho, String largo, Boolean paqueteria_diferente, String file_name){
        int salida = 400;
        try {
            String response = request.guardarGuia(tipo_envio, shopi_order_id, num_guia, carrier_name, peso, alto, ancho, largo, paqueteria_diferente, file_name, this.sesion.getSessionToken());
            System.out.println(response);
            if(response.equals("200")){
                salida = 200;
            }else{
                JOptionPane.showMessageDialog(dialogEtiqueta, response, "Mensaje", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error interno al guardar la guia en Marketful", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return salida;
    }
    
    public void guardarPDF(String url, String file_name){
        System.out.println("Vamos a guardar el pdf");
        System.out.println(url);
        System.out.println(file_name);
        File outGuia = new File("Guias/"+file_name);
        outGuia.getParentFile().mkdirs();
        Download desc = new Download(url, outGuia);
        desc.run();
        imprimirPDFTermica(file_name);
    }
    
    public void guardarPDF4x8(String url, String file_name){
        System.out.println("Vamos a guardar el pdf 4x8");
        System.out.println(url);
        System.out.println(file_name);
        File outGuia = new File("Guias/"+file_name);
        outGuia.getParentFile().mkdirs();
        Download desc = new Download(url, outGuia);
        desc.run();
        imprimirPDFTermica4x8(file_name);
    }
    
    public void imprimirPDFTermica(String file_name){
//        String impresora = "TSC TE200";
//        if(this.sesion.getSessionUserId() == 237){
//            System.out.println("vamos con red");
//            impresora = "\\\\Laptop-h3109let\\tsc te200";
//        }
//        if(this.sesion.getSessionUserId() == 147){
//            System.out.println("vamos con red");
//            impresora = "\\\\Laptop-h3109let\\TSC TE200";
//        }
        String impresora = this.impresoraTermica1;
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Number of print services: " + printServices.length);
        Boolean se_puede = false;
        for(PrintService printService : printServices) {
            System.out.println(printService.getName());
            if (impresora.equals(printService.getName())) {
                se_puede = true;
            }
        }

        if(se_puede){
            System.out.println("Impresora encontrada");
            try {
                PDDocument document = PDDocument.load(new File("Guias/"+file_name));
                
                PrintService myPrintService = findPrintService(impresora);
                
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPageable(new PDFPageable(document));
                try {
                    job.setPrintService(myPrintService);
                } catch (PrinterException ex) {
                    System.out.println("Impresora no conectada");
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Impresora no conectada para imprimir la guia", "Error", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    job.print();
                } catch (PrinterException ex) {
                    System.out.println("No se pudo imprimir");
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Error al imprimir", "Error", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                System.out.println("No se encontro el documento");
                JOptionPane.showMessageDialog(dialogEtiqueta, "No se encontro el archivo PDF", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            JOptionPane.showMessageDialog(dialogEtiqueta, "No se encontro ninguna impresora disponible", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Impresora NOT FOUND");
        }
    }
    
    public void imprimirPDFTermica4x8(String file_name){
//        String impresora = "TSC TE200";
//        if(this.sesion.getSessionUserId() == 237){
//            System.out.println("vamos con red");
//            impresora = "\\\\Laptop-h3109let\\tsc te200";
//        }
//        if(this.sesion.getSessionUserId() == 147){
//            System.out.println("vamos con red");
//            impresora = "\\\\Laptop-h3109let\\TSC TE200";
//        }
        String impresora = this.impresoraTermica2;
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Number of print services: " + printServices.length);
        Boolean se_puede = false;
        for(PrintService printService : printServices) {
            System.out.println(printService.getName());
            if (impresora.equals(printService.getName())) {
                se_puede = true;
            }
        }

        if(se_puede){
           System.out.println("Impresora encontrada");
            try {
                PDDocument document = PDDocument.load(new File("Guias/"+file_name));
                PrintService myPrintService = findPrintService(impresora);
                
                PrinterJob job = PrinterJob.getPrinterJob();
                Paper paper = new Paper();
                paper.setSize(704, 422);
                System.out.println("width es "+paper.getWidth() );
                paper.setImageableArea(-230, 0, paper.getWidth(), paper.getHeight()); // no margins
                
                PageFormat pageFormat = new PageFormat();
                pageFormat.setPaper(paper);
                Book book = new Book();
                book.append(new PDFPrintable(document, Scaling.SHRINK_TO_FIT), pageFormat, document.getNumberOfPages());
                job.setPageable(book);

                try {
                    job.setPrintService(myPrintService);
                } catch (PrinterException ex) {
                    System.out.println("Impresora no conectada");
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Impresora no conectada para imprimir la guia", "Error", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    job.print();
                } catch (PrinterException ex) {
                    System.out.println("No se pudo imprimir");
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Error al imprimir", "Error", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                System.out.println("No se encontro el documento");
                JOptionPane.showMessageDialog(dialogEtiqueta, "No se encontro el archivo PDF", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            JOptionPane.showMessageDialog(dialogEtiqueta, "No se encontro ninguna impresora disponible", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Impresora NOT FOUND");
        }
    }
    
    public void imprimirPDF(String file_name){
//        String impresora = "HP LaserJet M14-M17 PCLmS";
//        String impresora2 = "NP94EC54";
        String impresora = this.impresoraPDF;
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Number of print services: " + printServices.length);
        Boolean se_puede = false;
        for(PrintService printService : printServices) {
            System.out.println(printService.getName());
            if (impresora.equals(printService.getName())) {
                se_puede = true;
            }
        }

        if(se_puede){
            System.out.println("Impresora encontrada");
            try {
                PDDocument document = PDDocument.load(new File("Etiquetas/"+file_name));
                
                PrintService myPrintService = findPrintService(impresora);
                
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPageable(new PDFPageable(document));
                try {
                    job.setPrintService(myPrintService);
                } catch (PrinterException ex) {
                    System.out.println("Impresora no conectada");
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Impresora no conectada para imprimir las etiquetas", "Error", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    job.print();
                } catch (PrinterException ex) {
                    System.out.println("No se pudo imprimir");
                    JOptionPane.showMessageDialog(dialogEtiqueta, "Error al imprimir", "Error", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                System.out.println("No se encontro el documento");
                JOptionPane.showMessageDialog(dialogEtiqueta, "No se encontro el archivo PDF de las etiquetas", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            JOptionPane.showMessageDialog(dialogEtiqueta, "No se encontro ninguna impresora disponible", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Impresora NOT FOUND");
        }
    }
    
    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
    
    public void verificarOrden(String no_paquete){
        this.ordenMTFInput.setText("");
        this.economico = false;
        this.next_day = false;
        try {
            StringBuilder response = request.consultarPaquete(no_paquete);
            JSONObject res = new JSONObject(response.toString());
            JSONArray resPaquete = res.getJSONArray("result");
            System.out.println(resPaquete.length());
            System.out.println(resPaquete);
            System.out.println(resPaquete.get(0));
            String codigor = resPaquete.get(0).toString();
            this.el_pais = res.getString("el_pais");
            System.out.println(el_pais);
            if("200".equals(codigor)){
                this.order_actual_id = no_paquete;
                this.jLabel23.setText("Orden Actual: "+no_paquete);
                this.jLabel16.setText("Paqueteria: "+resPaquete.get(5).toString());
                this.jLabel17.setText("Tipo de envio: "+resPaquete.get(4).toString());
                this.paqueteriaBtn.setEnabled(true);
//                this.consolidadoBtn.setEnabled(true);
                this.willcallBtn.setEnabled(true);
                if("200".equals(resPaquete.get(6).toString())){
                    this.candidato_dostavista = true;
                }
                if("200".equals(resPaquete.get(7).toString())){
                    this.candidato_ampm = true;
                }
                this.tipo_envio_actual = resPaquete.get(4).toString();
                if(this.tipo_envio_actual.equals("economico")){
                    this.economico = true;
                }
                if(this.tipo_envio_actual.equals("dia_siguiente")){
                    this.next_day = true;
                }
                if("guia_termica".equals(resPaquete.get(1).toString())){
//                    if(JOptionPane.showConfirmDialog(dialogEtiqueta, "Esta Orden tiene una guia Cargada ¿Deseas imprimirla?", "WARNING",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
//                        guardarPDF4x8(resPaquete.get(3).toString(), "Guia_termica_"+this.order_actual_id+".pdf");
//                    }
                    JOptionPane.showMessageDialog(dialogEtiqueta,"Esta orden tiene una Guia cargada, asegurate de imprimirla" , "Alerta", JOptionPane.WARNING_MESSAGE);
                }else if("guia_normal".equals(resPaquete.get(1).toString())){
                    JOptionPane.showMessageDialog(dialogEtiqueta,"Esta orden tiene una Guia cargada, asegurate de imprimirla" , "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(dialogEtiqueta,resPaquete.get(1) , "Alerta", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error al consultar la informacion de la Orden.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void verificarGuiaPrepagada(String paqueteria ){
        try {
            StringBuilder response = request.verificarGuiaPrepagada(paqueteria, this.order_actual_id);
            JSONObject res = new JSONObject(response.toString());
            if( "200".equals(res.getString("result"))){
                System.out.println("Todo ok");
                this.paqueteria_diferente = false;
            }else{
                JOptionPane.showMessageDialog(dialogEtiqueta,res.getString("message") , "Alerta", JOptionPane.WARNING_MESSAGE);
                this.paqueteria_diferente = true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error al consultar la informacion de la Orden.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void consultarDireccion(String paqueteria){
        try {
            StringBuilder response = request.consultarDireccion(this.order_actual_id);
            JSONObject res = new JSONObject(response.toString());
            JSONArray resOrden = res.getJSONArray("result");
            System.out.println(resOrden);
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>( this.listaEstados );
            jComboBox5.setModel(model);
            jComboBox3.setModel(model);
            if("AMPM".equals(paqueteria) || "Paquetexpress".equals(paqueteria)){
                JSONArray datosOrden =  (JSONArray) resOrden.get(1);
                System.out.println("Datos orden es");
                System.out.println(datosOrden);
                this.jLabel116.setText("Fecha: "+datosOrden.get(0).toString());
                this.jLabel117.setText("Seller: "+datosOrden.get(1).toString());
                this.jLabel118.setText("Orden Seller: "+datosOrden.get(2).toString());
                this.jLabel119.setText("Orden MKTF: "+datosOrden.get(3).toString());
                this.jLabel120.setText("Cliente: "+datosOrden.get(4).toString());
                this.dirTextArea.setText("Domicilio: "+resOrden.get(0).toString());
                this.dirTextArea.setWrapStyleWord(true);
                this.dirTextArea.setLineWrap(true);
                this.dirTextArea.setEditable(false);
                if("true".equals(resOrden.get(2).toString())){
                    this.jLabel18.setText("Alerta Cambio de direccion!");
                }
                JSONArray datosContacto =  (JSONArray) resOrden.get(8);
                System.out.println("Datos contacto es");
                System.out.println(datosContacto);
                this.jTextField58.setText(datosContacto.get(7).toString());
                this.jTextField59.setText(datosContacto.get(8).toString());
                this.jTextField60.setText(datosContacto.get(0).toString());
                this.jTextField61.setText(datosContacto.get(3).toString());
                this.jTextField62.setText(datosContacto.get(2).toString());
                this.jTextField63.setText(datosContacto.get(1).toString());
                this.jTextField64.setText(datosContacto.get(9).toString());
                this.jTextField65.setText(datosContacto.get(4).toString());
                this.jTextField66.setText(datosContacto.get(5).toString());
                this.jTextField67.setText(datosContacto.get(6).toString());               
            }
            
            if("FedEx".equals(paqueteria)){
                JSONArray datosOrden =  (JSONArray) resOrden.get(1);
                System.out.println(datosOrden);
                this.jLabel71.setText("Fecha: "+datosOrden.get(0).toString());
                this.jLabel72.setText("Seller: "+datosOrden.get(1).toString());
                this.jLabel73.setText("Orden Seller: "+datosOrden.get(2).toString());
                this.jLabel74.setText("Orden MKTF: "+datosOrden.get(3).toString());
                this.jLabel75.setText("Cliente: "+datosOrden.get(4).toString());
                this.jTextArea1.setText("Domicilio: "+resOrden.get(0).toString());
                this.jTextArea1.setWrapStyleWord(true);
                this.jTextArea1.setLineWrap(true);
                this.jTextArea1.setEditable(false);
                if("true".equals(resOrden.get(2).toString())){
                    this.jLabel18.setText("Alerta Cambio de direccion!");
                }
                JSONArray datosContacto =  (JSONArray) resOrden.get(4);
                System.out.println(datosContacto);
                this.jTextField21.setText(datosContacto.get(5).toString());
                this.jTextField29.setText(datosContacto.get(6).toString());
                this.jTextField32.setText(datosContacto.get(2).toString());
                this.jTextField33.setText(datosContacto.get(1).toString());
                this.jTextField34.setText(datosContacto.get(3).toString());
                String line_address1 = resOrden.get(6).toString();
                String line_address2 = resOrden.get(7).toString();
                if(!line_address1.equals("400")){
                    this.jTextField30.setText(line_address1);
                }
                if(!line_address2.equals("400")){
                    this.jTextField31.setText(line_address2);
                }
                
                int caracteres = jTextField30.getText().length();
                if(caracteres > 35){
                    jLabel59.setForeground(Color.red);
                    jLabel59.setText("Direccion 1 (+35 caracteres)");
                }else{
                    jLabel59.setForeground(Color.black);
                    jLabel59.setText("Direccion 1");
                }
                
                caracteres = jTextField31.getText().length();
                if(caracteres > 35){
                    jLabel60.setForeground(Color.red);
                    jLabel60.setText("Direccion 2 (+35 caracteres)");
                }else{
                    jLabel60.setForeground(Color.black);
                    jLabel60.setText("Direccion 2");
                }
            }
            
                String el_estado = resOrden.get(10).toString();
                System.out.println("El estado es "+el_estado);
//                el_estado = "kjwsfnbvs";
                if(model.getIndexOf(el_estado) == -1){
                    System.out.println("No esta");
                    jLabel106.setForeground(Color.red);
                }else{
                    jLabel106.setForeground(Color.black);
                    System.out.println("Si esta");
                    jComboBox5.setSelectedItem(el_estado);
                    jComboBox3.setSelectedItem(el_estado);
                }
                
            if(paqueteria.equals("AMPM")){
                this.cotizarGuia2.setVisible(false);
            }
            if(paqueteria.equals("Paquetexpress")){
                this.cotizarGuia2.setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 201 - Error al consultar la informacion de la Orden.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void consultaPaquetes(){
        DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Otro"});
        jComboBox6.setModel(model);
        jComboBox1.setModel(model);
        try {
            StringBuilder response = request.consultarBoxes();
            JSONObject res = new JSONObject(response.toString());
            JSONArray boxes = res.getJSONArray("result");
            System.out.println(boxes.length());
            System.out.println(boxes);
           
            for (int i = 0; i < boxes.length(); i++) {
                model.addElement(boxes.get(i).toString());
//                jComboBox6.addItem(boxes.get(i).toString());
//                jComboBox1.addItem(boxes.get(i).toString());
//                System.out.println(boxes.get(i).toString());
            }
            jComboBox6.setModel(model);
            jComboBox1.setModel(model);
        } catch (Exception ex) {
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void consultaRates(String paqueteria){
        DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Internacional"});
        jComboBox4.setModel(model);
        tiposEnvio1.setModel(model);
        try {
            StringBuilder response = request.consultarRates(this.order_actual_id, paqueteria);
            JSONObject res = new JSONObject(response.toString());
            JSONArray tipo_envios = res.getJSONArray("result");
            System.out.println(tipo_envios.length());
            System.out.println(tipo_envios);
           
            for (int i = 0; i < tipo_envios.length(); i++) {
                model.addElement(tipo_envios.get(i).toString());

            }
            System.out.println("tipo de envio es "+this.tipo_envio_actual);
            if(this.tipo_envio_actual.equals("dia_siguiente") && model.getIndexOf("Dia Siguiente") == -1){
                model.addElement("Dia Siguiente");
            }
            if(this.tipo_envio_actual.equals("economico") && model.getIndexOf("Economico") == -1){
                model.addElement("Economico");
            }
            
            if(this.next_day && model.getIndexOf("Economico") != -1){
                int el_index = model.getIndexOf("Economico");
                model.removeElementAt(el_index);
                if(model.getIndexOf("Dia Siguiente") != -1){
                    model.setSelectedItem("Dia Siguiente");
                }
            }
            
            if(this.economico && model.getIndexOf("Dia Siguiente") != -1){
                int el_index = model.getIndexOf("Dia Siguiente");
                model.removeElementAt(el_index);
                if(model.getIndexOf("Economico") != -1){
                    model.setSelectedItem("Economico");
                }
            }
            
            if(this.crear_guia_fedex &&  model.getIndexOf("Internacional") != -1){
                int el_indexInter = model.getIndexOf("Internacional");
                model.removeElementAt(el_indexInter);
            }
            
            jComboBox4.setModel(model);
            tiposEnvio1.setModel(model);
        } catch (Exception ex) {
            Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.crear_guia_fedex = false;
    }
    
    public void verificarConsolidado(){
        this.ordenMTFInput.setText("");
        this.paqueteriaBtn.setEnabled(true);
    }
    
    public void iniciarHacerEnvios(){
        this.jLabel23.setText("");
        this.jLabel16.setText("");
        this.jLabel17.setText("");
        
        this.carrier_nombre_existente = "";
        this.paqueteriaBtn.setEnabled(false);
//        this.consolidadoBtn.setEnabled(false);
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
        this.candidato_ampm = false;
        this.candidato_dostavista = false;
        this.paqueteria_diferente = false;
        this.el_pais = "sin_pais";
        this.order_actual_id = null;
        this.rate_id = "";
    }
    public void calcularNuevaPosicion(Integer row){
        tableData.setValueAt("Calculando...", this.row_active, tableData.convertColumnIndexToView(tableData.getColumn("Calcular").getModelIndex()) );
        String response;
        try {
            response = request.cambiarPosicion(this.pedido_id);
            if("200".equals(response)){
               JOptionPane.showMessageDialog(dialogEtiqueta, "Posicion Actualizada", "Succes", JOptionPane.INFORMATION_MESSAGE);
               setTable("","","","");
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
            this.out.getParentFile().mkdirs();
//            new Thread(new Download(this.url_etiquetas, this.out)).start();
            Download desc = new Download(this.url_etiquetas, this.out);
            desc.run();
//            JOptionPane.showMessageDialog(dialogEtiqueta, "URL de Etiquetas es" + this.url_etiquetas, "Success", JOptionPane.INFORMATION_MESSAGE);
            btnGenerarPdf.setEnabled(true);
            donwloadPdf.setEnabled(true);
        }else{
            btnGenerarPdf.setEnabled(true);
            JOptionPane.showMessageDialog(dialogEtiqueta, "Error Code: 102 - Error al obtener el documento PDF de las etiquetas."+respuesta_pdf, "Error", JOptionPane.ERROR_MESSAGE);
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
    private javax.swing.JButton ConfirmarGuia1;
    private javax.swing.JButton ConfirmarGuia2;
    private javax.swing.JButton ampmBtn;
    private javax.swing.JButton btnActualizarPedidos;
    private javax.swing.JButton btnGenerarPdf;
    private javax.swing.JTextField canalFiltro;
    private javax.swing.JButton cancelarSurtido;
    private javax.swing.JButton cancelar_guia;
    private javax.swing.JLabel cantidadSurtida;
    private javax.swing.JTextField carrierNameExistente;
    private javax.swing.JButton consolidadoBtn;
    private javax.swing.JButton cotizarGuia1;
    private javax.swing.JButton cotizarGuia2;
    private javax.swing.JButton crearGuiaBtn;
    private javax.swing.JButton dhlBtn;
    private javax.swing.JDialog dialogEtiqueta;
    private javax.swing.JTextArea dirTextArea;
    private javax.swing.JMenuItem donwloadPdf;
    private javax.swing.JButton enviaBtn;
    private javax.swing.JTable envioTabla;
    private javax.swing.JCheckBox esConsolidadoTextBox;
    private javax.swing.JButton estafetaBtn;
    private javax.swing.JButton fedexBtn;
    private javax.swing.JDialog formConsolidado;
    private javax.swing.JDialog formCrearGuia;
    private javax.swing.JDialog formCrearGuiaFedex;
    private javax.swing.JDialog formGuiaExistente;
    private javax.swing.JButton generarPedidos;
    private javax.swing.JButton guiaExistenteBtn;
    private javax.swing.JDialog impresorasConfig;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
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
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel5;
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
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField38;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private javax.swing.JTextField jTextField53;
    private javax.swing.JTextField jTextField54;
    private javax.swing.JTextField jTextField55;
    private javax.swing.JTextField jTextField56;
    private javax.swing.JTextField jTextField57;
    private javax.swing.JTextField jTextField58;
    private javax.swing.JTextField jTextField59;
    private javax.swing.JTextField jTextField60;
    private javax.swing.JTextField jTextField61;
    private javax.swing.JTextField jTextField62;
    private javax.swing.JTextField jTextField63;
    private javax.swing.JTextField jTextField64;
    private javax.swing.JTextField jTextField65;
    private javax.swing.JTextField jTextField66;
    private javax.swing.JTextField jTextField67;
    private javax.swing.JTextField jTextField68;
    private javax.swing.JTextField jTextField69;
    private javax.swing.JTextField jTextField70;
    private javax.swing.JTextField jTextField71;
    private javax.swing.JTextField jTextField72;
    private javax.swing.JLabel kejgbe;
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
    private javax.swing.JComboBox<String> tiposEnvio1;
    private javax.swing.JLabel totalPages;
    private javax.swing.JButton tracusaBtn;
    private javax.swing.JButton upsBtn;
    private javax.swing.JLabel userWelcome;
    private javax.swing.JButton willcallBtn;
    // End of variables declaration//GEN-END:variables

    private static class AbstractActionImpl extends AbstractAction {

        public AbstractActionImpl() {
        }

        public void actionPerformed(ActionEvent e) {
            //do nothing
        }
    }
}
