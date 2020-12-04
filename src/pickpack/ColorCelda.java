/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickpack;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author user
 */
public class ColorCelda extends DefaultTableCellRenderer{
    
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        System.out.println("Value es "+value);
        if(value.equals("Pedido Completado")){
//            System.out.println("Lo vamos a pintar");
            this.setBackground(Color.green);
        }else{
            System.out.println("NO vamos a pintar");
            this.setBackground(Color.white);
        }
        return this;
    }
}
