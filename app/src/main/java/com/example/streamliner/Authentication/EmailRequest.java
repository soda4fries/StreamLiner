package com.example.streamliner.Authentication;

import java.util.ArrayList;
import java.util.List;
public class EmailRequest {
    private final List<Personalization> personalizations;
    private final List<Content> content;
    private final From from;

    public EmailRequest(String to, String subject, String body, String senderEmail) {
        this.personalizations = new ArrayList<>();
        this.personalizations.add(new Personalization(to, subject));

        this.content = new ArrayList<>();
        this.content.add(new Content(body));

        this.from = new From(senderEmail);
    }

    static class Personalization {
        private final List<To> to;
        private final String subject;

        public Personalization(String email, String subject) {
            this.to = new ArrayList<>();
            this.to.add(new To(email));
            this.subject = subject;
        }

        static class To {
            private final String email;

            public To(String email) {
                this.email = email;
            }
        }
    }

    static class Content {
        private final String type = "text/plain";
        private final String value;

        public Content(String value) {
            this.value = value;
        }
    }

    static class From {
        private final String email;

        public From(String email) {
            this.email = email;
        }
    }

}
