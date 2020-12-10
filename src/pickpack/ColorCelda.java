/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickpack;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author user
 */
public class ColorCelda extends DefaultTableCellRenderer{
    
    String color = "ROSA";
    boolean cambiar_color = false;
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        System.out.println("Value es "+value);
//            System.out.println("row es "+row);
        if(value.equals("Pedido Completado") || value.equals("✔")){
//            System.out.println("Lo vamos a pintar");
            this.setBackground(Color.green);
        }else{
            if(value instanceof JButton){
                JButton btn = (JButton)value;
                return btn;
            }else{
                if(row % 2 == 0){
                    this.setBackground(Color.LIGHT_GRAY);
                    this.setForeground(Color.BLACK);
                }else{
                    this.setBackground(Color.white);
                    this.setForeground(Color.BLACK);
                }
                if(isSelected){
                    this.setBackground(Color.BLUE);
                    this.setForeground(Color.white);
                }
            }
        }
        
        return this;
    }
    
    public boolean evaluar(int row){
        
        return (row % 2 == 0);
    }
}
