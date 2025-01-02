package com.example.streamliner.ui.loginAndRegister;

import java.util.ArrayList;
import java.util.List;
public class EmailRequest {
    private List<Personalization> personalizations;
    private List<Content> content;
    private From from;

    public EmailRequest(String to, String subject, String body, String senderEmail) {
        this.personalizations = new ArrayList<>();
        this.personalizations.add(new Personalization(to, subject));

        this.content = new ArrayList<>();
        this.content.add(new Content(body));

        this.from = new From(senderEmail);
    }

    static class Personalization {
        private List<To> to;
        private String subject;

        public Personalization(String email, String subject) {
            this.to = new ArrayList<>();
            this.to.add(new To(email));
            this.subject = subject;
        }

        static class To {
            private String email;

            public To(String email) {
                this.email = email;
            }
        }
    }

    static class Content {
        private String type = "text/plain";
        private String value;

        public Content(String value) {
            this.value = value;
        }
    }

    static class From {
        private String email;

        public From(String email) {
            this.email = email;
        }
    }

}
