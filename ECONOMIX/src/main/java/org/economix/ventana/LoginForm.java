package org.economix.ventana;

import com.formdev.flatlaf.FlatDarculaLaf;
import javax.swing.*;

public class LoginForm extends JFrame{
    private JPanel panelPrincipal;
    private JTextField usuarioTexto;
    private JPasswordField passwordTexto;
    private JButton enviarBoton;

    public LoginForm(){
        inicializarForma();
    }

    private void inicializarForma(){
     setContentPane(panelPrincipal);
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setSize(600, 400);
     setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
    }
}
