package com.keziko.dvdtek.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Nom de classe : Shortfilm
 * @version 27/12/2020
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
@Entity
@Table(name="shortfilm")
public class Shortfilm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="dvd_id", nullable=false)
    private Dvd dvd;

    private String body;

    public Shortfilm(Dvd dvd, String body) {
        this.dvd = dvd;
        this.body = body;
    }
    public Shortfilm(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Shortfilm{" +
                "id=" + id +
                ", dvdId=" + dvd.getId() +
                ", body='" + body + '\'' +
                '}';
    }
}
