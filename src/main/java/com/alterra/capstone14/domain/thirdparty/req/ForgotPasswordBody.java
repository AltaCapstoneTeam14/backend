package com.alterra.capstone14.domain.thirdparty.req;

public class ForgotPasswordBody {

    private String sender;
    private String baseUrl;

    public ForgotPasswordBody(String sender, String baseUrl){
        this.sender = sender;
        this.baseUrl = baseUrl;
    }

    public String generate(String reciever, String token) {
        String htmlContent = String.format("<html>%s?t=%s</html>", baseUrl, token);

        return String.format(
                "{" +
                        "\"sender\":{" +
                            "\"email\":\"%s\"" +
                        "}," +
                        "\"to\":[" +
                            "{" +
                                "\"email\":\"%s\"" +
                            "}" +
                        "]," +
                        "\"subject\":\"Request Reset Password\","+
                        "\"htmlContent\":\"%s\""+
//                        "\"textContent\":\"%s\""+
                "}", sender, reciever, htmlContent);
    }
}
