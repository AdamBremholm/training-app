package view;


import java.util.Optional;


public class HtmlView {

    public static  String printHtml(Number number, String operation){

        String header ="<html>" +
                "<style>" +
                        "h1 {" +
                        "font-size: 36px;" +
                        "color: " + "blue" + ";" +
                        "}" +
                        "</style>";
        String nonNullOperation = Optional.ofNullable(operation).orElseThrow(IllegalArgumentException::new);
        String message = "<h1>The result of  " + nonNullOperation.toLowerCase() + " was: " + number.toString() + "</h1> </html>";
        return  header + message;
    }
}
