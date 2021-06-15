package com.weather.station.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "telegram_users", schema = "weather")
public class TelegramUser {
    @Id
    private Long userId;

    private Boolean admin;

    private Boolean notifications;

    @ManyToOne
    private Hour weatherSuscribeHour;




    
}
