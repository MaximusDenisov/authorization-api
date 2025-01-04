package ru.krbk.authorization.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity()
@Table(name = "integrationlog")
public class IntegrationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private LocalDateTime logTime;

    private String body;

    private String statusText;

    private int statusNum;

    public IntegrationLog(
            String body,
            String statusText,
            int statusNum) {
        this.logTime = LocalDateTime.now();
        this.body = body;
        this.statusText = statusText;
        this.statusNum = statusNum;
    }

    public IntegrationLog() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public int getStatusNum() {
        return statusNum;
    }

    public void setStatusNum(int statusNum) {
        this.statusNum = statusNum;
    }
}
