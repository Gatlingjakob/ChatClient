package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by jakob on 18/02/2017.
 */
public class UDPChatClient extends Application {

    private static InetAddress host;
    private static final int PORT = 1234;
    private static DatagramSocket datagramSocket;
    private static DatagramPacket inPacket, outPacket;
    private static byte[] buffer;
    private static int alive;

    // JavaFX
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UDP Chat Client");

        // Send Button
        Button send = new Button();
        send.setText("Send");
        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("send");
            }
        });
        send.setTranslateX(50);
        send.setTranslateY(50);

        // Quit Button
        Button quit = new Button();
        send.setText("quit");
        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("quit");
            }
        });
        quit.setTranslateX(80);
        quit.setTranslateY(80);

        TextArea showText = new TextArea();
        ScrollPane scrollP = new ScrollPane(showText);
        StackPane root = new StackPane();
        root.getChildren().addAll(scrollP, send,quit);
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

    }

    public static void main(String[] args) {

        // Get IP-Address of Host
        try {
            host = InetAddress.getLocalHost(); //Bruges kun hvis client tilgås fra host-enheden
            //host = InetAddress.getByName("10.111.176.189"); // Jakses macs' private IP-adresse

        }
        catch(UnknownHostException uhEX){
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        accessServer();
    }

    private static void accessServer(){


        try{
            datagramSocket = new DatagramSocket();
            Scanner userEntry = new Scanner(System.in);
            String message="", response="";
            boolean gotUsername = false;
            String username = "blankestspace";
            char[] validInput = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l',
                    'm','n','o','p','q','r','s','t','u','v','w','x','y','z','æ','ø','å',
                    '0','1','2','3','4','5','6','7','8','9','-','_'}; //husk equalsIgnoreCase


            // Check if Username has been successfully generated
            if (!gotUsername) {

                // Check if Username is longer than 12 chars - if not, try again!
                while (username.length() >= 12) { // Skal derudover kunne indeholde tal, underscore, og komma og intet andet!
                    System.out.print("Enter Username (Valid Input: A-Z:, 0-9, -, _): ");
                    username = userEntry.nextLine();
                }

                // "Load" packet by passing it the newly entered Username
                outPacket = new DatagramPacket(username.getBytes(), username.length(), host, PORT);

                // Send Packet in the form of a byte array
                datagramSocket.send(outPacket);
                buffer = new byte[256];
                inPacket = new DatagramPacket(buffer, buffer.length);

                // receive packet containing data from Server
                datagramSocket.receive(inPacket);

                // Convert Server Response to String-type
                response = new String(inPacket.getData(), 0, inPacket.getLength());

                System.out.println(response);
                gotUsername = true;
                username = response;
            }

            do { // See Line 141 for while-condition
                System.out.print("Enter message: ");
                message = userEntry.nextLine();

                // If User doesn't wish to go offline (nuværende alternativ til "quit"-knap)
                if (!message.equals("***CLOSE***")){

                    // "Load" outgoing packet by passing it the newly entered message
                    outPacket = new DatagramPacket(message.getBytes(), message.length(), host, PORT);

                    // Send Packet in the form of a byte array
                    datagramSocket.send(outPacket);
                    buffer = new byte[256];
                    inPacket = new DatagramPacket(buffer, buffer.length);

                    // receive packet containing data from Server
                    datagramSocket.receive(inPacket);

                    // Convert Server Response to String-type
                    response = new String(inPacket.getData(), 0, inPacket.getLength());

                    System.out.println(response );

                }

            }
            // While User hasn't gone Offline (nuværende alternativ til "quit"-knap)
            while (!message.equals("***CLOSE***"));
        }
        catch (IOException ioEx){
            ioEx.printStackTrace();
        }
        finally {
            System.out.println("\n Closing connection... *");
            datagramSocket.close();
        }
    }

}
