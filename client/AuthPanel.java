/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;


import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;




public class AuthPanel extends Parent {
   TextField userTextField;
   PasswordField pwBox;
   Label userName;
   Label password;
   Button sendBtn;


    public AuthPanel () {
        //////////////////user:
this.userName = new Label("Pseudo :");
userName.setLayoutX(70);
userName.setLayoutY(10);
userName.setPrefHeight(10);
userName.setPrefWidth(50);
this.getChildren().add(userName);


this.userTextField = new TextField();
userTextField.setLayoutX(70);
userTextField.setLayoutY(30);
userTextField.setPrefHeight(10);
userTextField.setPrefWidth(100);
this.getChildren().add(userTextField);

/////////password: 
this.password = new Label("mdp fdp:");
password.setLayoutX(70);
password.setLayoutY(60);
password.setPrefHeight(10);
password.setPrefWidth(50);
this.getChildren().add(password);

this.pwBox = new PasswordField();

pwBox.setLayoutX(70);
pwBox.setLayoutY(80);
pwBox.setPrefHeight(10);
pwBox.setPrefWidth(100);
this.getChildren().add(pwBox);

//////valider:

this.sendBtn = new Button();
sendBtn.setLayoutX(70);
sendBtn.setLayoutY(100);
sendBtn.setPrefHeight(10);
sendBtn.setPrefWidth(100);
sendBtn.setText("Valider");
this.getChildren().add(sendBtn);
        }
    
    
}
        
